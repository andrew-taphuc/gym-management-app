package model;

public class TrainingScheduleExercise {
    private int scheduleId;
    private int exerciseId;
    private String comment;

    public TrainingScheduleExercise() {
    }

    public TrainingScheduleExercise(int scheduleId, int exerciseId, String comment) {
        this.scheduleId = scheduleId;
        this.exerciseId = exerciseId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}