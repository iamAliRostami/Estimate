package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoQotrEnsheabDictionary {
    @Query("SELECT * FROM QotrEnsheabDictionary")
    List<QotrEnsheabDictionary> getQotrEnsheabDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<QotrEnsheabDictionary> values);
}
