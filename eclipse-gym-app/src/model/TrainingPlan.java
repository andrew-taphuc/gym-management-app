package model;

import java.time.LocalDateTime;
import model.enums.enum_TrainerSpecialization;
import model.enums.enum_PlanStatus;

public class TrainingPlan {
    private int planId;
    private String planCode;
    private String planName;
    private enum_TrainerSpecialization type;
    private int sessionAmount;
    private double price;
    private enum_PlanStatus status;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private int updateFrom;

    public TrainingPlan() {
    }

    public TrainingPlan(int planId, String planCode, String planName, enum_TrainerSpecialization type,
            int sessionAmount, double price, enum_PlanStatus status, String description,
            LocalDateTime createdDate, LocalDateTime updatedDate, int updateFrom) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.type = type;
        this.sessionAmount = sessionAmount;
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

    public enum_TrainerSpecialization getType() {
        return type;
    }

    public void setType(enum_TrainerSpecialization type) {
        this.type = type;
    }

    public int getSessionAmount() {
        return sessionAmount;
    }

    public void setSessionAmount(int sessionAmount) {
        this.sessionAmount = sessionAmount;
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