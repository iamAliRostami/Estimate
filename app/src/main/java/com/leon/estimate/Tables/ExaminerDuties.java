package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "ExaminerDuties", indices = @Index(value = {"trackNumber", "id"}, unique = true))
public class ExaminerDuties {
    @PrimaryKey(autoGenerate = true)
    int id;
    String trackNumber;
    String examinationId;
    String karbariId;
    String radif;
    String billId;
    String examinationDay;
    String nameAndFamily;
    String moshtarakMobile;
    String notificationMobile;
    String serviceGroup;
    String address;
    String neighbourBillId;
    boolean isPeymayesh;
    String trackingId;
    String requestType;
    String parNumber;
    String zoneId;
    String callerId;
    String zoneTitle;
    boolean isNewEnsheab;
    String phoneNumber;
    String mobile;
    String firstName;
    String sureName;
    boolean hasFazelab;
    String fazelabInstallDate;
    boolean isFinished;
    String eshterak;
    int arse;
    int aianKol;
    int aianMaskooni;
    int aianNonMaskooni;
    int qotrEnsheabId;
    int sifoon100;
    int sifoon125;
    int sifoon150;
    int sifoon200;
    int zarfiatQarardadi;
    int arzeshMelk;
    int tedadMaskooni;
    int tedadTejari;
    int tedadSaier;
    int taxfifId;
    int tedadTaxfif;
    String nationalId;
    String identityCode;
    String fatherName;
    String postalCode;
    String description;
    boolean adamTaxfifAb;
    boolean adamTaxfifFazelab;
    boolean isEnsheabQeirDaem;
    boolean hasRadif;
    String requestDictionaryString;
    @Ignore
    ArrayList<RequestDictionary> requestDictionary;

    public String getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(String examinationId) {
        this.examinationId = examinationId;
    }

    public String getKarbariId() {
        return karbariId;
    }

    public void setKarbariId(String karbariId) {
        this.karbariId = karbariId;
    }

    public String getRadif() {
        return radif;
    }

