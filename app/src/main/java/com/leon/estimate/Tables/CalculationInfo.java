package com.leon.estimate.Tables;

import java.util.List;

public class CalculationInfo {
    String trackingId;
    String trackNumber;
    String requestType;
    String parNumber;
    String billId;
    String radif;
    String neighbourBillId;
    String zoneId;
    String callerId;
    String notificationMobile;
    String zoneTitle;
    String isNewEnsheab;
    String phoneNumber;
    String mobile;
    String firstName;
    String sureName;
    String hasFazelab;
    String fazelabInstallDate;
    String isFinished;
    String eshterak;
    String arse;
    String aianKol;
    String aianMaskooni;
    String aianNonMaskooni;
    String sifoon100;
    String sifoon125;
    String sifoon150;
    String sifoon200;
    String zarfiatQarardadi;
    String arzeshMelk;
    String tedadMaskooni;
    String tedadTejari;
    String tedadSaier;
    String tedadTaxfif;
    String nationalId;
    String identityCode;
    String fatherName;
    String postalCode;
    String address;
    String description;
    boolean adamTaxfifAb;
    boolean adamTaxfifFazelab;
    boolean isEnsheabQeirDaem;
    boolean hasRadif;

    List<KarbariDictionary> karbariDictionary;
    List<NoeVagozariDictionary> noeVagozariDictionary;
    List<QotrEnsheabDictionary> qotrEnsheabDictionary;
    List<TaxfifDictionary> taxfifDictionary;
    List<ServiceDictionary> serviceDictionary;

