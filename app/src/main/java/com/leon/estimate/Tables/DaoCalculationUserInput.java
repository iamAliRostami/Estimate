package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoCalculationUserInput {

    @Query("SELECT * FROM CalculationUserInput ORDER BY trackNumber desc")
    List<CalculationUserInput> fetchCalculationUserInput();

    @Query("SELECT * FROM CalculationUserInput WHERE trackNumber =:trackingNumber")
    List<CalculationUserInput> getCalculationUserInput(String trackingNumber);

    @Query("UPDATE CalculationUserInput SET sent = :sent  WHERE trackNumber = :trackNumber")
    int updateCalculationUserInput(boolean sent, String trackNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCalculationUserInput(CalculationUserInput calculationUserInput);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<CalculationUserInput> values);

    @Update
    void updateCalculationUserInput(CalculationUserInput calculationUserInput);

    @Delete
    void deleteCalculationUserInput(CalculationUserInput calculationUserInput);
}
