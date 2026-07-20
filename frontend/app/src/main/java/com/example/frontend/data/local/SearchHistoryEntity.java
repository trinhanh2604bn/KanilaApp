package com.example.frontend.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_history")
public class SearchHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String displayQuery;
    public String normalizedQuery;
    public long timestamp;
    
    public SearchHistoryEntity(String displayQuery, String normalizedQuery, long timestamp) {
        this.displayQuery = displayQuery;
        this.normalizedQuery = normalizedQuery;
        this.timestamp = timestamp;
    }
}
