package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Calculation", indices = @Index(value = {"examinationId"}, unique = true))

public class Calculation {
    @PrimaryKey(autoGenerate = true)
    int id;
    String address;
    String billId;
    String examinationDay;
    String examinationId;
    boolean isPeymayesh;
    String moshtarakMobile;
    String nameAndFamily;
    String neighbourBillId;
    String notificationMobile;
    String radif;
    String serviceGroup;
    String trackNumber;
    boolean read;

    public Calculation(String address, String billId, String examinationDay,
                       String examinationId, boolean isPeymayesh, String moshtarakMobile,
                       String nameAndFamily, String neighbourBillId, String notificationMobile,
                       String radif, String serviceGroup, String trackNumber) {
        this.address = address;
        this.billId = billId;
        this.examinationDay = examinationDay;
        this.examinationId = examinationId;
        this.isPeymayesh = isPeymayesh;
        this.moshtarakMobile = moshtarakMobile;
        this.nameAndFamily = nameAndFamily;
        this.neighbourBillId = neighbourBillId;
        this.notificationMobile = notificationMobile;
        this.radif = radif;
        this.serviceGroup = serviceGroup;
        this.trackNumber = trackNumber;
    }

//    public Calculation() {
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(String examinationId) {
        this.examinationId = examinationId;
    }

    public boolean isPeymayesh() {
        return isPeymayesh;
    }

    public void setPeymayesh(boolean peymayesh) {
        isPeymayesh = peymayesh;
    }

    public String getMoshtarakMobile() {
        return moshtarakMobile;
    }

    public void setMoshtarakMobile(String moshtarakMobile) {
        this.moshtarakMobile = moshtarakMobile;
    }

    public String getNameAndFamily() {
        return nameAndFamily;
    }

    public void setNameAndFamily(String nameAndFamily) {
        this.nameAndFamily = nameAndFamily;
    }

    public String getNeighbourBillId() {
        return neighbourBillId;
    }

    public void setNeighbourBillId(String neighbourBillId) {
        this.neighbourBillId = neighbourBillId;
    }

    public String getNotificationMobile() {
        return notificationMobile;
    }

    public void setNotificationMobile(String notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public String getRadif() {
        return radif;
    }

    public void setRadif(String radif) {
        this.radif = radif;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
