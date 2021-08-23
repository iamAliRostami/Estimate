package com.leon.estimate.Tables;

import static com.leon.estimate.Utils.Constants.calculationUserInput;
import static com.leon.estimate.Utils.Constants.calculationUserInputTemp;
import static com.leon.estimate.Utils.Constants.examinerDuties;

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
    public boolean ready;
    public String trackNumber;
    public String trackingId;
    public String radif;
    public int requestType;
    public String parNumber;
    public String billId;
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
    public int aianKol;
    public int aianMaskooni;
    public int aianTejari;
    public int sifoon100;
    public int sifoon125;
    public int sifoon150;
    public int sifoon200;

    public int zarfiatQarardadi;
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
    public String shenasname;
    @Ignore
    public List<RequestDictionary> selectedServicesObject;
    @Ignore
    public double x3, y3;
    public int resultId;
    public double x1, x2, y1, y2;
    public double accuracy;
    public int arzeshMelk;
    public int karbariId;
    @PrimaryKey(autoGenerate = true)
    int id;
    @Ignore
    int pelak;

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

    public void prepareCalculationFromPersonal() {
        calculationUserInput.nationalId = calculationUserInputTemp.nationalId;
        calculationUserInput.firstName = calculationUserInputTemp.firstName;
        calculationUserInput.sureName = calculationUserInputTemp.sureName;
        calculationUserInput.fatherName = calculationUserInputTemp.fatherName;
        calculationUserInput.postalCode = calculationUserInputTemp.postalCode;
        calculationUserInput.radif = calculationUserInputTemp.radif;
        calculationUserInput.phoneNumber = calculationUserInputTemp.phoneNumber;
        calculationUserInput.mobile = calculationUserInputTemp.mobile;
        calculationUserInput.address = calculationUserInputTemp.address;
        calculationUserInput.description = calculationUserInputTemp.description;
        calculationUserInput.shenasname = calculationUserInputTemp.shenasname;
        calculationUserInput.zoneId = Integer.parseInt(examinerDuties.getZoneId());
    }

    public void fillCalculationUserInput() {
        calculationUserInput.trackingId = examinerDuties.getTrackingId();
        calculationUserInput.requestType = Integer.parseInt(examinerDuties.getRequestType());
        calculationUserInput.parNumber = examinerDuties.getParNumber();
        calculationUserInput.billId = examinerDuties.getBillId();
        calculationUserInput.neighbourBillId = examinerDuties.getNeighbourBillId();
        calculationUserInput.notificationMobile = examinerDuties.getNotificationMobile();
        calculationUserInput.identityCode = examinerDuties.getIdentityCode();
        calculationUserInput.trackNumber = examinerDuties.getTrackNumber();
        calculationUserInput.sent = false;
    }

    public void prepareCalculationUserInputFromForm() {
        calculationUserInput.sifoon100 = calculationUserInputTemp.sifoon100;
        calculationUserInput.sifoon125 = calculationUserInputTemp.sifoon125;
        calculationUserInput.sifoon150 = calculationUserInputTemp.sifoon150;
        calculationUserInput.sifoon200 = calculationUserInputTemp.sifoon200;
        calculationUserInput.arse = calculationUserInputTemp.arse;
        calculationUserInput.aianKol = calculationUserInputTemp.aianKol;
        calculationUserInput.aianMaskooni = calculationUserInputTemp.aianMaskooni;
        calculationUserInput.aianTejari = calculationUserInputTemp.aianTejari;
        calculationUserInput.tedadMaskooni = calculationUserInputTemp.tedadMaskooni;
        calculationUserInput.tedadTejari = calculationUserInputTemp.tedadTejari;
        calculationUserInput.tedadSaier = calculationUserInputTemp.tedadSaier;
        calculationUserInput.arzeshMelk = calculationUserInputTemp.arzeshMelk;
        calculationUserInput.tedadTaxfif = calculationUserInputTemp.tedadTaxfif;
        calculationUserInput.zarfiatQarardadi = calculationUserInputTemp.zarfiatQarardadi;
        calculationUserInput.parNumber = calculationUserInputTemp.parNumber;
        calculationUserInput.karbariId = calculationUserInputTemp.karbariId;
        calculationUserInput.noeVagozariId = calculationUserInputTemp.noeVagozariId;
        calculationUserInput.qotrEnsheabId = calculationUserInputTemp.qotrEnsheabId;
        calculationUserInput.taxfifId = calculationUserInputTemp.taxfifId;
        calculationUserInput.adamTaxfifAb = calculationUserInputTemp.adamTaxfifAb;
        calculationUserInput.adamTaxfifFazelab = calculationUserInputTemp.adamTaxfifFazelab;
        calculationUserInput.ensheabQeireDaem = calculationUserInputTemp.ensheabQeireDaem;
    }
}
