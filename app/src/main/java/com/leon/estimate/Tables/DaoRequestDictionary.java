package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoRequestDictionary {

    @Query("SELECT * FROM RequestDictionary")
    List<RequestDictionary> getRequestDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<RequestDictionary> values);
}
