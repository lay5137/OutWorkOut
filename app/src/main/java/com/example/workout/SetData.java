package com.example.workout;

import java.io.Serializable;

public class SetData implements Serializable {
    String reps;
    boolean completed;

    public SetData(String reps, boolean completed) {
        this.reps = reps;
        this.completed = completed;
    }

    // Getters and setters if needed
    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
