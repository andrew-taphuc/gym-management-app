package model;

import java.time.LocalDateTime;
import model.enums.enum_PlanStatus;

public class MembershipPlan {
    private int planId;
    private String planCode;
    private String planName;
    private int duration;
    private double price;
    private enum_PlanStatus status;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private int updateFrom;

    public MembershipPlan() {
    }

    public MembershipPlan(int planId, String planCode, String planName, int duration, double price,
            enum_PlanStatus status, String description, LocalDateTime createdDate, LocalDateTime updatedDate,
            int updateFrom) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.duration = duration;
        this.price = price;
        this.status = status;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.updateFrom = updateFrom;
    }

    // Getters and Setters
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public enum_PlanStatus getStatus() {
        return status;
    }

    public void setStatus(enum_PlanStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getUpdateFrom() {
        return updateFrom;
    }

    public void setUpdateFrom(int updateFrom) {
        this.updateFrom = updateFrom;
    }
}