package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoMapScreen {

    @Query("SELECT * FROM MapScreen")
    List<MapScreen> getMapScreen();
}