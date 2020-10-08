package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "CalculationUserInput", indices = @Index(value = {"trackNumber"}, unique = true))

public class CalculationUserInput {
    public boolean sent;
    public String trackNumber;
    public String trackingId;
    public String radif;
    public int requestType;
    public String parNumber;
    public String billId;
    public int karbariId;
    public String neighbourBillId;
    public int zoneId;
    public String notificationMobile;
    public String selectedServicesString;
    public int qotrEnsheabId;
    public int noeVagozariId;
    public int taxfifId;
    public int arse;
    public String phoneNumber;
    public String mobile;
    public String firstName;
    public String sureName;
    @Ignore
    public List<RequestDictionary> selectedServicesObject;
    public int aianKol;
    public int aianMaskooni;
    public int aianTejari;
    public int sifoon100;
    public int sifoon125;
    public int sifoon150;
    public int sifoon200;

    public int zarfiatQarardadi;
    public int arzeshMelk;
    public int tedadMaskooni;
    public int tedadTejari;
    public int tedadSaier;
    public int tedadTaxfif;
    public String nationalId;
    public String identityCode;
    public String fatherName;
    public String postalCode;
    public boolean ensheabQeireDaem;
    public boolean adamTaxfifAb;
    public boolean adamTaxfifFazelab;
    public String address;
    public String description;
    @PrimaryKey(autoGenerate = true)
    int id;
    public String shenasname;
    @Ignore
    int pelak;
    public int resultId;
    public double x1, x2, y1, y2;

    public CalculationUserInput() {
    }

    public void setSelectedServicesString(CalculationUserInputSend calculationUserInput) {
        for (int s : calculationUserInput.selectedServices) {
            selectedServicesString = selectedServicesString.concat(String.valueOf(s)).concat(",");
        }
    }

    public ArrayList<RequestDictionary> setSelectedServices(CalculationUserInput calculationUserInput) {
        String json = calculationUserInput.selectedServicesString;
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<RequestDictionary>>() {
        }.getType();
        return gson.fromJson(json, userListType);
    }
}
