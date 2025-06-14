package controller;

import model.enums.enum_MembershipStatus;
import model.Attendance;
import model.Member;
import model.Membership;
import model.User;
import model.enums.enum_MemberStatus;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class MemberController extends AbstractMemberController {
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
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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


    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT m.*, u.*, m.Status as MemberStatus, u.Status as UserStatus " +
                    "FROM Members m JOIN Users u ON m.UserID = u.UserID";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("MemberID"));
                member.setUserId(rs.getInt("UserID"));
                member.setMemberCode(rs.getString("MemberCode"));
                member.setJoinDate(rs.getDate("JoinDate").toLocalDate());
                member.setStatus(enum_MemberStatus.fromValue(rs.getString("MemberStatus")));

                // Tạo đối tượng User và set đầy đủ thông tin
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEmail(rs.getString("Email"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setFullName(rs.getString("FullName"));
                user.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                user.setGender(enum_Gender.fromValue(rs.getString("Gender")));
                user.setAddress(rs.getString("Address"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("UpdateAt").toLocalDateTime());
                user.setStatus(enum_UserStatus.fromValue(rs.getString("UserStatus")));
                user.setRole(enum_Role.fromValue(rs.getString("Role")));
                
                member.setUser(user);
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * Lấy ID của member từ UserID
     * 
     * @param userId ID của user
     * @return ID của member, hoặc null nếu không tìm thấy
     */
    public Integer getMemberIdByUserId(int userId) {
        String sql = "SELECT MemberID FROM Members WHERE UserID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("MemberID");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn MemberID từ UserID: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Không tìm thấy hoặc lỗi xảy ra
    }
}
