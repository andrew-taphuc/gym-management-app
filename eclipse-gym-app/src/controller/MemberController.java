package controller;

import model.Member;
import model.enums.enum_MemberStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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
}