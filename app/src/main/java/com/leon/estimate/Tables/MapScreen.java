package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "MapScreen", indices = @Index(value = {"id"}, unique = true))
public class MapScreen {
    @PrimaryKey(autoGenerate = true)
    int id;
    String billId;
    byte[] bitmap;
}
