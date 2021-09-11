package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Formula", indices = @Index(value = {"id"}, unique = true))
public class Formula {
    @PrimaryKey
    public int id;
    public int zoneId;
    public double gozarFrom;
    public double gozarTo;
    public String gozarTitle;
    public double maskooniZ;
    public double tejariZ;
    public double edariDolatiZ;
    public double khadamatiZ;
    public double sanatiZ;
    public double sayerZ;
}
