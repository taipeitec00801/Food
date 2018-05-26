package com.example.food.Member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.food.R;

import java.util.Calendar;

public class MemberActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private EditText etUser , etPassword, etBirthday, etGender;
    private de.hdodenhof.circleimageview.CircleImageView cvImage;
    private Button btConfirm;
    private int mYear, mMonth, mDay;
    private Spinner spGender;
    MemderBeanActivity mb = new MemderBeanActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        btConfirm = findViewById(R.id.btConfirm);
        findViews();
        showNow();
        showSpinner();
//        getDropDownView();
//        initContent();
        btConfirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MemberActivity.this, MemberActivity.class);
                startActivity(intent);
//                finish();

            }
        });
    }

    private void findViews() {
        cvImage = findViewById(R.id.cvImage);
        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        etGender = findViewById(R.id.etGender);
        spGender= findViewById(R.id.spGender);
        mb.inputFilter(etPassword,12);
        mb.inputFilter(etUser,40);
    }

    private void showSpinner() {
        String[] places = {"Gender", "男姓", "女姓"};
        SpinnerAdapter adapterPlace = new SpinnerAdapter(this,places);
        adapterPlace
                .setDropDownViewResource(android.R.layout.simple_gallery_item);
//        spGender.setSelection(1, true);
        spGender.setAdapter(adapterPlace);

////        spGender.setVisibility(View.INVISIBLE);
        spGender.setOnItemSelectedListener(listener);
    }
    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {
            etGender.setText(" ");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            etGender.setText(R.string.text_gender);
        }//被呼叫時代表下拉式選單有問題
    };


    //birthDay
    //----------------------------------------------------------------------------
    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
//        updateDisplay();

    }
    private void updateDisplay() {
        etBirthday.setText(new StringBuilder().append(mYear).append("-")
                .append(pad(mMonth + 1)).append("-").append(pad(mDay)));
    }

    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDisplay();
    }
    public static class DatePickerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MemberActivity memberactivity = (MemberActivity) getActivity();
            return new DatePickerDialog(
                    memberactivity, memberactivity, memberactivity.mYear, memberactivity.mMonth,
                    memberactivity.mDay);
        }
    }
    public void onDateClick(View view) {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }
    //----------------------------------------------------------------------------
    //birthDay

    public void onConfirmClick(View view) {
        boolean isValid =
                mb.isValidAccount(etUser) & mb.isValidPassword(etPassword);
        if (!isValid) {
            return;
        }
//
//        String user = etUser.getText().toString();
//        String password = etPassword.getText().toString();
//        String birthday = etBirthday.getText().toString();
        //將帳號密碼打包傳送到資料庫驗證
//        Intent intent = new Intent(this, DAO);
//        Bundle bundle = new Bundle();
//        bundle.putString("user", user);
//        bundle.putString("password", password);
//        bundle.putString("birthday", birthday);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

}
