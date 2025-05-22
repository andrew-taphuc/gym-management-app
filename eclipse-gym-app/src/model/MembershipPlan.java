package model;

public class MembershipPlan {
    private int planId;
    private String planCode;
    private String planName;
    private int duration;
    private double price;
    private String description;

    public MembershipPlan() {
    }

    public MembershipPlan(int planId, String planCode, String planName, int duration, double price,
            String description) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.duration = duration;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}