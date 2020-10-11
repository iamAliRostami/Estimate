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
    int karbariId;
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
    String shenasname;

    int faseleKhakiA;
    int faseleKhakiF;
    int faseleAsphaultA;
    int faseleAsphaultF;
    int faseleSangA;
    int faseleSangF;
    int faseleOtherA;
    int faseleOtherF;
    boolean ezhaNazarA;
    boolean ezhaNazarF;
    int qotrLooleI;
    int jensLooleI;
    String qotrLooleS;
    String jensLooleS;
    boolean looleA;
    boolean looleF;
    int noeMasrafI;
    String noeMasrafS;
    int vaziatNasbPompI;
    int omqeZirzamin;
    boolean etesalZirzamin;
    int omqFazelab;
    boolean chahAbBaran;
    public int noeVagozariId;
    public int pelak;
    boolean sanad;
    String examinerName;
    boolean estelamShahrdari, parvane, motaqazi;

    String chahDescription;
    String masrafDescription;
    String mapDescription;

    @Ignore
    ArrayList<RequestDictionary> requestDictionary;

    public ExaminerDuties updateExaminerDuties(SecondForm secondForm) {
        faseleKhakiA = secondForm.khakiAb;
        faseleKhakiF = secondForm.khakiFazelab;
        faseleAsphaultA = secondForm.asphalutAb;
        faseleAsphaultF = secondForm.asphalutFazelab;
        faseleSangA = secondForm.sangFarshAb;
        faseleSangF = secondForm.sangFarshFazelab;
        faseleOtherA = secondForm.otherAb;
        faseleOtherF = secondForm.otherFazelab;
        ezhaNazarA = secondForm.ezhaNazarA;
        ezhaNazarF = secondForm.ezhaNazarF;
        qotrLooleI = secondForm.qotreLooleI;
        jensLooleI = secondForm.jenseLooleI;
        qotrLooleS = secondForm.qotreLoole;
        jensLooleS = secondForm.jenseLoole;
        looleA = secondForm.looleA;
        looleF = secondForm.looleF;
        noeMasrafI = secondForm.noeMasraf;
        noeMasrafS = secondForm.noeMasrafString;
        vaziatNasbPompI = secondForm.vaziatNasbePomp;
        omqeZirzamin = secondForm.omqeZirzamin;
        omqFazelab = secondForm.omqFazelab;
        etesalZirzamin = secondForm.etesalZirzamin;
        chahAbBaran = secondForm.chahAbBaran;
        chahDescription = secondForm.chahDescription;
        masrafDescription = secondForm.masrafDescription;
        eshterak = secondForm.eshterak;
        return this;
    }

    public ExaminerDuties updateExaminerDuties(CalculationUserInput calculationUserInput) {
        trackNumber = calculationUserInput.trackNumber;
        karbariId = calculationUserInput.karbariId;
        radif = calculationUserInput.radif;
        billId = calculationUserInput.billId;
        nameAndFamily = calculationUserInput.firstName.trim().concat(" ".concat(calculationUserInput.sureName.trim()));
        notificationMobile = calculationUserInput.notificationMobile;
        address = calculationUserInput.address;
        neighbourBillId = calculationUserInput.neighbourBillId;
        trackingId = calculationUserInput.trackingId;
        requestType = String.valueOf(calculationUserInput.requestType);
        parNumber = calculationUserInput.parNumber;
        phoneNumber = calculationUserInput.phoneNumber;
        mobile = calculationUserInput.mobile;
        firstName = calculationUserInput.firstName;
        sureName = calculationUserInput.sureName;
        arse = calculationUserInput.arse;
        aianKol = calculationUserInput.aianKol;
        aianMaskooni = calculationUserInput.aianMaskooni;
        aianNonMaskooni = calculationUserInput.aianTejari;
        qotrEnsheabId = calculationUserInput.qotrEnsheabId;
        sifoon100 = calculationUserInput.sifoon100;
        sifoon125 = calculationUserInput.sifoon125;
        sifoon150 = calculationUserInput.sifoon150;
        sifoon200 = calculationUserInput.sifoon200;
        zarfiatQarardadi = calculationUserInput.zarfiatQarardadi;
        arzeshMelk = calculationUserInput.arzeshMelk;
        tedadMaskooni = calculationUserInput.tedadMaskooni;
        tedadTejari = calculationUserInput.tedadTejari;
        tedadSaier = calculationUserInput.tedadSaier;
        taxfifId = calculationUserInput.taxfifId;
        tedadTaxfif = calculationUserInput.tedadTaxfif;
        nationalId = calculationUserInput.nationalId;
        identityCode = calculationUserInput.identityCode;
        fatherName = calculationUserInput.fatherName;
        postalCode = calculationUserInput.postalCode;
        description = calculationUserInput.description;
        adamTaxfifAb = calculationUserInput.adamTaxfifAb;
        adamTaxfifFazelab = calculationUserInput.adamTaxfifFazelab;
        isEnsheabQeirDaem = calculationUserInput.ensheabQeireDaem;
        requestDictionaryString = calculationUserInput.selectedServicesString;
        shenasname = calculationUserInput.shenasname;
        return this;
    }

    public int getVaziatNasbPompI() {
        return vaziatNasbPompI;
    }

    public String getExaminerName() {
        return examinerName;
    }

    public void setExaminerName(String examinerName) {
        this.examinerName = examinerName;
    }

    public String getMapDescription() {
        return mapDescription;
    }

    public void setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
    }

    public boolean isSanad() {
        return sanad;
    }

    public void setSanad(boolean sanad) {
        this.sanad = sanad;
    }

    public int getNoeVagozariId() {
        return noeVagozariId;
    }

    public void setNoeVagozariId(int noeVagozariId) {
        this.noeVagozariId = noeVagozariId;
    }

    public int getPelak() {
        return pelak;
    }

    public void setPelak(int pelak) {
        this.pelak = pelak;
    }

    public boolean isEstelamShahrdari() {
        return estelamShahrdari;
    }

    public void setEstelamShahrdari(boolean estelamShahrdari) {
        this.estelamShahrdari = estelamShahrdari;
    }

    public boolean isParvane() {
        return parvane;
    }

    public void setParvane(boolean parvane) {
        this.parvane = parvane;
    }

    public boolean isMotaqazi() {
        return motaqazi;
    }

    public void setMotaqazi(boolean motaqazi) {
        this.motaqazi = motaqazi;
    }

    public String getShenasname() {
        return shenasname;
    }

    public void setShenasname(String shenasname) {
        this.shenasname = shenasname;
    }

    public String getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(String examinationId) {
        this.examinationId = examinationId;
    }

    public int getKarbariId() {
        return karbariId;
    }

    public void setKarbariId(int karbariId) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFaseleKhakiA() {
        return faseleKhakiA;
    }

    public void setFaseleKhakiA(int faseleKhakiA) {
        this.faseleKhakiA = faseleKhakiA;
    }

    public int getFaseleKhakiF() {
        return faseleKhakiF;
    }

    public void setFaseleKhakiF(int faseleKhakiF) {
        this.faseleKhakiF = faseleKhakiF;
    }

    public String getChahDescription() {
        return chahDescription;
    }

    public void setChahDescription(String chahDescription) {
        this.chahDescription = chahDescription;
    }

    public String getMasrafDescription() {
        return masrafDescription;
    }

    public void setMasrafDescription(String masrafDescription) {
        this.masrafDescription = masrafDescription;
    }

    public int getFaseleAsphaultA() {
        return faseleAsphaultA;
    }

    public void setFaseleAsphaultA(int faseleAsphaultA) {
        this.faseleAsphaultA = faseleAsphaultA;
    }

    public int getFaseleAsphaultF() {
        return faseleAsphaultF;
    }

    public void setFaseleAsphaultF(int faseleAsphaultF) {
        this.faseleAsphaultF = faseleAsphaultF;
    }

    public int getFaseleSangA() {
        return faseleSangA;
    }

    public void setFaseleSangA(int faseleSangA) {
        this.faseleSangA = faseleSangA;
    }

    public int getFaseleSangF() {
        return faseleSangF;
    }

    public void setFaseleSangF(int faseleSangF) {
        this.faseleSangF = faseleSangF;
    }

    public int getFaseleOtherA() {
        return faseleOtherA;
    }

    public void setFaseleOtherA(int faseleOtherA) {
        this.faseleOtherA = faseleOtherA;
    }

    public int getFaseleOtherF() {
        return faseleOtherF;
    }

    public void setFaseleOtherF(int faseleOtherF) {
        this.faseleOtherF = faseleOtherF;
    }

    public boolean isEzhaNazarA() {
        return ezhaNazarA;
    }

    public void setEzhaNazarA(boolean ezhaNazarA) {
        this.ezhaNazarA = ezhaNazarA;
    }

    public boolean isEzhaNazarF() {
        return ezhaNazarF;
    }

    public void setEzhaNazarF(boolean ezhaNazarF) {
        this.ezhaNazarF = ezhaNazarF;
    }

    public int getQotrLooleI() {
        return qotrLooleI;
    }

    public void setQotrLooleI(int qotrLooleI) {
        this.qotrLooleI = qotrLooleI;
    }

    public int getJensLooleI() {
        return jensLooleI;
    }

    public void setJensLooleI(int jensLooleI) {
        this.jensLooleI = jensLooleI;
    }

    public String getQotrLooleS() {
        return qotrLooleS;
    }

    public void setQotrLooleS(String qotrLooleS) {
        this.qotrLooleS = qotrLooleS;
    }

    public String getJensLooleS() {
        return jensLooleS;
    }

    public void setJensLooleS(String jensLooleS) {
        this.jensLooleS = jensLooleS;
    }

    public boolean isLooleA() {
        return looleA;
    }

    public void setLooleA(boolean looleA) {
        this.looleA = looleA;
    }

    public boolean isLooleF() {
        return looleF;
    }

    public void setLooleF(boolean looleF) {
        this.looleF = looleF;
    }

    public int getNoeMasrafI() {
        return noeMasrafI;
    }

    public void setNoeMasrafI(int noeMasrafI) {
        this.noeMasrafI = noeMasrafI;
    }

    public String getNoeMasrafS() {
        return noeMasrafS;
    }

    public void setNoeMasrafS(String noeMasrafS) {
        this.noeMasrafS = noeMasrafS;
    }

    public int isVaziatNasbPompI() {
        return vaziatNasbPompI;
    }

    public void setVaziatNasbPompI(int vaziatNasbPompI) {
        this.vaziatNasbPompI = vaziatNasbPompI;
    }

    public int getOmqeZirzamin() {
        return omqeZirzamin;
    }

    public void setOmqeZirzamin(int omqeZirzamin) {
        this.omqeZirzamin = omqeZirzamin;
    }

    public boolean isEtesalZirzamin() {
        return etesalZirzamin;
    }

    public void setEtesalZirzamin(boolean etesalZirzamin) {
        this.etesalZirzamin = etesalZirzamin;
    }

    public int getOmqFazelab() {
        return omqFazelab;
    }

    public void setOmqFazelab(int omqFazelab) {
        this.omqFazelab = omqFazelab;
    }

    public boolean isChahAbBaran() {
        return chahAbBaran;
    }

    public void setChahAbBaran(boolean chahAbBaran) {
        this.chahAbBaran = chahAbBaran;
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
