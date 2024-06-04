package com.example.workout;

import java.io.Serializable;

public class SetData implements Serializable {
    int reps;
    boolean completed;

    public SetData(int reps, boolean completed) {
        this.reps = reps;
        this.completed = completed;
    }

    // Getters and setters if needed
    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}