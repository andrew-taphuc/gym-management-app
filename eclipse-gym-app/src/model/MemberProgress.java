package model;

import java.time.LocalDate;

public class MemberProgress {
    private int progressId;
    private int memberId;
    private LocalDate measurementDate;
    private double weight;
    private double height;
    private double bmi;
    private double bodyFatPercentage;
    private double chest;
    private double waist;
    private double hip;
    private double biceps;
    private double thigh;
    private int trainerId;
    private String notes;

    public MemberProgress() {
    }

    public MemberProgress(int progressId, int memberId, LocalDate measurementDate, double weight, double height,
            double bmi, double bodyFatPercentage, double chest, double waist, double hip, double biceps, double thigh,
            int trainerId, String notes) {
        this.progressId = progressId;
        this.memberId = memberId;
        this.measurementDate = measurementDate;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.bodyFatPercentage = bodyFatPercentage;
        this.chest = chest;
        this.waist = waist;
        this.hip = hip;
        this.biceps = biceps;
        this.thigh = thigh;
        this.trainerId = trainerId;
        this.notes = notes;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public double getChest() {
        return chest;
    }

    public void setChest(double chest) {
        this.chest = chest;
    }

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public double getHip() {
        return hip;
    }

    public void setHip(double hip) {
        this.hip = hip;
    }

    public double getBiceps() {
        return biceps;
    }

    public void setBiceps(double biceps) {
        this.biceps = biceps;
    }

    public double getThigh() {
        return thigh;
    }

    public void setThigh(double thigh) {
        this.thigh = thigh;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}