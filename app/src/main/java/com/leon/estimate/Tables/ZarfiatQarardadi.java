package com.leon.estimate.Tables;

public class ZarfiatQarardadi {
    String karbari;
    String noeShoql;
    String tedadVahed;
    String vahedMohasebe;
    int meter;

    public ZarfiatQarardadi(String karbari, String noeShoql, String tedadVahed,
                            String vahedMohasebe, int meter) {
        this.karbari = karbari;
        this.noeShoql = noeShoql;
        this.tedadVahed = tedadVahed;
        this.vahedMohasebe = vahedMohasebe;
        this.meter = meter;
    }

    public String getKarbari() {
        return karbari;
    }

    public void setKarbari(String karbari) {
        this.karbari = karbari;
    }

    public String getNoeShoql() {
        return noeShoql;
    }

    public void setNoeShoql(String noeShoql) {
        this.noeShoql = noeShoql;
    }

    public String getTedadVahed() {
        return tedadVahed;
    }

    public void setTedadVahed(String tedadVahed) {
        this.tedadVahed = tedadVahed;
    }

    public String getVahedMohasebe() {
        return vahedMohasebe;
    }

    public void setVahedMohasebe(String vahedMohasebe) {
        this.vahedMohasebe = vahedMohasebe;
    }

    public int getMeter() {
        return meter;
    }

    public void setMeter(int meter) {
        this.meter = meter;
    }
}
