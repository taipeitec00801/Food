package com.example.food.Settings;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;

@SuppressWarnings("serial")
class Member implements Serializable {
    private String userId, password, nickName;
    private Date birthday;
    private int gender;
    private Blob portrait;
    private String preference, collection;

    public Member(String userId, String password, String nickName, Date birthday, int gender, Blob portrait, String preference, String collection) {
        this.userId = userId;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
        this.portrait = portrait;
        this.preference = preference;
        this.collection = collection;
    }

    public Member(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Blob getPortrait() {
        return portrait;
    }

    public void setPortrait(Blob portrait) {
        this.portrait = portrait;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
