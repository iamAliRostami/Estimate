package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoServiceDictionary {

    @Query("SELECT * FROM ServiceDictionary Order By id")
    List<ServiceDictionary> getServiceDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<ServiceDictionary> values);
}
