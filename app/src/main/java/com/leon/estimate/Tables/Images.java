package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Images", indices = @Index(value = {"imageId"}, unique = true))
public class Images {
    @PrimaryKey(autoGenerate = true)
    int imageId;
    String address;
    String billId;
    String eshterak;
}
