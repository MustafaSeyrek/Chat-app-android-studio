package com.mychat.chat;

public class SonMesaj {
    String keyGelenKutusu;
    String mesajKey;

    public SonMesaj(String keyGelenKutusu, String mesajKey) {
        this.keyGelenKutusu = keyGelenKutusu;
        this.mesajKey = mesajKey;
    }

    public SonMesaj() {
    }

    public String getKeyGelenKutusu() {
        return keyGelenKutusu;
    }

    public void setKeyGelenKutusu(String keyGelenKutusu) {
        this.keyGelenKutusu = keyGelenKutusu;
    }

    public String getMesajKey() {
        return mesajKey;
    }

    public void setMesajKey(String mesajKey) {
        this.mesajKey = mesajKey;
    }
}
