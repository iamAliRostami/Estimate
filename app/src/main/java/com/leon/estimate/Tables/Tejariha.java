package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Tejariha", indices = @Index(value = {"id"}, unique = true))
public class Tejariha {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String karbari;
    public String noeShoql;
    public int tedadVahed;
    public String vahedMohasebe;
    public String tozihat;
    public String a;
    public String trackNumber;

    public Tejariha(String karbari, String noeShoql, int tedadVahed, String vahedMohasebe, String a,
                    String trackNumber) {
        this.karbari = karbari;
        this.noeShoql = noeShoql;
        this.tedadVahed = tedadVahed;
        this.vahedMohasebe = vahedMohasebe;
        this.a = a;
        this.trackNumber = trackNumber;
    }
}
