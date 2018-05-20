package com.example.food.Settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.R;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        // 讀取  資料庫內的會員資料
        showMemberData();
        initContent();
        selectCardView();
    }

    private void selectCardView() {
        /* 個人頭像 */
        CircleImageView cvUserImage = findViewById(R.id.cvUserImage);
        cvUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 密碼 */
        CardView cvUserPassword = findViewById(R.id.userPassword);
        cvUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.textNickname)
                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .inputRange(6,12,0)
                        .widgetColorRes(R.color.colorBody)
                        .input(0, 0,  new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                // 確認密碼 視窗
                                new MaterialDialog.Builder(UserInformationActivity.this)
                                        .title(R.string.textNickname)
                                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                        .widgetColorRes(R.color.colorBody)
                                        .input(0, 0,  new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                            }
                                        }).show();
                            }
                        }).show();
            }
        });
        /* 暱稱 */
        CardView cvUserNickname = findViewById(R.id.userNickname);
        cvUserNickname.setOnClickListener(new View.OnClickListener() {
            TextView tvUserNickname = findViewById(R.id.tvUserNickname);
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.textNickname)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .widgetColorRes(R.color.colorBody)
                        .input(0, 0,  new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                tvUserNickname.setText(input);
                            }
                        }).show();
            }
        });
        /* 生日 */
        CardView cvUserBirthday = findViewById(R.id.cvUserBirthday);
        cvUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
        /* 性別 */
        CardView cvUserGender = findViewById(R.id.userGender);
        cvUserGender.setOnClickListener(new View.OnClickListener() {
            TextView tvUserGender = findViewById(R.id.tvUserGender);
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.text_gender)
                        .items(R.array.genderArray)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                tvUserGender.setText(text);
                                return true;
                            }
                        })
                        .positiveText(R.string.text_btYes)
                        .show();
            }
        });

    }

    private void initContent() {
        Toolbar settingsUserInformationToolbar = findViewById(R.id.settingsUserInformationToolbar);
        settingsUserInformationToolbar.setTitle(R.string.settingUserData);

        setSupportActionBar(settingsUserInformationToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    /* 選擇日期的跳脫視窗 */
    public static class DatePickerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            UserInformationActivity uiActivity = (UserInformationActivity) getActivity();
            DatePickerDialog datePickerDialog = new DatePickerDialog(uiActivity, uiActivity,
                                    uiActivity.mYear, uiActivity.mMonth, uiActivity.mDay);
            return datePickerDialog;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDisplay();
    }

    private void showMemberData() {
//        showMemberId();
//        showMemberPassword();
        showMemberBirthday();

    }

    // 讀取  資料庫內的會員生日
    private void showMemberBirthday() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    private void updateDisplay() {
        TextView tvUserBirthday = findViewById(R.id.tvUserBirthday);
        tvUserBirthday.setText(new StringBuilder().append(mYear).append("-")
                .append(pad(mMonth+1)).append("-").append(mDay));
    }

    // 若數字有十位數 直接顯示 / 若是個位數 補0再顯示
    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + String.valueOf(number);
        }
    }
}
