package com.mychat.chat;

public class UserInfo {
    String uid;
    String ad;
    String UrlPp;

    public UserInfo(String uid, String ad, String urlPp) {
        this.uid = uid;
        this.ad = ad;
        UrlPp = urlPp;
    }
    public UserInfo(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getUrlPp() {
        return UrlPp;
    }

    public void setUrlPp(String urlPp) {
        UrlPp = urlPp;
    }
}
