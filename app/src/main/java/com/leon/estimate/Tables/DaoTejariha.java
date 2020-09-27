package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoTejariha {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertTejariha(Tejariha tejariha);

    @Query("SELECT * FROM Tejariha WHERE trackNumber =:trackNumber")
    List<Tejariha> getTejarihaByTrackNumber(String trackNumber);


//    @Query("SELECT * FROM Tejariha WHERE trackNumber =:trackNumber")
//    ArrayList<Tejariha> getTejariByTrackNumber(String trackNumber);

    @Query("DELETE FROM Tejariha WHERE id = :id")
    void delete(int id);
}
