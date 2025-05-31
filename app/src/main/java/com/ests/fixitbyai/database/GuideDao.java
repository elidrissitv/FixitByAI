package com.ests.fixitbyai.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GuideDao {
    @Insert
    void insert(Guide guide);

    @Query("SELECT * FROM guides")
    LiveData<List<Guide>> getAllGuides();

    @Query("SELECT * FROM guides WHERE id = :id")
    LiveData<Guide> getGuideById(int id);

    @Query("SELECT * FROM guides WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<Guide>> searchGuides(String query);

    @Query("SELECT * FROM guides WHERE category = :category")
    LiveData<List<Guide>> getGuidesByCategory(String category);
} 