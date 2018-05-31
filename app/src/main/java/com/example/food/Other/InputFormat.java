package com.example.food.Other;


import android.app.Activity;
import android.text.InputFilter;
import android.widget.EditText;

import com.example.food.DAO.Member;
import com.example.food.DAO.MemberDAO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFormat {
    //限制帳號格式
    public boolean isValidAccount(EditText editText) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+@[0-9a-z_-]+([.0-9a-z_-]+)*$");
        String text = editText.getText().toString();
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            editText.setError("格式錯誤");
            return false;
        } else {
            return true;
        }
    }

    //傳送帳號到 server 帳號是否重複 若重複  傳 true
    public boolean unUsableAccount(Activity activity, EditText editText) {
        MemberDAO memberDAO = new MemberDAO(activity);
        String inputAccount = editText.getText().toString().trim();
        boolean usable = memberDAO.checkAccount(inputAccount);
        if (usable) {
            editText.setError("帳號已存在");
            return false;
        } else {
            return true;
        }
    }

    //限制密碼格式
    public boolean isValidPassword(EditText editText) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]*$");
        String text = editText.getText().toString();
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            editText.setError("格式錯誤");
            return false;
        } else {
            return true;
        }
    }

    public void inputFilter(EditText editText, int no){
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(no)});
    }




}
