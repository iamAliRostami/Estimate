package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoFormula {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertFormula(Formula formula);

    @Query("SELECT * FROM Formula WHERE zoneId =:zoneId")
    List<Formula> getFormulaByZoneId(int zoneId);
}