    public CalculationInfo(String trackingId, String trackNumber, String requestType,
                           String parNumber, String billId, String radif, String neighbourBillId,
                           String zoneId, String callerId, String notificationMobile,
                           String zoneTitle, String isNewEnsheab, String phoneNumber,
                           String mobile, String firstName, String sureName, String hasFazelab,
                           String fazelabInstallDate, String isFinished, String eshterak,
                           String arse, String aianKol, String aianMaskooni, String aianNonMaskooni,
                           String sifoon100, String sifoon125, String sifoon150, String sifoon200,
                           String zarfiatQarardadi, String arzeshMelk, String tedadMaskooni,
                           String tedadTejari, String tedadSaier, String tedadTaxfif,
                           String nationalId, String identityCode, String fatherName,
                           String postalCode, String address, String description,
                           boolean adamTaxfifAb, boolean adamTaxfifFazelab, boolean isEnsheabQeirDaem,
                           boolean hasRadif, List<KarbariDictionary> karbariDictionary,
                           List<NoeVagozariDictionary> noeVagozariDictionary,
                           List<QotrEnsheabDictionary> qotrEnsheabDictionary,
                           List<TaxfifDictionary> taxfifDictionary, List<ServiceDictionary> serviceDictionary) {
        this.trackingId = trackingId;
        this.trackNumber = trackNumber;
        this.requestType = requestType;
        this.parNumber = parNumber;
        this.billId = billId;
        this.radif = radif;
        this.neighbourBillId = neighbourBillId;
        this.zoneId = zoneId;
        this.callerId = callerId;
        this.notificationMobile = notificationMobile;
        this.zoneTitle = zoneTitle;
        this.isNewEnsheab = isNewEnsheab;
        this.phoneNumber = phoneNumber;
        this.mobile = mobile;
        this.firstName = firstName;
        this.sureName = sureName;
        this.hasFazelab = hasFazelab;
        this.fazelabInstallDate = fazelabInstallDate;
        this.isFinished = isFinished;
        this.eshterak = eshterak;
        this.arse = arse;
        this.aianKol = aianKol;
        this.aianMaskooni = aianMaskooni;
        this.aianNonMaskooni = aianNonMaskooni;
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
        this.address = address;
        this.description = description;
        this.adamTaxfifAb = adamTaxfifAb;
        this.adamTaxfifFazelab = adamTaxfifFazelab;
        this.isEnsheabQeirDaem = isEnsheabQeirDaem;
        this.hasRadif = hasRadif;
        this.karbariDictionary = karbariDictionary;
        this.noeVagozariDictionary = noeVagozariDictionary;
        this.qotrEnsheabDictionary = qotrEnsheabDictionary;
        this.taxfifDictionary = taxfifDictionary;
        this.serviceDictionary = serviceDictionary;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
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

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getRadif() {
        return radif;
    }

    public void setRadif(String radif) {
        this.radif = radif;
    }

    public String getNeighbourBillId() {
        return neighbourBillId;
    }

    public void setNeighbourBillId(String neighbourBillId) {
        this.neighbourBillId = neighbourBillId;
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

    public String getNotificationMobile() {
        return notificationMobile;
    }

    public void setNotificationMobile(String notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }

    public void setZoneTitle(String zoneTitle) {
        this.zoneTitle = zoneTitle;
    }

    public String getIsNewEnsheab() {
        return isNewEnsheab;
    }

    public void setIsNewEnsheab(String isNewEnsheab) {
        this.isNewEnsheab = isNewEnsheab;
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

    public String getHasFazelab() {
        return hasFazelab;
    }

    public void setHasFazelab(String hasFazelab) {
        this.hasFazelab = hasFazelab;
    }

    public String getFazelabInstallDate() {
        return fazelabInstallDate;
    }

    public void setFazelabInstallDate(String fazelabInstallDate) {
        this.fazelabInstallDate = fazelabInstallDate;
    }

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public String getEshterak() {
        return eshterak;
    }

    public void setEshterak(String eshterak) {
        this.eshterak = eshterak;
    }

    public String getArse() {
        return arse;
    }

    public void setArse(String arse) {
        this.arse = arse;
    }

    public String getAianKol() {
        return aianKol;
    }

    public void setAianKol(String aianKol) {
        this.aianKol = aianKol;
    }

    public String getAianMaskooni() {
        return aianMaskooni;
    }

    public void setAianMaskooni(String aianMaskooni) {
        this.aianMaskooni = aianMaskooni;
    }

    public String getAianNonMaskooni() {
        return aianNonMaskooni;
    }

    public void setAianNonMaskooni(String aianNonMaskooni) {
        this.aianNonMaskooni = aianNonMaskooni;
    }

    public String getSifoon100() {
        return sifoon100;
    }

    public void setSifoon100(String sifoon100) {
        this.sifoon100 = sifoon100;
    }

    public String getSifoon125() {
        return sifoon125;
    }

    public void setSifoon125(String sifoon125) {
        this.sifoon125 = sifoon125;
    }

    public String getSifoon150() {
        return sifoon150;
    }

    public void setSifoon150(String sifoon150) {
        this.sifoon150 = sifoon150;
    }

    public String getSifoon200() {
        return sifoon200;
    }

    public void setSifoon200(String sifoon200) {
        this.sifoon200 = sifoon200;
    }

    public String getZarfiatQarardadi() {
        return zarfiatQarardadi;
    }

    public void setZarfiatQarardadi(String zarfiatQarardadi) {
        this.zarfiatQarardadi = zarfiatQarardadi;
    }

    public String getArzeshMelk() {
        return arzeshMelk;
    }

    public void setArzeshMelk(String arzeshMelk) {
        this.arzeshMelk = arzeshMelk;
    }

    public String getTedadMaskooni() {
        return tedadMaskooni;
    }

    public void setTedadMaskooni(String tedadMaskooni) {
        this.tedadMaskooni = tedadMaskooni;
    }

    public String getTedadTejari() {
        return tedadTejari;
    }

    public void setTedadTejari(String tedadTejari) {
        this.tedadTejari = tedadTejari;
    }

    public String getTedadSaier() {
        return tedadSaier;
    }

    public void setTedadSaier(String tedadSaier) {
        this.tedadSaier = tedadSaier;
    }

    public String getTedadTaxfif() {
        return tedadTaxfif;
    }

    public void setTedadTaxfif(String tedadTaxfif) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public List<KarbariDictionary> getKarbariDictionary() {
        return karbariDictionary;
    }

    public void setKarbariDictionary(List<KarbariDictionary> karbariDictionary) {
        this.karbariDictionary = karbariDictionary;
    }

    public List<NoeVagozariDictionary> getNoeVagozariDictionary() {
        return noeVagozariDictionary;
    }

    public void setNoeVagozariDictionary(List<NoeVagozariDictionary> noeVagozariDictionary) {
        this.noeVagozariDictionary = noeVagozariDictionary;
    }

    public List<QotrEnsheabDictionary> getQotrEnsheabDictionary() {
        return qotrEnsheabDictionary;
    }

    public void setQotrEnsheabDictionary(List<QotrEnsheabDictionary> qotrEnsheabDictionary) {
        this.qotrEnsheabDictionary = qotrEnsheabDictionary;
    }

    public List<TaxfifDictionary> getTaxfifDictionary() {
        return taxfifDictionary;
    }

    public void setTaxfifDictionary(List<TaxfifDictionary> taxfifDictionary) {
        this.taxfifDictionary = taxfifDictionary;
    }

    public List<ServiceDictionary> getServiceDictionary() {
        return serviceDictionary;
    }

    public void setServiceDictionary(List<ServiceDictionary> serviceDictionary) {
        this.serviceDictionary = serviceDictionary;
    }
}
