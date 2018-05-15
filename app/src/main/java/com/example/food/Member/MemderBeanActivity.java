package com.example.food.Member;


import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Pattern;

public class MemderBeanActivity  {
    //限制帳號密碼格式
    public boolean isValid(EditText editText) {
        Pattern pattern = Pattern.compile("[a-pA-P]&[0-9]]");
        String text = editText.getText().toString();

        if (!text.matches(String.valueOf(pattern))) {
            editText.setError("格式錯誤");
            return false;
        } else {
            return true;
        }
    }
    public void inputFilter(EditText editText){
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
    }




}
