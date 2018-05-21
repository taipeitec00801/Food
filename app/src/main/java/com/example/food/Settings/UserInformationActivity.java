package com.example.food.Settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.R;

import java.sql.Blob;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay;
    private String x = null,y = null;
    private TextView tvUserPassword, tvUserNickname, tvUserGender;
    private final String testUserId = "taipeitec00801@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        findById();
        initContent();
        selectCardView();
        confirmButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 讀取  資料庫內的會員資料
        showMemberData(testUserId);
    }

    private void findById() {
        tvUserPassword = findViewById(R.id.tvUserPassword);
        tvUserNickname = findViewById(R.id.tvUserNickname);
        tvUserGender = findViewById(R.id.tvUserGender);
    }

    private void confirmButton() {
        Button btUserDataSetting = findViewById(R.id.btUserDataSetting);
        btUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("confirmButton","true");
            }
        });
    }

    private void selectCardView() {
        /* 個人頭像 */
        CircleImageView cvUserImage = findViewById(R.id.cvUserImage);
        cvUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cvUserImage.src
            }
        });

        /* 密碼 */
        CardView cvUserPassword = findViewById(R.id.cvUserPassword);
        cvUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.textPassword)
                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .inputRange(6,12,0)
                        .widgetColorRes(R.color.colorBody)
                        .input(0, 0,  new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                x = String.valueOf(input);
                                Log.e("CharSequence input",x);
                                // 確認密碼 視窗
                                new MaterialDialog.Builder(UserInformationActivity.this)
                                        .title(R.string.confirmPassword)
                                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                        .widgetColorRes(R.color.colorBody)
                                        .input(0, 0,  new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                y = String.valueOf(input);
                                                Log.e("CharSequence input2",y);
                                                tvUserPassword.setText(input);
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });
        /* 暱稱 */
        CardView cvUserNickname = findViewById(R.id.cvUserNickname);
        cvUserNickname.setOnClickListener(new View.OnClickListener() {
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
        CardView cvUserGender = findViewById(R.id.cvUserGender);
        cvUserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.text_gender)
                        .items(R.array.genderArray)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        discernMemberGender(which);
                                        break;
                                    default:
                                        dialog.cancel();
                                        break;
                                }
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

    private void showMemberData(String userId) {
        final MemberDAO memberDAO = new MemberDAO(UserInformationActivity.this);
        List<Member> memberList = memberDAO.userInformation(userId);
        Log.e("memberList",String.valueOf(memberList));

//        Member member = new Member(userId);
//        String password = memberList.getPassword();
//        String nickName = member.getNickName();
//        Date birthday = member.getBirthday();
//        int gender = member.getGender();
//        discernMemberGender(gender);
//
//        Blob portrait = member.getPortrait();


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

    // 分析 性別
    private void discernMemberGender(int gender) {
        if (gender ==1) {
            tvUserGender.setText(R.string.textMale);
        } else if (gender == 2) {
            tvUserGender.setText(R.string.textFemale);
        } else {
            tvUserGender.setText(R.string.textDoNotShow);
        }
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
