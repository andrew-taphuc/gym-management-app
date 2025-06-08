package model;

import java.time.LocalDate;
import model.enums.enum_MembershipStatus;

public class Membership {
    private int membershipId;
    private int userId;
    private int memberId;
    private int planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private enum_MembershipStatus status;
    private int paymentId;
    private Member member; // Reference to Member
    private MembershipPlan plan; // Reference to MembershipPlan
    private Payment payment; // Reference to Payment

    public Membership() {
    }

    public Membership(int membershipId, int userId, int memberId, int planId, LocalDate startDate,
            LocalDate endDate, enum_MembershipStatus status, int paymentId) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.memberId = memberId;
        this.planId = planId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.paymentId = paymentId;
    }

    // Getters and Setters
    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public enum_MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(enum_MembershipStatus status) {
        this.status = status;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    public boolean isPersonalTraining() {
        if (plan != null) {
            return plan.getPlanName() != null && plan.getPlanName().toLowerCase().contains("pt");
        }
        return false;
    }

    public boolean isActive() {
        return status == enum_MembershipStatus.ACTIVE && (endDate == null || endDate.isAfter(LocalDate.now()));
    }

    public String getPlanName() {
        // Nếu đã có đối tượng plan, lấy trực tiếp
        if (plan != null && plan.getPlanName() != null) {
            return plan.getPlanName();
        }
        // Nếu chưa có, truy vấn từ DB theo planId
        String planName = "";
        try {
            java.sql.Connection conn = utils.DBConnection.getConnection();
            String sql = "SELECT PlanName FROM MembershipPlans WHERE PlanID = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, this.planId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        planName = rs.getString("PlanName");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return planName;
    }
}