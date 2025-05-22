package model;

import java.time.LocalDate;
import model.enums.enum_MembershipStatus;

public class Membership {
    private int membershipId;
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

    public Membership(int membershipId, int memberId, int planId, LocalDate startDate,
            LocalDate endDate, enum_MembershipStatus status, int paymentId) {
        this.membershipId = membershipId;
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
}