package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoQotrSifoonDictionary {

    @Query("SELECT * FROM QotrSifoonDictionary")
    List<QotrSifoonDictionary> getQotrSifoonDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<QotrSifoonDictionary> values);
}
