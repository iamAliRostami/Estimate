package com.leon.estimate.Tables;

import androidx.room.Ignore;

public class SecondForm {
    int khakiAb;
    int khakiFazelab;
    int asphalutAb;
    int asphalutFazelab;
    int sangFarshAb;
    int sangFarshFazelab;
    int otherAb;
    int otherFazelab;
    String jenseLoole;
    int noeMasraf;
    String qotreLoole;
    String noeMasrafString;
    int vaziatNasbePomp;
    int omqeZirzamin;
    boolean etesalZirzamin;
    int omqFazelab;
    boolean chahAbBaran;

    boolean ezhaNazarA;
    boolean ezhaNazarF;
    int qotreLooleI;
    int jenseLooleI;
    boolean looleA;
    boolean looleF;

    String chahDescription;
    String masrafDescription;
    @Ignore
    String eshterak;

    public SecondForm(int khakiAb, int khakiFazelab, int asphalutAb, int asphalutFazelab,
                      int sangFarshAb, int sangFarshFazelab, int otherAb, int otherFazelab,
                      String qotreLoole, String jenseLoole, int noeMasraf, String noeMasrafString,
                      int vaziatNasbePomp, int omqeZirzamin, boolean etesalZirzamin,
                      int omqFazelab, boolean chahAbBaran, boolean ezhaNazarA, boolean ezhaNazarF,
                      int qotreLooleI, int jenseLooleI, boolean looleA, boolean looleF,
                      String masrafDescription, String chahDescription, String eshterak) {

        this.ezhaNazarA = ezhaNazarA;
        this.ezhaNazarF = ezhaNazarF;
        this.qotreLooleI = qotreLooleI;
        this.jenseLooleI = jenseLooleI;
        this.looleA = looleA;
        this.looleF = looleF;

        this.khakiAb = khakiAb;
        this.khakiFazelab = khakiFazelab;
        this.asphalutAb = asphalutAb;
        this.asphalutFazelab = asphalutFazelab;
        this.sangFarshAb = sangFarshAb;
        this.sangFarshFazelab = sangFarshFazelab;
        this.otherAb = otherAb;
        this.otherFazelab = otherFazelab;
        this.qotreLoole = qotreLoole;
        this.jenseLoole = jenseLoole;
        this.noeMasraf = noeMasraf;
        this.noeMasrafString = noeMasrafString;
        this.vaziatNasbePomp = vaziatNasbePomp;
        this.omqeZirzamin = omqeZirzamin;
        this.omqFazelab = omqFazelab;
        this.etesalZirzamin = etesalZirzamin;
        this.chahAbBaran = chahAbBaran;

        this.chahDescription = chahDescription;
        this.masrafDescription = masrafDescription;
        this.eshterak = eshterak;

    }

    public String getEshterak() {
        return eshterak;
    }

    public void setEshterak(String eshterak) {
        this.eshterak = eshterak;
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

    public int getOtherAb() {
        return otherAb;
    }

    public void setOtherAb(int otherAb) {
        this.otherAb = otherAb;
    }

    public int getOtherFazelab() {
        return otherFazelab;
    }

    public void setOtherFazelab(int otherFazelab) {
        this.otherFazelab = otherFazelab;
    }

    public int getKhakiAb() {
        return khakiAb;
    }

    public void setKhakiAb(int khakiAb) {
        this.khakiAb = khakiAb;
    }

    public int getKhakiFazelab() {
        return khakiFazelab;
    }

    public void setKhakiFazelab(int khakiFazelab) {
        this.khakiFazelab = khakiFazelab;
    }

    public int getAsphalutAb() {
        return asphalutAb;
    }

    public void setAsphalutAb(int asphalutAb) {
        this.asphalutAb = asphalutAb;
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

    public int getQotreLooleI() {
        return qotreLooleI;
    }

    public void setQotreLooleI(int qotreLooleI) {
        this.qotreLooleI = qotreLooleI;
    }

    public int getJenseLooleI() {
        return jenseLooleI;
    }

    public void setJenseLooleI(int jenseLooleI) {
        this.jenseLooleI = jenseLooleI;
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

    public int getAsphalutFazelab() {
        return asphalutFazelab;
    }

    public void setAsphalutFazelab(int asphalutFazelab) {
        this.asphalutFazelab = asphalutFazelab;
    }

    public int getSangFarshAb() {
        return sangFarshAb;
    }

    public void setSangFarshAb(int sangFarshAb) {
        this.sangFarshAb = sangFarshAb;
    }

    public int getSangFarshFazelab() {
        return sangFarshFazelab;
    }

    public void setSangFarshFazelab(int sangFarshFazelab) {
        this.sangFarshFazelab = sangFarshFazelab;
    }

    public String getQotreLoole() {
        return qotreLoole;
    }

    public void setQotreLoole(String qotreLoole) {
        this.qotreLoole = qotreLoole;
    }

    public String getJenseLoole() {
        return jenseLoole;
    }

    public void setJenseLoole(String jenseLoole) {
        this.jenseLoole = jenseLoole;
    }

    public int getNoeMasraf() {
        return noeMasraf;
    }

    public void setNoeMasraf(int noeMasraf) {
        this.noeMasraf = noeMasraf;
    }

    public String getNoeMasrafString() {
        return noeMasrafString;
    }

    public void setNoeMasrafString(String noeMasrafString) {
        this.noeMasrafString = noeMasrafString;
    }

    public int isVaziatNasbePomp() {
        return vaziatNasbePomp;
    }

    public void setVaziatNasbePomp(int vaziatNasbePomp) {
        this.vaziatNasbePomp = vaziatNasbePomp;
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
}
