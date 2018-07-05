package com.example.food.AppModel;

import java.io.Serializable;
import java.sql.Blob;

@SuppressWarnings("serial")
public class Member implements Serializable {
    private int memberId;
    private String userAccount;
    private String userPassword;
    private String nickname;
    private String birthday;
    private int gender;
    private int userRank;
    private byte[] portrait;
    private String preference;
    private String collection;
    private String userGift;
    private String userFriends;

    // 註冊用
    // 送出會員資料給 server 不含 portrait
    public Member(String userAccount, String userPassword, String nickname, String birthday,
                  int gender, String preference) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.preference = preference;
    }

    // 接收 server 給的會員所有資料 不含 portrait
    public Member(int memberId, String userAccount, String userPassword,
                  String nickname, String birthday, int gender,
                  int userRank, Blob portrait, String preference,
                  String collection, String userGift, String userFriends) {
        this.memberId = memberId;
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.userRank = userRank;
//        this.portrait = portrait;
        this.preference = preference;
        this.collection = collection;
        this.userGift = userGift;
        this.userFriends = userFriends;
    }

    // 修改會員資料用
    public Member(String userAccount, String userPassword, String nickname,
                  String birthday, int gender) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
    }

    //註冊第一頁傳送到第二頁用
    public Member(String userAccount, String userPassword, String nickname,
                  String birthday, int gender, byte[] portrait) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.portrait = portrait;
    }

    public Member() {
        super();
    }

    //-------getter--setter---------------------------------------------------------------
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public int getMemberId() {
        return memberId;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
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
