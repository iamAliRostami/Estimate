package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoBlock {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertBlock(Block block);

    @Query("SELECT * FROM Block WHERE zoneId =:zoneId")
    List<Block> getBlockByZoneId(int zoneId);
}
