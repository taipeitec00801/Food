package com.example.food.Other;

import android.content.SharedPreferences;

public class MySharedPreferences {

    public static void initSharedPreferences(SharedPreferences prefs) {
        //是否登入 預設 false
        prefs.edit().putBoolean("login", false).apply();
        //  會員資料
        prefs.edit().putInt("memberId", 0).apply();
        prefs.edit().putString("userAccount", "").apply();
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

}
