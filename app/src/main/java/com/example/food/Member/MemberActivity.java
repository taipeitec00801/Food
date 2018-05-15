package com.example.food.Member;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

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
        mb.inputFilter(etPassword);
        mb.inputFilter(etUser);
    }
//    class NewArrayAdapter extends ArrayAdapter {
//        private Context mContext;
//        private String [] mStringArray;
//        public NewArrayAdapter(Context context, int simple_spinner_item, String[] stringArray) {
//            super(context, android.R.layout.simple_spinner_item, stringArray);
//            mContext = context;
//            mStringArray=stringArray;
//        }


//        public View getDropDownView(int position, View convertView, ViewGroup parent) {
//            //修改Spinner展開後的字體顏色
//            if (convertView == null) {
//                LayoutInflater inflater = LayoutInflater.from(this);
//                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
//            }
//
//            //此處text1是Spinner默認的用來顯示文字的TextView
//            TextView tv =  convertView.findViewById(android.R.id.text1);
//            tv.setText(places[position]);
//            tv.setTextSize(22f);
//            tv.setTextColor(Color.RED);
//
//            return convertView;
//
//        }
//
//
//        public View getView(AdapterView<?> parent, View view, int pos, long id) {
//            // 修改Spinner選擇後結果的字體顏色
//            if (view == null) {
//                LayoutInflater inflater = LayoutInflater.from(this);
//                convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
//            }
//
//            //此處text1是Spinner默認的用來顯示文字的TextView
//            EditText tv =  convertView.findViewById(R.id.etGender);
//            tv.setText(adapterPlace[position]);
//            tv.setTextSize(18f);
//            tv.setTextColor(Color.BLUE);
//            return convertView;
//        }
//
////    }
//




    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {
            etGender.setText("");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            etGender.setText(R.string.text_gender);
        }//被呼叫時代表下拉式選單有問題
    };
    private void showSpinner() {
        String[] places = {"Gender", "男姓", "女姓"};
        ArrayAdapter<String> adapterPlace = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, places);
        adapterPlace
                .setDropDownViewResource(android.R.layout.simple_gallery_item);
        spGender.setAdapter(adapterPlace);
        spGender.setSelection(0, true);
////        spGender.setVisibility(View.INVISIBLE);
        spGender.setOnItemSelectedListener(listener);

    }
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
                mb.isValid(etUser) & mb.isValid(etPassword);
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
