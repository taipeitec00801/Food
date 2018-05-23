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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay, newGender;
    private Member member;
    private String newPassword, newNickName, newBirthday;
    private TextView tvUserNickname, tvUserBirthday;
    private static final String testUserAccount = "taipeitec00801@gmail.com";
    private MemberDAO memberDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        findById();
        initContent();

        // 讀取  資料庫內的會員資料
        memberDAO = new MemberDAO(UserInformationActivity.this);
        member = memberDAO.findMemberByUserAccount(testUserAccount);
        showMemberData(member);


        selectCardView();
//        confirmButton();

        Button cvUserDataSetting = findViewById(R.id.cvUserDataSetting);
        cvUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberDAO.updateMemberDate(testUserAccount, newPassword, newNickName,
                        newBirthday, newGender);
            }
        });

    }


    private void findById() {
        tvUserBirthday = findViewById(R.id.tvUserBirthday);
        tvUserNickname = findViewById(R.id.tvUserNickname);
    }

//    private void confirmButton() {
//        CardView cvUserDataSetting = findViewById(R.id.cvUserDataSetting);
//        cvUserDataSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("confirmButton","true");
//                memberDAO.updateMemberDate(testUserAccount, newPassword, newNickName,
//                                        newBirthday, newGender);
//            }
//        });
//    }

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
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                //  密碼是否與資料庫內相同
                                discernMemberPassword(newPassword,input.toString());

                                // 確認密碼 視窗
                                new MaterialDialog.Builder(UserInformationActivity.this)
                                        .title(R.string.confirmPassword)
                                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                        .widgetColorRes(R.color.colorBody)
                                        .input(0, 0,  new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                discernMemberPassword(newPassword,input.toString());
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
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                newNickName = input.toString();
                                tvUserNickname.setText(newNickName);
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
            assert uiActivity != null;
            return new DatePickerDialog(uiActivity, uiActivity,
                                    uiActivity.mYear, uiActivity.mMonth, uiActivity.mDay);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month+1;
        mDay = day;
        newBirthday = mYear + "-" + pad(mMonth) + "-" + pad(mDay);
        tvUserBirthday.setText(newBirthday);
    }

    //判斷 是否大於十位數 若為個位數 補0
    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    private void showMemberData(Member member) {
        TextView tvUserId = findViewById(R.id.tvUserId);
        tvUserId.setText(testUserAccount);

        discernMemberPassword(member.getUserPassword(),"");
        newNickName = member.getNickName();
        tvUserNickname.setText(newNickName);
        newBirthday = member.getBirthday();
        tvUserBirthday.setText(newBirthday);
        discernMemberGender(member.getGender());
    }

    // 載入此介面時，判斷會員是否有 Portrait 若沒有 顯示預設頭像
    private void showMemberPortrait() {
//        if () {
//
//        }  else {
//
//        }
    }

    // 判斷密碼 original與input相關關係
    private void discernMemberPassword(String original, String input) {
        TextView tvUserPassword = findViewById(R.id.tvUserPassword);
        if (!original.equals(input) && input.length() !=0) {
            tvUserPassword.setText(input);
            newPassword = input;
        } else {
            tvUserPassword.setText(original);
            newPassword = original;
        }
    }

    // 分析 性別
    private void discernMemberGender(int gender) {
        TextView tvUserGender = findViewById(R.id.tvUserGender);
        newGender = gender;
        if (gender == 0) {
            tvUserGender.setText(R.string.textFemale);
        } else {
            tvUserGender.setText(R.string.textMale);
        }
    }

}
