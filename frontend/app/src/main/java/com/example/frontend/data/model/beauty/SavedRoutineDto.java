package com.example.frontend.data.model.beauty;

import java.io.Serializable;

public class SavedRoutineDto implements Serializable {
    private String id;
    private String name;
    private String savedDate;
    private int imageRes;

    public SavedRoutineDto(String id, String name, String savedDate, int imageRes) {
        this.id = id;
        this.name = name;
        this.savedDate = savedDate;
        this.imageRes = imageRes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSavedDate() { return savedDate; }
    public int getImageRes() { return imageRes; }
}