    public void setRadif(String radif) {
        this.radif = radif;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getExaminationDay() {
        return examinationDay;
    }

    public void setExaminationDay(String examinationDay) {
        this.examinationDay = examinationDay;
    }

    public String getNameAndFamily() {
        return nameAndFamily;
    }

    public void setNameAndFamily(String nameAndFamily) {
        this.nameAndFamily = nameAndFamily;
    }

    public String getMoshtarakMobile() {
        return moshtarakMobile;
    }

    public void setMoshtarakMobile(String moshtarakMobile) {
        this.moshtarakMobile = moshtarakMobile;
    }

    public String getNotificationMobile() {
        return notificationMobile;
    }

    public void setNotificationMobile(String notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighbourBillId() {
        return neighbourBillId;
    }

    public void setNeighbourBillId(String neighbourBillId) {
        this.neighbourBillId = neighbourBillId;
    }

    public boolean isPeymayesh() {
        return isPeymayesh;
    }

    public void setPeymayesh(boolean peymayesh) {
        isPeymayesh = peymayesh;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getParNumber() {
        return parNumber;
    }

    public void setParNumber(String parNumber) {
        this.parNumber = parNumber;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }

    public void setZoneTitle(String zoneTitle) {
        this.zoneTitle = zoneTitle;
    }

    public boolean isNewEnsheab() {
        return isNewEnsheab;
    }

    public void setNewEnsheab(boolean newEnsheab) {
        isNewEnsheab = newEnsheab;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public boolean isHasFazelab() {
        return hasFazelab;
    }

    public void setHasFazelab(boolean hasFazelab) {
        this.hasFazelab = hasFazelab;
    }

    public String getFazelabInstallDate() {
        return fazelabInstallDate;
    }

    public void setFazelabInstallDate(String fazelabInstallDate) {
        this.fazelabInstallDate = fazelabInstallDate;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getEshterak() {
        return eshterak;
    }

    public void setEshterak(String eshterak) {
        this.eshterak = eshterak;
    }

    public int getArse() {
        return arse;
    }

    public void setArse(int arse) {
        this.arse = arse;
    }

    public int getAianKol() {
        return aianKol;
    }

    public void setAianKol(int aianKol) {
        this.aianKol = aianKol;
    }

    public int getAianMaskooni() {
        return aianMaskooni;
    }

    public void setAianMaskooni(int aianMaskooni) {
        this.aianMaskooni = aianMaskooni;
    }

    public int getAianNonMaskooni() {
        return aianNonMaskooni;
    }

    public void setAianNonMaskooni(int aianNonMaskooni) {
        this.aianNonMaskooni = aianNonMaskooni;
    }

    public int getQotrEnsheabId() {
        return qotrEnsheabId;
    }

    public void setQotrEnsheabId(int qotrEnsheabId) {
        this.qotrEnsheabId = qotrEnsheabId;
    }

    public int getSifoon100() {
        return sifoon100;
    }

    public void setSifoon100(int sifoon100) {
        this.sifoon100 = sifoon100;
    }

    public int getSifoon125() {
        return sifoon125;
    }

    public void setSifoon125(int sifoon125) {
        this.sifoon125 = sifoon125;
    }

    public int getSifoon150() {
        return sifoon150;
    }

    public void setSifoon150(int sifoon150) {
        this.sifoon150 = sifoon150;
    }

    public int getSifoon200() {
        return sifoon200;
    }

    public void setSifoon200(int sifoon200) {
        this.sifoon200 = sifoon200;
    }

    public int getZarfiatQarardadi() {
        return zarfiatQarardadi;
    }

    public void setZarfiatQarardadi(int zarfiatQarardadi) {
        this.zarfiatQarardadi = zarfiatQarardadi;
    }

    public int getArzeshMelk() {
        return arzeshMelk;
    }

    public void setArzeshMelk(int arzeshMelk) {
        this.arzeshMelk = arzeshMelk;
    }

    public int getTedadMaskooni() {
        return tedadMaskooni;
    }

    public void setTedadMaskooni(int tedadMaskooni) {
        this.tedadMaskooni = tedadMaskooni;
    }

    public int getTedadTejari() {
        return tedadTejari;
    }

    public void setTedadTejari(int tedadTejari) {
        this.tedadTejari = tedadTejari;
    }

    public int getTedadSaier() {
        return tedadSaier;
    }

    public void setTedadSaier(int tedadSaier) {
        this.tedadSaier = tedadSaier;
    }

    public int getTaxfifId() {
        return taxfifId;
    }

    public void setTaxfifId(int taxfifId) {
        this.taxfifId = taxfifId;
    }

    public int getTedadTaxfif() {
        return tedadTaxfif;
    }

    public void setTedadTaxfif(int tedadTaxfif) {
        this.tedadTaxfif = tedadTaxfif;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAdamTaxfifAb() {
        return adamTaxfifAb;
    }

    public void setAdamTaxfifAb(boolean adamTaxfifAb) {
        this.adamTaxfifAb = adamTaxfifAb;
    }

    public boolean isAdamTaxfifFazelab() {
        return adamTaxfifFazelab;
    }

    public void setAdamTaxfifFazelab(boolean adamTaxfifFazelab) {
        this.adamTaxfifFazelab = adamTaxfifFazelab;
    }

    public boolean isEnsheabQeirDaem() {
        return isEnsheabQeirDaem;
    }

    public void setEnsheabQeirDaem(boolean ensheabQeirDaem) {
        isEnsheabQeirDaem = ensheabQeirDaem;
    }

    public boolean isHasRadif() {
        return hasRadif;
    }

    public void setHasRadif(boolean hasRadif) {
        this.hasRadif = hasRadif;
    }

    public String getRequestDictionaryString() {
        return requestDictionaryString;
    }

    public void setRequestDictionaryString(String requestDictionaryString) {
        this.requestDictionaryString = requestDictionaryString;
    }

    public ArrayList<RequestDictionary> getRequestDictionary() {
        return requestDictionary;
    }

    public void setRequestDictionary(ArrayList<RequestDictionary> requestDictionary) {
        this.requestDictionary = requestDictionary;
    }
}
