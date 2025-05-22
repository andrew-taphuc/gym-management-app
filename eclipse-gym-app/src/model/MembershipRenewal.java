package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MembershipRenewal {
    private int renewalId;
    private int membershipId;
    private LocalDate newEndDate;
    private LocalDateTime renewalDate;
    private int paymentId;
    private int staffId;
    private String notes;
    private Membership membership; // Reference to Membership
    private Payment payment; // Reference to Payment
    private Staff staff; // Reference to Staff

    public MembershipRenewal() {
    }

    public MembershipRenewal(int renewalId, int membershipId, LocalDate newEndDate,
            LocalDateTime renewalDate, int paymentId, int staffId, String notes) {
        this.renewalId = renewalId;
        this.membershipId = membershipId;
        this.newEndDate = newEndDate;
        this.renewalDate = renewalDate;
        this.paymentId = paymentId;
        this.staffId = staffId;
        this.notes = notes;
    }

    // Getters and Setters
    public int getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(int renewalId) {
        this.renewalId = renewalId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(LocalDate newEndDate) {
        this.newEndDate = newEndDate;
    }

    public LocalDateTime getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDateTime renewalDate) {
        this.renewalDate = renewalDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}