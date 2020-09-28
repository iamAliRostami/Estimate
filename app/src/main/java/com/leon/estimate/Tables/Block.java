package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Block", indices = @Index(value = {"id"}, unique = true))
public class Block {
    @PrimaryKey
    public double id;
    public int zoneId;
    public String blockId;
    public double maskooni;
    public double tejari;
    public double edariDolati;
    public double khadamati;
    public double sanati;
    public double sayer;
}
