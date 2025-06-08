package model;

public class ExerciseWithDetails {
    private Exercise exercise;
    private TrainingScheduleExercise scheduleExercise;

    public ExerciseWithDetails() {
    }

    public ExerciseWithDetails(Exercise exercise, TrainingScheduleExercise scheduleExercise) {
        this.exercise = exercise;
        this.scheduleExercise = scheduleExercise;
    }

    // Getters and Setters
    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public TrainingScheduleExercise getScheduleExercise() {
        return scheduleExercise;
    }

    public void setScheduleExercise(TrainingScheduleExercise scheduleExercise) {
        this.scheduleExercise = scheduleExercise;
    }

    // Convenience methods để truy cập thông tin exercise
    public int getExerciseId() {
        return exercise != null ? exercise.getExerciseId() : 0;
    }

    public String getExerciseCode() {
        return exercise != null ? exercise.getExerciseCode() : "";
    }

    public String getExerciseName() {
        return exercise != null ? exercise.getExerciseName() : "";
    }

    public String getCategory() {
        return exercise != null ? exercise.getCategory() : "";
    }

    public String getDescription() {
        return exercise != null ? exercise.getDescription() : "";
    }

    // Convenience methods để truy cập thông tin schedule exercise
    public int getSet() {
        return scheduleExercise != null ? scheduleExercise.getSet() : 0;
    }

    public int getRep() {
        return scheduleExercise != null ? scheduleExercise.getRep() : 0;
    }

    public String getComment() {
        return scheduleExercise != null ? scheduleExercise.getComment() : "";
    }

    // Method để format số lượng theo yêu cầu: "X reps x Y"
    public String getQuantityFormatted() {
        return getRep() + " reps x " + getSet();
    }
}