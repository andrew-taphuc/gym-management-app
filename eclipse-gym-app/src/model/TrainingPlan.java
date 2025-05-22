package model;

import model.enums.enum_TrainerSpecialization;

public class TrainingPlan {
    private int planId;
    private String planCode;
    private String planName;
    private enum_TrainerSpecialization type;
    private int sessionAmount;
    private double price;
    private String description;

    public TrainingPlan() {
    }

    public TrainingPlan(int planId, String planCode, String planName, enum_TrainerSpecialization type,
            int sessionAmount, double price, String description) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.type = type;
        this.sessionAmount = sessionAmount;
        this.price = price;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}