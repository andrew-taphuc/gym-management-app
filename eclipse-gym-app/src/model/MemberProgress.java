package model;

import java.time.LocalDate;

public class MemberProgress {
    private int progressId;
    private int memberId;
    private LocalDate measurementDate;
    private Double weight;
    private Double height;
    private Double bmi;
    private Double bodyFatPercentage;
    private Double chest;
    private Double waist;
    private Double hip;
    private Double biceps;
    private Double thigh;
    private Integer trainerId;
    private String notes;
    private String status;

    public MemberProgress() {
    }

    public MemberProgress(int progressId, int memberId, LocalDate measurementDate, Double weight, Double height,
            Double bmi, Double bodyFatPercentage, Double chest, Double waist, Double hip,
            Double biceps, Double thigh,
            Integer trainerId, String notes) {
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(Double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Double getChest() {
        return chest;
    }

    public void setChest(Double chest) {
        this.chest = chest;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public Double getHip() {
        return hip;
    }

    public void setHip(Double hip) {
        this.hip = hip;
    }

    public Double getBiceps() {
        return biceps;
    }

    public void setBiceps(Double biceps) {
        this.biceps = biceps;
    }

    public Double getThigh() {
        return thigh;
    }

    public void setThigh(Double thigh) {
        this.thigh = thigh;
    }

    public Integer getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Integer trainerId) {
        this.trainerId = trainerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}