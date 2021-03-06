package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoTaxfifDictionary {

    @Query("SELECT * FROM TaxfifDictionary")
    List<TaxfifDictionary> getTaxfifDictionaries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<TaxfifDictionary> values);
}
