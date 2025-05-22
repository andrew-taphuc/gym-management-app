package model;

import model.enums.enum_TrainerSpecialization;
import model.enums.enum_TrainerStatus;

public class Trainer {
    private int trainerId;
    private int userId;
    private String trainerCode;
    private enum_TrainerSpecialization specialization;
    private String bio;
    private double rating;
    private enum_TrainerStatus status;
    private User user; // Reference to User object

    public Trainer() {
    }

    public Trainer(int trainerId, int userId, String trainerCode, enum_TrainerSpecialization specialization,
            String bio, double rating, enum_TrainerStatus status) {
        this.trainerId = trainerId;
        this.userId = userId;
        this.trainerCode = trainerCode;
        this.specialization = specialization;
        this.bio = bio;
        this.rating = rating;
        this.status = status;
    }

    // Getters and Setters
    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTrainerCode() {
        return trainerCode;
    }

    public void setTrainerCode(String trainerCode) {
        this.trainerCode = trainerCode;
    }

    public enum_TrainerSpecialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(enum_TrainerSpecialization specialization) {
        this.specialization = specialization;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public enum_TrainerStatus getStatus() {
        return status;
    }

    public void setStatus(enum_TrainerStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}