package com.example.frontend.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    LiveData<List<SearchHistoryEntity>> getRecentSearches();

    @Query("SELECT * FROM search_history WHERE normalizedQuery = :normalizedQuery LIMIT 1")
    SearchHistoryEntity getByNormalizedQuery(String normalizedQuery);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistoryEntity history);

    @Update
    void update(SearchHistoryEntity history);
    
    @Query("DELETE FROM search_history WHERE normalizedQuery = :normalizedQuery")
    void deleteByQuery(String normalizedQuery);
    
    @Query("DELETE FROM search_history")
    void clearAll();
}
