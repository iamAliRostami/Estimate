package com.leon.estimate.Tables;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoCalculateInfo {

    @Query("SELECT * FROM CalculationInformation ORDER BY TrackingId desc")
    LiveData<List<CalculationInformation>> fetchCalcuateInfo();


    @Query("SELECT * FROM CalculationInformation WHERE TrackingId =:trackingId")
    LiveData<CalculationInformation> getCalcuateInfo(int trackingId);

    @Insert
    Long insertCalculateInfo(CalculationInformation calculationInformation);

    @Update
    void updateCalcuateInfo(CalculationInformation calculationInformation);

    @Delete
    void deleteCalcuateInfo(CalculationInformation calculationInformation);
}
