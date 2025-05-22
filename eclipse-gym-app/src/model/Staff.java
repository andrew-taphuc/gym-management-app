package model;

import model.enums.enum_StaffStatus;

public class Staff {
    private int staffId;
    private int userId;
    private String staffCode;
    private enum_StaffStatus status;
    private User user; // Reference to User object

    public Staff() {
    }

    public Staff(int staffId, int userId, String staffCode, enum_StaffStatus status) {
        this.staffId = staffId;
        this.userId = userId;
        this.staffCode = staffCode;
        this.status = status;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public enum_StaffStatus getStatus() {
        return status;
    }

    public void setStatus(enum_StaffStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}