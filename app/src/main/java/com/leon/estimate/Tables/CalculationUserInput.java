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
    //
    public int arse;
    @PrimaryKey(autoGenerate = true)
    int id;
    //
    public String phoneNumber;
    public String mobile;
    public String firstName;
    public String sureName;
    @Ignore
    public List<RequestDictionary> selectedServicesObject;

    @Ignore
    public List<Integer> selectedServices;
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
    boolean sent;
    public String shenasname;
    @Ignore
    int pelak;
    public int resultId;

    public boolean isSent() {
        return sent;
    }

    public CalculationUserInput() {
    }
    @Ignore
    public CalculationUserInput(String trackingId, String trackNumber, int requestType,
                                String parNumber, String billId, String radif, int zoneId,
                                String notificationMobile, int karbariId, int qotrEnsheabId,
                                int noeVagozariId, int taxfifId, String selectedServicesString,
                                String mobile, String firstName, String sureName, int arse,
                                int aianKol, int aianMaskooni, int aianTejari, int sifoon100,
                                int sifoon125, int sifoon150, int sifoon200, int zarfiatQarardadi,
                                int arzeshMelk, int tedadMaskooni, int tedadTejari, int tedadSaier,
                                int tedadTaxfif, String nationalId, boolean ensheabQeireDaem,
                                boolean adamTaxfifAb, boolean adamTaxfifFazelab,
                                String address) {
        this.trackingId = trackingId;
        this.trackNumber = trackNumber;
        this.requestType = requestType;
        this.parNumber = parNumber;
        this.billId = billId;
        this.radif = radif;
        this.neighbourBillId = neighbourBillId;
        this.zoneId = zoneId;
        this.notificationMobile = notificationMobile;
        this.karbariId = karbariId;
        this.qotrEnsheabId = qotrEnsheabId;
        this.noeVagozariId = noeVagozariId;
        this.taxfifId = taxfifId;
        this.selectedServicesString = selectedServicesString;
        this.phoneNumber = phoneNumber;
        this.mobile = mobile;
        this.firstName = firstName;
        this.sureName = sureName;
        this.arse = arse;
        this.aianKol = aianKol;
        this.aianMaskooni = aianMaskooni;
        this.aianTejari = aianTejari;
        this.sifoon100 = sifoon100;
        this.sifoon125 = sifoon125;
        this.sifoon150 = sifoon150;
        this.sifoon200 = sifoon200;
        this.zarfiatQarardadi = zarfiatQarardadi;
        this.arzeshMelk = arzeshMelk;
        this.tedadMaskooni = tedadMaskooni;
        this.tedadTejari = tedadTejari;
        this.tedadSaier = tedadSaier;
        this.tedadTaxfif = tedadTaxfif;
        this.nationalId = nationalId;
        this.identityCode = identityCode;
        this.fatherName = fatherName;
        this.postalCode = postalCode;
        this.ensheabQeireDaem = ensheabQeireDaem;
        this.adamTaxfifAb = adamTaxfifAb;
        this.adamTaxfifFazelab = adamTaxfifFazelab;
        this.address = address;
        this.description = description;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setSelectedServicesString(CalculationUserInputSend calculationUserInput) {
        for (int s : calculationUserInput.selectedServices
        ) {
            selectedServicesString = selectedServicesString.concat(String.valueOf(s)).concat(",");
        }
    }

    public void setNeighbourBillId(String neighbourBillId) {
        this.neighbourBillId = neighbourBillId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<RequestDictionary> setSelectedServices(CalculationUserInput calculationUserInput) {
        String json = calculationUserInput.selectedServicesString;
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<RequestDictionary>>() {
        }.getType();
        return gson.fromJson(json, userListType);
    }
}
