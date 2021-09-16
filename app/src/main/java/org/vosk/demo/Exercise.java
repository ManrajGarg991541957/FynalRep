package org.vosk.demo;

public class Exercise {

    private String exerciseName;
    private String repCount;
    private String setCount;
    private String weight;

    public Exercise(String exerciseName, String repCount, String setCount, String weight) {
        this.exerciseName = exerciseName;
        this.repCount = repCount;
        this.setCount = setCount;
        this.weight = weight;
    }

    public Exercise() {

    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getRepCount() {
        return repCount;
    }

    public void setRepCount(String repCount) {
        this.repCount = repCount;
    }

    public String getSetCount() {
        return setCount;
    }

    public void setSetCount(String setCount) {
        this.setCount = setCount;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

}
