package com.example.food.Member;


import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemderBeanActivity  {
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
