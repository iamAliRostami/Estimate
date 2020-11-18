package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoZarib {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertZarib(Zarib zarib);

    @Query("SELECT * FROM Zarib WHERE zoneId =:zoneId")
    List<Zarib> getZaribByZoneId(int zoneId);
}
