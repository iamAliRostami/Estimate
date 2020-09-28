package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Zarib", indices = @Index(value = {"id"}, unique = true))
public class Zarib {
    @PrimaryKey
    public double id;
    public int zoneId;
    public double maskkoniZ1;
    public double tejariZ1;
    public double edariDolatiZ1;
    public double khadamatiZ1;
    public double sanatiZ1;
    public double sayerZ1;
    public double maskooniZ2;
    public double tejariZ2;
    public double edariDolatiZ2;
    public double khadamatiZ2;
    public double sanatiZ2;
    public double sayerZ2;
}
