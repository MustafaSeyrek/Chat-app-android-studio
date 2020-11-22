package com.mychat.chat;

public class GelenKutusu {
    String keyGelenKutusu;
    String gonderenUid;
    String aliciUid;
    String okundu;

    public GelenKutusu(String keyGelenKutusu, String gonderenUid, String aliciUid, String okundu) {
        this.keyGelenKutusu = keyGelenKutusu;
        this.gonderenUid = gonderenUid;
        this.aliciUid = aliciUid;
        this.okundu = okundu;
    }

    public GelenKutusu() {
    }

    public String getKeyGelenKutusu() {
        return keyGelenKutusu;
    }

    public void setKeyGelenKutusu(String keyGelenKutusu) {
        this.keyGelenKutusu = keyGelenKutusu;
    }

    public String getGonderenUid() {
        return gonderenUid;
    }

    public void setGonderenUid(String gonderenUid) {
        this.gonderenUid = gonderenUid;
    }

    public String getAliciUid() {
        return aliciUid;
    }

    public void setAliciUid(String aliciUid) {
        this.aliciUid = aliciUid;
    }

    public String getOkundu() {
        return okundu;
    }

    public void setOkundu(String okundu) {
        this.okundu = okundu;
    }
}

