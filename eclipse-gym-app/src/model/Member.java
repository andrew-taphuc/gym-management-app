package model;

import java.time.LocalDate;
import model.enums.enum_MemberStatus;

public class Member {
    private int memberId;
    private int userId;
    private String memberCode;
    private LocalDate joinDate;
    private enum_MemberStatus status;
    private User user; // Reference to User object

    public Member() {
    }

    public Member(int memberId, int userId, String memberCode, LocalDate joinDate, enum_MemberStatus status) {
        this.memberId = memberId;
        this.userId = userId;
        this.memberCode = memberCode;
        this.joinDate = joinDate;
        this.status = status;
    }

    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public enum_MemberStatus getStatus() {
        return status;
    }

    public void setStatus(enum_MemberStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}