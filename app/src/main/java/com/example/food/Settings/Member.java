package com.example.food.Settings;

import java.io.Serializable;
import java.sql.Blob;

@SuppressWarnings("serial")
class Member implements Serializable {
    private String userAccount, userPassword, nickName, birthday;
    private int gender, userId;
    private Blob portrait;
    private String preference, collection, userGift, userFriends;

    // 傳修改過後的個人資料 給server
    public Member(String userAccount, String userPassword, String nickName, String birthday, int gender, Blob portrait) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
        this.portrait = portrait;
    }

    // 接受server給的個人會員資料
    public Member(String userAccount, String userPassword, String nickName, String birthday, int gender) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
    }

    // 傳 userAccount 來搜尋
    public Member(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getUserGift() {
        return userGift;
    }

    public void setUserGift(String userGift) {
        this.userGift = userGift;
    }

    public String getUserFriends() {
        return userFriends;
    }

    public void setUserFriends(String userFriends) {
        this.userFriends = userFriends;
    }
}
