package model;

import java.time.LocalDateTime;

public class Attendance {
    private int attendanceId;
    private int memberId;
    private int membershipId;
    private LocalDateTime checkInTime;
    private String type; // Gym, Yoga, Kickfit, ...

    public Attendance() {
    }

    public Attendance(int attendanceId, int memberId, int membershipId, LocalDateTime checkInTime, String type) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.checkInTime = checkInTime;
        this.type = type;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}