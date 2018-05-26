package com.example.food.DAO;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Member implements Serializable {
    private String userAccount, userPassword, nickName, birthday;
    private int gender, userId;
    private byte[] portrait;
    private String preference, collection, userGift, userFriends;

    // 註冊用
    // 送出會員資料給 server 不含 portrait(在image方法)
    public Member(String userAccount, String userPassword, String nickName, String birthday,
                  int gender, String preference) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
        this.preference = preference;
    }

    // 接收 server 給的會員所有資料 不含 portrait(在image方法)
    public Member(int userId, String userAccount, String userPassword, String nickName, String birthday
            , int gender, String preference, String collection, String userGift,
                  String userFriends) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
        this.userId = userId;
        this.preference = preference;
        this.collection = collection;
        this.userGift = userGift;
        this.userFriends = userFriends;
    }

    // 送出 與 接收server給的會員資料 修改會員資料用
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

    //登入用
    public Member(String userAccount, String userPassword) {
        this.userAccount = userAccount;
        this.userPassword = userPassword;
    }

    /*   n用途編號  0=登入用  1=修改喜好  2=新增或移除收藏用
                   3=禮物回饋  4=追蹤好友  */
    public Member(int n, int userId, String string) {
        this.userId = userId;
        switch (n) {
            case 0:
                this.userPassword = string;
                break;
            case 1:
                this.preference = string;
                break;
            case 2:
                this.collection = string;
                break;
            case 3:
                this.userGift = string;
                break;
            case 4:
                this.userFriends = string;
                break;
            default:
                break;
        }
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
