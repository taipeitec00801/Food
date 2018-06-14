package com.example.food.Other;

import android.content.SharedPreferences;

import com.example.food.DAO.Member;

public class MySharedPreferences {

    public static void initSharedPreferences(SharedPreferences prefs) {
        //是否登入 預設 false
        prefs.edit().putBoolean("login", false).apply();
        //  會員資料
        prefs.edit().putInt("memberId", 0).apply();
        prefs.edit().putString("userAccount", "userEmail@.gmail.com").apply();
        prefs.edit().putString("userPassword", "").apply();
        prefs.edit().putString("nickname", "").apply();
        prefs.edit().putString("birthday", "").apply();
        // 預設會員性別 為2 查無資料
        prefs.edit().putInt("gender", 2).apply();
        prefs.edit().putInt("userRank", 1).apply();
        prefs.edit().putString("preference", "0,0,0,0,0,0,0,0,0,0").apply();
        prefs.edit().putString("collection", "").apply();
        prefs.edit().putString("userGift", "").apply();
        prefs.edit().putString("userFriends", "").apply();
    }

    public static boolean inputSharedPreferences(SharedPreferences prefs, Member member) {
        boolean inputOk = false;
        //將 從Server端 拿到的會員資料 寫入偏好設定檔中
        if (member != null) {
            //將 從Server端 拿到的會員資料 寫入偏好設定檔中
            prefs.edit().putInt("memberId", member.getMemberId()).apply();
            prefs.edit().putString("userAccount", member.getUserAccount()).apply();
            prefs.edit().putString("userPassword", member.getUserPassword()).apply();
            prefs.edit().putString("nickname", member.getNickName()).apply();
            prefs.edit().putString("birthday", member.getBirthday()).apply();
            prefs.edit().putInt("gender", member.getGender()).apply();
            prefs.edit().putInt("userRank", member.getUserRank()).apply();
            prefs.edit().putString("preference", member.getPreference()).apply();
            prefs.edit().putString("collection", member.getCollection()).apply();
            prefs.edit().putString("userGift", member.getUserGift()).apply();
            prefs.edit().putString("userFriends", member.getUserFriends()).apply();

            inputOk = true;
        }
        return inputOk;
    }
}
