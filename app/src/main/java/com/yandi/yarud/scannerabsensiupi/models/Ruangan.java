package com.yandi.yarud.scannerabsensiupi.models;

public class Ruangan {
    private int idRuangan;
    private String kodeRuangan="", ruangan="", kodeFak="", namaFak="";

    public Ruangan(){}
    public Ruangan(int idRuangan, String kodeRuangan, String ruangan, String kodeFak, String namaFak) {
        this.idRuangan = idRuangan;
        this.kodeRuangan = kodeRuangan;
        this.ruangan = ruangan;
        this.kodeFak = kodeFak;
        this.namaFak = namaFak;
    }

    public int getIdRuangan() {
        return idRuangan;
    }

    public void setIdRuangan(int idRuangan) {
        this.idRuangan = idRuangan;
    }

    public String getKodeRuangan() {
        return kodeRuangan;
    }

    public void setKodeRuangan(String kodeRuangan) {
        this.kodeRuangan = kodeRuangan;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getKodeFak() {
        return kodeFak;
    }

    public void setKodeFak(String kodeFak) {
        this.kodeFak = kodeFak;
    }

    public String getNamaFak() {
        return namaFak;
    }

    public void setNamaFak(String namaFak) {
        this.namaFak = namaFak;
    }
}
