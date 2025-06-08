package model;

import java.time.LocalDateTime;
import model.enums.enum_PaymentStatus;

public class Payment {
    private int paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private enum_PaymentStatus status;
    private Integer promotionId;
    private String notes;

    public Payment() {
    }

    public Payment(int paymentId, double amount, LocalDateTime paymentDate, String paymentMethod,
            enum_PaymentStatus status,Integer promotionId, String notes) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.promotionId = promotionId; // Default value, can be set later
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public enum_PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(enum_PaymentStatus status) {
        this.status = status;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}