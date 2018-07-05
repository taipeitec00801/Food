package com.example.food.Other;


import android.text.InputFilter;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class InputFormat {
    public static final String KEY = "taipeitecjava008";

    //限制非空值
    public boolean isInputNotNull(EditText editText) {
        String text = editText.getText().toString();
        if (text.length() > 0) {
            editText.setError(null);
            return true;
        } else {
            editText.setError("請輸入資料");
            return false;
        }
    }

    //限制帳號格式
    public boolean isValidAccount(EditText editText) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        String text = editText.getText().toString();
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            editText.setError("帳號格式錯誤");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }


    //限制密碼格式
    public boolean isValidPassword(EditText editText) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]*$");
        String text = editText.getText().toString();
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            editText.setError("密碼格式錯誤");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    //限制密碼長度大於5 小於13
    public boolean passwordLength(EditText editText) {
        String text = editText.getText().toString();
        if (text.length() > 5) {
            editText.setError(null);
            return true;
        } else {
            editText.setError("密碼長度必須6-12碼");
            return false;
        }
    }
    public void inputFilter(EditText editText, int no) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(no)});
    }

}
