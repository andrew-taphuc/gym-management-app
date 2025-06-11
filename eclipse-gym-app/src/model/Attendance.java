package model;

import java.time.LocalDateTime;

public class Attendance {
    private int attendanceId;
    private int memberId;
    private int membershipId;
    private LocalDateTime checkInTime;
    private Integer trainingScheduleId; // Có thể null
    private String planName;
    private String trainerName; // Tên huấn luyện viên (nếu có)

    public Attendance() {
    }

    public Attendance(int attendanceId, int memberId, int membershipId, LocalDateTime checkInTime,
            Integer trainingScheduleId, String planName) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.checkInTime = checkInTime;
        this.trainingScheduleId = trainingScheduleId;
        this.planName = planName;
    }

    public Integer getTrainingScheduleId() {
        return trainingScheduleId;
    }

    public void setTrainingScheduleId(Integer trainingScheduleId) {
        this.trainingScheduleId = trainingScheduleId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    // Getter/setter cho các trường khác
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
}