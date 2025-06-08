package controller;
import model.enums.enum_MembershipStatus;
import model.Attendance;
import model.Member;
import model.Membership;
import model.User;
import model.enums.enum_MemberStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class MemberController {
    private Connection connection;

    public MemberController() {
        this.connection = DBConnection.getConnection();
    }

    public Member getMemberByID(int memberId) {
        String query = "SELECT * FROM Members WHERE MemberID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("MemberID"));
                member.setUserId(rs.getInt("UserID"));
                member.setMemberCode(rs.getString("MemberCode"));
                member.setJoinDate(rs.getDate("JoinDate").toLocalDate());
                member.setStatus(enum_MemberStatus.fromValue(rs.getString("Status")));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createMember(int userId) {
        // 1. Lấy mã hội viên mới nhất
        String getLatestCodeQuery = "SELECT MemberCode FROM Members ORDER BY MemberCode DESC LIMIT 1";
        String newMemberCode = "HV0001"; // Mặc định nếu chưa có hội viên nào

        try (PreparedStatement stmt = connection.prepareStatement(getLatestCodeQuery)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String latestCode = rs.getString("MemberCode");
                // Kiểm tra nếu mã cũ không đúng định dạng HVxxxx
                if (latestCode != null && latestCode.startsWith("HV")) {
                    try {
                        // Tách số từ mã cũ và tăng lên 1
                        int number = Integer.parseInt(latestCode.substring(2)) + 1;
                        // Format lại thành chuỗi 4 số
                        newMemberCode = String.format("HV%04d", number);
                    } catch (NumberFormatException e) {
                        // Nếu không parse được số, sử dụng mã mặc định
                        System.out.println("Lỗi định dạng mã hội viên cũ: " + latestCode);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        // 2. Tạo hội viên mới
        String insertQuery = "INSERT INTO Members (UserID, MemberCode, JoinDate, Status) " +
                "VALUES (?, ?, ?, ?::member_status_enum) RETURNING MemberID";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, newMemberCode);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(4, enum_MemberStatus.ACTIVE.getValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int memberId = rs.getInt("MemberID");
                System.out.println("✅ Tạo hội viên thành công! MemberCode: " + newMemberCode);
                return memberId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Member findMemberByCodeOrPhone(String keyword) {
        String query = "SELECT m.*, u.PhoneNumber, u.FullName " +
                       "FROM Members m " +
                       "JOIN Users u ON m.UserID = u.UserID " +
                       "WHERE m.MemberCode = ? OR u.PhoneNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("MemberID"));
                member.setUserId(rs.getInt("UserID"));
                member.setMemberCode(rs.getString("MemberCode"));
                if (rs.getDate("JoinDate") != null) {
                    member.setJoinDate(rs.getDate("JoinDate").toLocalDate());
                }
                member.setStatus(enum_MemberStatus.fromValue(rs.getString("Status")));
                // Nếu cần, có thể set thêm số điện thoại, tên, ...
                // member.setPhoneNumber(rs.getString("PhoneNumber"));
                // member.setFullName(rs.getString("FullName"));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy danh sách các gói tập của hội viên
    public static List<Membership> getMembershipsByMemberId(int memberId) {
        List<Membership> list = new ArrayList<>();
        String query = "SELECT * FROM Memberships WHERE MemberID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Membership m = new Membership();
                m.setMembershipId(rs.getInt("MembershipID"));
                m.setPlanId(rs.getInt("PlanID"));
                m.setMemberId(rs.getInt("MemberID"));
                m.setStartDate(rs.getDate("StartDate").toLocalDate());
                m.setEndDate(rs.getDate("EndDate").toLocalDate());
                m.setStatus(enum_MembershipStatus.fromValue(rs.getString("Status")));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy 5 lần check-in gần nhất
    public static List<Attendance> getRecentAttendance(int memberId, int limit) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, mp.PlanName " +
                       "FROM Attendance a " +
                       "JOIN Memberships m ON a.MembershipID = m.MembershipID " +
                       "JOIN MembershipPlans mp ON m.PlanID = mp.PlanID " +
                       "WHERE a.MemberID = ? ORDER BY a.CheckinTime DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setAttendanceId(rs.getInt("AttendanceID"));
                a.setMemberId(rs.getInt("MemberID"));
                a.setCheckInTime(rs.getTimestamp("CheckinTime").toLocalDateTime());
                a.setMembershipId(rs.getInt("MembershipID"));
                a.setPlanName(rs.getString("PlanName")); // Thêm trường này vào model Attendance
                a.setTrainingScheduleId(rs.getObject("TrainingScheduleID") != null ? rs.getInt("TrainingScheduleID") : null);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm số buổi đã tập trong tháng này
    public static int countAttendanceThisMonth(int memberId, LocalDate now) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Attendance WHERE MemberID = ? AND EXTRACT(MONTH FROM CheckinTime) = ? AND EXTRACT(YEAR FROM CheckinTime) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, now.getMonthValue());
            stmt.setInt(3, now.getYear());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Check-in phòng tập (GYM)
    public static boolean checkinGym(int memberId, int membershipId) {
        String query = "INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, TrainingScheduleID) VALUES (?, ?, NOW(), NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Check-in dịch vụ PT
    public static boolean checkinPT(int memberId, int membershipId, int trainingScheduleId) {
        String query = "INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, TrainingScheduleID) VALUES (?, ?, NOW(), ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            stmt.setInt(3, trainingScheduleId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static int getTodayPTScheduleId(int memberId, int membershipId) {
        String query = "SELECT ScheduleID FROM TrainingSchedule " +
                    "WHERE MemberID = ? AND MembershipID = ? AND DATE(CreatedDate) = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ScheduleID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User getUserById(int userId) {
        String query = "SELECT * FROM Users WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                // Thêm các trường khác nếu cần
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}