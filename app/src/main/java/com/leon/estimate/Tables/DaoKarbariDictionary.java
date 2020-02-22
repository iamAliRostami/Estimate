package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoKarbariDictionary {

    @Query("SELECT * FROM KarbariDictionary")
    List<KarbariDictionary> getKarbariDictionary();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<KarbariDictionary> values);
}
