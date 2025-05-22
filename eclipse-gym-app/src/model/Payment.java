package model;

import java.time.LocalDateTime;
import model.enums.enum_PaymentMethod;
import model.enums.enum_PaymentStatus;

public class Payment {
    private int paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private enum_PaymentMethod paymentMethod;
    private enum_PaymentStatus status;
    private int staffId;
    private String notes;
    private Staff staff; // Reference to Staff

    public Payment() {
    }

    public Payment(int paymentId, double amount, LocalDateTime paymentDate, enum_PaymentMethod paymentMethod,
            enum_PaymentStatus status, int staffId, String notes) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.staffId = staffId;
        this.notes = notes;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public enum_PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(enum_PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public enum_PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(enum_PaymentStatus status) {
        this.status = status;
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

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}