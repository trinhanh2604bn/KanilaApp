package com.example.frontend.data.model.beauty;

import java.io.Serializable;

public class SavedRoutineDto implements Serializable {
    private String id;
    private String name;
    private long savedTimestamp; // Store as timestamp for relative time calculation
    private int imageRes;

    public SavedRoutineDto(String id, String name, long savedTimestamp, int imageRes) {
        this.id = id;
        this.name = name;
        this.savedTimestamp = savedTimestamp;
        this.imageRes = imageRes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public long getSavedTimestamp() { return savedTimestamp; }
    public int getImageRes() { return imageRes; }

    public void setName(String name) { this.name = name; }
    public void setSavedTimestamp(long savedTimestamp) { this.savedTimestamp = savedTimestamp; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }
}
