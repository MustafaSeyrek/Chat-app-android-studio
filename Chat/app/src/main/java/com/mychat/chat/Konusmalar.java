package com.mychat.chat;

public class Konusmalar {
    String keyGelenKutusu;
    String gonderenUid;
    String mesaj;

    public Konusmalar(String keyGelenKutusu, String gonderenUid, String mesaj) {
        this.keyGelenKutusu = keyGelenKutusu;
        this.gonderenUid = gonderenUid;
        this.mesaj = mesaj;
    }

    public Konusmalar() {
    }

    public String getKeyGelenKutusu() {
        return keyGelenKutusu;
    }

    public void setKeyGelenKutusu(String keyGelenKutusu) {
        keyGelenKutusu = keyGelenKutusu;
    }

    public String getGonderenUid() {
        return gonderenUid;
    }

    public void setGonderenUid(String gonderenUid) {
        this.gonderenUid = gonderenUid;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
