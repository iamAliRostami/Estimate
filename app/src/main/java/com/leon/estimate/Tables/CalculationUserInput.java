package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CalculationUserInput", indices = @Index(value = {"trackNumber"}, unique = true))

public class CalculationUserInput {
    public String trackingId;
    public int trackNumber;
    public int requestType;
    public String parNumber;
    public String billId;
    public int radif;
    public String neighbourBillId;
    public int zoneId;
    public String notificationMobile;
    public int karbariId;
    public int qotrEnsheabId;
    public int noeVagozariId;
    public int taxfifId;
    public String selectedServices;
    //
    public String phoneNumber;
    public String mobile;
    public String firstName;
    public String sureName;
    public int arse;
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
    boolean sent;

    public CalculationUserInput(String trackingId, int trackNumber, int requestType,
                                String parNumber, String billId, int radif, String neighbourBillId,
                                int zoneId, String notificationMobile, int karbariId,
                                int qotrEnsheabId, int noeVagozariId, int taxfifId,
                                String selectedServices, String phoneNumber, String mobile,
                                String firstName, String sureName, int arse, int aianKol,
                                int aianMaskooni, int aianTejari, int sifoon100, int sifoon125,
                                int sifoon150, int sifoon200, int zarfiatQarardadi, int arzeshMelk,
                                int tedadMaskooni, int tedadTejari, int tedadSaier, int tedadTaxfif,
                                String nationalId, String identityCode, String fatherName, String postalCode,
                                boolean ensheabQeireDaem, boolean adamTaxfifAb, boolean adamTaxfifFazelab,
                                String address, String description) {
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
        this.selectedServices = selectedServices;
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

    void setselectedServices(CalculationUserInputSend calculationUserInput) {
        for (String s : calculationUserInput.selectedServices
        ) {
            selectedServices = selectedServices.concat(s).concat(",");
        }
    }
}
