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

    @Query("SELECT * FROM CalculationInfo ORDER BY TrackingId desc")
    LiveData<List<CalculationInfo>> fetchCalcuateInfo();


    @Query("SELECT * FROM CalculationInfo WHERE TrackingId =:trackingId")
    LiveData<CalculationInfo> getCalcuateInfo(int trackingId);

    @Insert
    Long insertCalcuateInfo(CalculationInfo calculationInfo);

    @Update
    void updateCalcuateInfo(CalculationInfo calculationInfo);

    @Delete
    void deleteCalcuateInfo(CalculationInfo calculationInfo);
}
