package org.vosk.demo;

import java.util.List;

public class Workout {

    private String workoutName;
    private org.vosk.demo.Exercise exercise;

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public org.vosk.demo.Exercise getExercise() {
        return exercise;
    }

    public void setExercise(org.vosk.demo.Exercise exercise) {
        this.exercise = exercise;
    }

    public List<org.vosk.demo.Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<org.vosk.demo.Exercise> exercises) {
        this.exercises = exercises;
    }

    private List <org.vosk.demo.Exercise> exercises;

    public Workout() {
    }

    public Workout(String workoutName, List<org.vosk.demo.Exercise> exercises ) {
        this.workoutName = workoutName;
        this.exercises = exercises;
    }

    public Workout(String workoutName) {
        this.workoutName = workoutName;
    }

    public Workout(String workoutName, org.vosk.demo.Exercise exercise) {
        this.workoutName = workoutName;
        this.exercise = exercise;
    }

    public Workout(String workoutName, int repCount, int setCount, int weight) {
        this.workoutName = workoutName;
    }

    public String toString() {
        return this.workoutName;
    }

}
