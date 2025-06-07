package model;

public class Exercise {
    private int exerciseId;
    private String exerciseCode;
    private String exerciseName;
    private String category;
    private String description;

    public Exercise() {
    }

    public Exercise(int exerciseId, String exerciseCode, String exerciseName, String category, String description) {
        this.exerciseId = exerciseId;
        this.exerciseCode = exerciseCode;
        this.exerciseName = exerciseName;
        this.category = category;
        this.description = description;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseCode() {
        return exerciseCode;
    }

    public void setExerciseCode(String exerciseCode) {
        this.exerciseCode = exerciseCode;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}