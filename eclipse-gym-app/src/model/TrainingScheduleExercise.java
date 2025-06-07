package model;

public class TrainingScheduleExercise {
    private int scheduleId;
    private int exerciseId;
    private int rep;
    private int set;
    private String comment;

    public TrainingScheduleExercise() {
    }

    public TrainingScheduleExercise(int scheduleId, int exerciseId, int rep, int set, String comment) {
        this.scheduleId = scheduleId;
        this.exerciseId = exerciseId;
        this.rep = rep;
        this.set = set;
        this.comment = comment;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}