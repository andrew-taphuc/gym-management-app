package model;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private String invoiceCode;
    private int paymentId;
    private int memberId;
    private LocalDateTime issueDate;
    private String serviceType;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private Integer createdBy;
    private LocalDateTime createdAt;

    public Invoice() {
    }

    public Invoice(int invoiceId, String invoiceCode, int paymentId, int memberId,
            LocalDateTime issueDate, String serviceType,
            double totalAmount, double discountAmount, double finalAmount,
            Integer createdBy, LocalDateTime createdAt) {
        this.invoiceId = invoiceId;
        this.invoiceCode = invoiceCode;
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.serviceType = serviceType;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId=" + invoiceId +
                ", invoiceCode='" + invoiceCode + '\'' +
                ", paymentId=" + paymentId +
                ", memberId=" + memberId +
                ", issueDate=" + issueDate +
                ", serviceType='" + serviceType + '\'' +
                ", totalAmount=" + totalAmount +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
