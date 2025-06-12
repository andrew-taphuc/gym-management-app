package controller;

import model.Membership;
import model.MembershipPlan;
import model.enums.enum_MembershipStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class MembershipController {
    private Connection connection;

    public MembershipController() {
        this.connection = DBConnection.getConnection();
    }

    public List<Membership> getMembershipsByMemberID(int memberID) {
        List<Membership> memberships = new ArrayList<>();
        String query = "SELECT m.*, mp.planname, mp.plancode, mp.duration, mp.price, mp.description " +
                "FROM Memberships m " +
                "JOIN MembershipPlans mp ON m.planid = mp.planid " +
                "WHERE m.MemberID = ? " +
                "ORDER BY m.MembershipID DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Membership membership = new Membership();
                membership.setMembershipId(rs.getInt("MembershipID"));
                membership.setUserId(rs.getInt("UserID"));
                membership.setMemberId(rs.getInt("MemberID"));
                membership.setPlanId(rs.getInt("PlanID"));
                membership.setStartDate(rs.getDate("StartDate").toLocalDate());
                membership.setEndDate(rs.getDate("EndDate").toLocalDate());
                membership.setStatus(enum_MembershipStatus.fromValue(rs.getString("Status")));
                membership.setPaymentId(rs.getInt("PaymentID"));
                membership.setRenewalTo(rs.getObject("RenewalTo") != null ? rs.getInt("RenewalTo") : null);

                // Tạo và set thông tin gói tập
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDescription(rs.getString("Description"));
                membership.setPlan(plan);

                memberships.add(membership);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberships;
    }

    public List<Membership> getAllMemberships() {
        List<Membership> memberships = new ArrayList<>();
        String query = "SELECT * FROM Memberships ORDER BY MembershipID DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Membership membership = new Membership();
                membership.setMembershipId(rs.getInt("MembershipID"));
                membership.setUserId(rs.getInt("UserID"));
                membership.setMemberId(rs.getInt("MemberID"));
                membership.setPlanId(rs.getInt("PlanID"));
                membership.setStartDate(rs.getDate("StartDate").toLocalDate());
                membership.setEndDate(rs.getDate("EndDate").toLocalDate());
                membership.setStatus(enum_MembershipStatus.fromValue(rs.getString("Status")));
                membership.setPaymentId(rs.getInt("PaymentID"));
                memberships.add(membership);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberships;
    }

    public Membership getMembershipByID(int membershipID) {
        String query = "SELECT * FROM Memberships WHERE MembershipID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, membershipID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Membership membership = new Membership();
                membership.setMembershipId(rs.getInt("MembershipID"));
                membership.setUserId(rs.getInt("UserID"));
                membership.setMemberId(rs.getInt("MemberID"));
                membership.setPlanId(rs.getInt("PlanID"));
                membership.setStartDate(rs.getDate("StartDate").toLocalDate());
                membership.setEndDate(rs.getDate("EndDate").toLocalDate());
                membership.setStatus(enum_MembershipStatus.fromValue(rs.getString("Status")));
                membership.setPaymentId(rs.getInt("PaymentID"));
                return membership;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createMembership(Membership membership) {
        String query = "INSERT INTO Memberships (UserID, MemberID, PlanID, StartDate, EndDate, PaymentID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, membership.getUserId());
            stmt.setInt(2, membership.getMemberId());
            stmt.setInt(3, membership.getPlanId());
            stmt.setDate(4, java.sql.Date.valueOf(membership.getStartDate()));
            stmt.setDate(5, java.sql.Date.valueOf(membership.getEndDate()));
            // stmt.setObject(6, membership.getStatus().getValue());
            stmt.setInt(6, membership.getPaymentId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMembershipStatus(int membershipID, enum_MembershipStatus newStatus) {
        String query = "UPDATE Memberships SET Status = 'Hoạt động' WHERE MembershipID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, membershipID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean extendMembership(int membershipID, int months) {
        String query = "UPDATE Memberships SET EndDate = EndDate + INTERVAL '? months' " +
                "WHERE MembershipID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, months);
            stmt.setInt(2, membershipID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createMembershipRenewal(Membership membership) {
        String sql = "INSERT INTO Memberships (UserID, MemberID, PlanID, StartDate, EndDate, PaymentID, RenewalTo, RenewalDate, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::membership_status_enum)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, membership.getUserId());
            pstmt.setInt(2, membership.getMemberId());
            pstmt.setInt(3, membership.getPlanId());
            pstmt.setDate(4, java.sql.Date.valueOf(membership.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(membership.getEndDate()));
            pstmt.setInt(6, membership.getPaymentId());
            pstmt.setInt(7, membership.getRenewalTo());
            pstmt.setTimestamp(8, java.sql.Timestamp.valueOf(membership.getRenewalDate()));
            pstmt.setString(9, model.enums.enum_MembershipStatus.ACTIVE.getValue());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRenewalDate(int membershipId, LocalDateTime renewalDate) {
        String sql = "UPDATE Memberships SET RenewalDate = ? WHERE MembershipID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(renewalDate));
            pstmt.setInt(2, membershipId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra xem một membership đã được gia hạn chưa
     * 
     * @param membershipId ID của membership cần kiểm tra
     * @return true nếu đã được gia hạn, false nếu chưa
     */
    public boolean isMembershipAlreadyRenewed(int membershipId) {
        String query = "SELECT COUNT(*) FROM Memberships WHERE RenewalTo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, membershipId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean activateMembership(int membershipId) {
        String sql = "UPDATE Memberships SET Status = ?::membership_status_enum WHERE MembershipID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, enum_MembershipStatus.ACTIVE.getValue());
            stmt.setInt(2, membershipId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Membership getCurrentMembershipInfo(int memberId) {
        String sql = """
                    SELECT m.MembershipID, m.MemberID, m.PlanID, m.StartDate, m.EndDate, m.Status, m.PaymentID,
                           mp.PlanName, mp.Duration, mp.Price,
                           COALESCE(tr.SessionsLeft, 0) as SessionsLeft
                    FROM Memberships m
                    JOIN MembershipPlans mp ON m.PlanID = mp.PlanID
                    LEFT JOIN (
                        SELECT MemberID, SessionsLeft
                        FROM TrainingRegistrations
                        WHERE MemberID = ?
                        ORDER BY StartDate DESC
                        LIMIT 1
                    ) tr ON tr.MemberID = m.MemberID
                    WHERE m.MemberID = ? AND m.Status = 'Hoạt động'
                    ORDER BY m.EndDate DESC
                    LIMIT 1
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Membership membership = new Membership();
                membership.setMembershipId(rs.getInt("MembershipID"));
                membership.setMemberId(rs.getInt("MemberID"));
                membership.setPlanId(rs.getInt("PlanID"));
                membership.setStartDate(rs.getDate("StartDate").toLocalDate());
                membership.setEndDate(rs.getDate("EndDate").toLocalDate());
                membership.setStatus(model.enums.enum_MembershipStatus.fromValue(rs.getString("Status")));
                membership.setPaymentId(rs.getInt("PaymentID"));
                
                // Set plan information
                model.MembershipPlan plan = new model.MembershipPlan();
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                membership.setPlan(plan);
                
                return membership;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin gói tập: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
