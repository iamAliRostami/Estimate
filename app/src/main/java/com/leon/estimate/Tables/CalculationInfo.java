package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CalculationInfo {
    @PrimaryKey
    int id;
    String TrackingId;
    String TrackNumber;
    String RequestType;
    String ParNumber;
    String BillId;
    String Radif;
    String NeighbourBillId;
    String ZoneId;
    String CallerId;
    String NotificationMobile;
    //    ArrayList<String> KarbarDictionary;
//    ArrayList<String> QotrEnsheabDictionary;
//    ArrayList<String> NoeVagozariDictionary;
//    ArrayList<String> TaxfifDictionary;
//    ArrayList<String> ServiceDictionary;
    String KarbarDictionary;
    String QotrEnsheabDictionary;
    String NoeVagozariDictionary;
    String TaxfifDictionary;
    String ServiceDictionary;
    String ZoneTitle;
    String IsNewEnsheab;
    //
    String PhoneNumber;
    String Mobile;
    String FirstName;
    String SureName;
    String HasFazelab;
    String FazelabInstallDate;
    String IsFinished;
    String Eshterak;

    String Arse;
    String AianKol;
    String AianMaskooni;
    String AianNonMaskooni;
    String Sifoon100;
    String Sifoon125;
    String Sifoon150;
    String Sifoon200;

    String ZarfiatQarardadi;

    String ArzeshMelk;
    String TedadMaskooni;

    String TedadTejari;
    String TedadSaier;
    String TedadTaxfif;
    String NationalId;
    String IdentityCode;
    String FatherName;
    String PostalCode;
    String Address;
    String Description;
    String AdamTaxfifAb;
    String AdamTaxfifFazelab;
    String IsEnsheabQeirDaem;
    String HasRadif;

    boolean read;
    boolean send;

    public void setTrackingId(String trackingId) {
        TrackingId = trackingId;
    }

    public void setTrackNumber(String trackNumber) {
        TrackNumber = trackNumber;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public void setParNumber(String parNumber) {
        ParNumber = parNumber;
    }

    public void setBillId(String billId) {
        BillId = billId;
    }

    public void setRadif(String radif) {
        Radif = radif;
    }

    public void setNeighbourBillId(String neighbourBillId) {
        NeighbourBillId = neighbourBillId;
    }

    public void setZoneId(String zoneId) {
        ZoneId = zoneId;
    }

    public void setNotificationMobile(String notificationMobile) {
        NotificationMobile = notificationMobile;
    }

    public void setKarbarDictionary(String karbarDictionary) {
        KarbarDictionary = karbarDictionary;
    }

    public void setQotrEnsheabDictionary(String qotrEnsheabDictionary) {
        QotrEnsheabDictionary = qotrEnsheabDictionary;
    }

    public void setNoeVagozariDictionary(String noeVagozariDictionary) {
        NoeVagozariDictionary = noeVagozariDictionary;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setSureName(String sureName) {
        SureName = sureName;
    }

    public void setArse(String arse) {
        Arse = arse;
    }

    public void setAianKol(String aianKol) {
        AianKol = aianKol;
    }

    public void setAianMaskooni(String aianMaskooni) {
        AianMaskooni = aianMaskooni;
    }

    public void setAianNonMaskooni(String aianNonMaskooni) {
        AianNonMaskooni = aianNonMaskooni;
    }

    public void setSifoon100(String sifoon100) {
        Sifoon100 = sifoon100;
    }

    public void setSifoon125(String sifoon125) {
        Sifoon125 = sifoon125;
    }

    public void setSifoon150(String sifoon150) {
        Sifoon150 = sifoon150;
    }

    public void setSifoon200(String sifoon200) {
        Sifoon200 = sifoon200;
    }

    public void setZarfiatQarardadi(String zarfiatQarardadi) {
        ZarfiatQarardadi = zarfiatQarardadi;
    }

    public void setArzeshMelk(String arzeshMelk) {
        ArzeshMelk = arzeshMelk;
    }

    public void setTedadMaskooni(String tedadMaskooni) {
        TedadMaskooni = tedadMaskooni;
    }

    public void setTedadTejari(String tedadTejari) {
        TedadTejari = tedadTejari;
    }

    public void setTedadSaier(String tedadSaier) {
        TedadSaier = tedadSaier;
    }

    public void setTedadTaxfif(String tedadTaxfif) {
        TedadTaxfif = tedadTaxfif;
    }

    public void setNationalId(String nationalId) {
        NationalId = nationalId;
    }

    public void setIdentityCode(String identityCode) {
        IdentityCode = identityCode;
    }
}
