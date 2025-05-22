package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String fullName;
    private LocalDate dateOfBirth;
    private enum_Gender gender;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private enum_UserStatus status;
    private enum_Role role;

    public User() {
    }

    public User(int userId, String username, String password, String email, String phoneNumber,
            String fullName, LocalDate dateOfBirth, enum_Gender gender, String address,
            LocalDateTime createdAt, LocalDateTime updatedAt, enum_UserStatus status, enum_Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public enum_Gender getGender() {
        return gender;
    }

    public void setGender(enum_Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum_UserStatus getStatus() {
        return status;
    }

    public void setStatus(enum_UserStatus status) {
        this.status = status;
    }

    public enum_Role getRole() {
        return role;
    }

    public void setRole(enum_Role role) {
        this.role = role;
    }
}
