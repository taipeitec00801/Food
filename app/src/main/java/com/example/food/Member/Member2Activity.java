package com.example.food.Member;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.AppModel.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.Main.MainActivity;
import com.example.food.R;
import com.github.ybq.android.spinkit.SpinKitView;


public class Member2Activity extends AppCompatActivity {
    private Button btConfirm, btLike1, btLike2, btLike3, btLike4, btLike5;
    private Button btLike6, btLike7, btLike8, btLike9, btLike10;
    private int[] nowPreference = new int[10];
    private SharedPreferences prefs;
    private SpinKitView regSpinKit;
    private Member member;
    private boolean insertResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered2);

        findViews();
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);

        //點擊事件
        clickEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btConfirm.setEnabled(true);
    }

    private void clickEvent() {
        //點擊完成
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btConfirm.isEnabled()) {
                    btConfirm.setEnabled(false);
                    regSpinKit.setVisibility(View.VISIBLE);
                    Thread mThread = new Thread(runnable);
                    mThread.start();
                    try {
                        mThread.join();
                    } catch (InterruptedException e) {
                        System.out.println("執行緒被中斷");
                    }
                }
                checkDialog();
            }
        });

        click(btLike1, 0);
        click(btLike2, 1);
        click(btLike3, 2);
        click(btLike4, 3);
        click(btLike5, 4);
        click(btLike6, 5);
        click(btLike7, 6);
        click(btLike8, 7);
        click(btLike9, 8);
        click(btLike10, 9);
    }

    //這裡放執行緒要執行的程式。
    private Runnable runnable = new Runnable() {
        public void run() {
            //編寫Preference 內容
            StringBuilder newPreference = new StringBuilder();
            String preference = "";
            if (nowPreference.length > 0) {
                for (int i = 0; i < nowPreference.length; i++) {
                    if (nowPreference[i] == 1) {
                        newPreference.append(i).append(",");
                    }
                }
                newPreference = new StringBuilder(newPreference.substring(0, newPreference.length() - 1));
                preference = newPreference.toString();
            }
            //接收上頁 的資料 並寫出到 Server
            //寫入到 Server 若成功 將會員資料 寫入偏好設定檔中
            insertResult = false;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Member member = (Member) bundle.getSerializable("MemberDate");
                if (member != null) {
                    Member newMember = new Member(member.getUserAccount(), member.getUserPassword(),
                            member.getNickName(), member.getBirthday(), member.getGender(), preference);
                    //傳會員註冊資料到 Server
                    MemberDAO memberDAO = new MemberDAO(Member2Activity.this);
                    insertResult = memberDAO.insertMemberDate(newMember, member.getPortrait());

                    if (insertResult) {
                        //將會員資料寫入偏好設定檔
                        prefs.edit().putString("userAccount", member.getUserAccount()).apply();
                        prefs.edit().putString("userPassword", member.getUserPassword()).apply();
                        prefs.edit().putString("nickName", member.getNickName()).apply();
                        prefs.edit().putString("birthday", member.getBirthday()).apply();
                        prefs.edit().putInt("gender", member.getGender()).apply();
                        //將會員喜好寫入偏好設定檔
                        prefs.edit().putString("preference", preference).apply();
                    }
                }
            }
        }
    };

    //更新結果 提示視窗
    private void checkDialog() {
        String result = "註冊失敗";
        if (insertResult) {
            result = "註冊成功";
        }
        new MaterialDialog.Builder(Member2Activity.this)
                .title(R.string.textPreferencesSettings)
                .content(result)
                .positiveText(R.string.textIKnow)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        regSpinKit.setVisibility(View.INVISIBLE);
                        btConfirm.setEnabled(true);
                        Log.e("測試--", String.valueOf(insertResult));
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (insertResult) {
                            //註冊成功 並登入
                            prefs.edit().putBoolean("login", true).apply();
                            intent.setClass(Member2Activity.this, MainActivity.class);
                        } else {
                            intent.setClass(Member2Activity.this, MemberActivity.class);
                        }
                        Member2Activity.this.finish();
                        startActivity(intent);
                    }
                }).show();
    }

    private void findViews() {
        regSpinKit = findViewById(R.id.reg_spinKit);
        btConfirm = findViewById(R.id.bt_reg_Confirm);
        btLike1 = findViewById(R.id.bt_reg_Like1);
        btLike2 = findViewById(R.id.bt_reg_Like2);
        btLike3 = findViewById(R.id.bt_reg_Like3);
        btLike4 = findViewById(R.id.bt_reg_Like4);
        btLike5 = findViewById(R.id.bt_reg_Like5);
        btLike6 = findViewById(R.id.bt_reg_Like6);
        btLike7 = findViewById(R.id.bt_reg_Like7);
        btLike8 = findViewById(R.id.bt_reg_Like8);
        btLike9 = findViewById(R.id.bt_reg_Like9);
        btLike10 = findViewById(R.id.bt_reg_Like10);
    }

    public void click(final Button button, final int prefNum) {
        button.setOnClickListener(new View.OnClickListener() {
            private boolean i = true;

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (i) {
                    //選取
                    i = false;
                    v.setBackgroundResource(R.drawable.ripple_sample);
                    button.setTextColor(0xaaaaaaaa);
                    nowPreference[prefNum] = 1;
                } else {
                    //取消選取
                    i = true;
                    v.setBackgroundResource(R.drawable.btn_bg);
                    button.setTextColor(0xffffffff);
                    nowPreference[prefNum] = 0;
                }

            }
        });

    }

}
