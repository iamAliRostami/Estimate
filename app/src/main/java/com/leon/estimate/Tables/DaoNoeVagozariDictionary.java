package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoNoeVagozariDictionary {

    @Query("SELECT * FROM NoeVagozariDictionary")
    List<NoeVagozariDictionary> getNoeVagozariDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<NoeVagozariDictionary> values);
}
