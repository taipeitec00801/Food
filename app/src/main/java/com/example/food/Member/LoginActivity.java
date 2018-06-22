package com.example.food.Member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.AppModel.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.Main.MainActivity;
import com.example.food.Other.InputFormat;
import com.example.food.Other.MySharedPreferences;
import com.example.food.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private EditText etUser, etPassword;
    private Button btLogin, btSubmit, btForgetPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btn_sign_in;
    private MemberDAO memberDAO;
    private Member member;
    private InputFormat inputFormat;
    private SpinKitView loginSpinKit;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findById();

        //透過inputFormat.inputFilter()來限制帳密字數;
        inputFormat = new InputFormat();
        inputFormat.inputFilter(etUser, 40);
        inputFormat.inputFilter(etPassword, 12);

        //Google 登入
//        googleSignIn();
//        getUserInfo();
//        btn_sign_in.setOnClickListener(this);
        // 忘記密碼
        btForgetPassword.setOnClickListener(btflistener);
        //註冊
        btSubmit.setOnClickListener(this);
        //登入
        btLogin.setOnClickListener(this);
    }

    //這裡放執行緒要執行的程式。
    private Runnable runnable = new Runnable() {
        public void run() {
            memberDAO = new MemberDAO(LoginActivity.this);
            //傳送帳號與密碼到 Server 回傳登入結果
            boolean isUser = memberDAO.userLogin(etUser.getText().toString().trim(),
                    etPassword.getText().toString().trim());
            prefs.edit().putBoolean("login", isUser).apply();
            Log.e("測試--loginActivity",String.valueOf(isUser));
            if (isUser) {
                //登入成功抓資料
                member = memberDAO.getUserDate(etUser.getText().toString().trim());
                boolean inputPrefOk = MySharedPreferences.inputSharedPreferences(prefs, member);

                //抓資料與寫入偏好設定檔成功跳頁
                if (inputPrefOk) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.finish();
                    startActivity(intent);
                } else {
                    //失敗跳出訊息視窗
                    loginFail();
                }
            } else {
                loginFail();
            }
        }
    };

    private void inputImgStorage() {
    }

    //登入 失敗 訊息提示窗
    public void loginFail() {
        new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.textMemberLogin)
                .content("登入失敗，請從新登入")
                .positiveText(R.string.textIKnow)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        btLogin.setEnabled(true);
                        btSubmit.setEnabled(true);
                        loginSpinKit.setVisibility(View.INVISIBLE);
                    }
                }).show();
    }

    //取得activity_login的ID
    private void findById() {
        btLogin = findViewById(R.id.bt_login_Login);
        btSubmit = findViewById(R.id.bt_login_Submit);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btForgetPassword = findViewById(R.id.btForgetPassword);
        etUser = findViewById(R.id.et_login_User);
        etPassword = findViewById(R.id.et_login_Password);
        loginSpinKit = findViewById(R.id.login_spinKit);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);

    }

    public void googleSignIn() {
        GoogleSignInOptions gso
                = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        btLogin.setEnabled(true);
        btSubmit.setEnabled(true);

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        Log.d("getaccount", "getaccount" + account);
        // [END on_start_sign_in]
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
//                signIn();
                break;
            case R.id.btn_sign_out:
//                signOut();
                break;
            case R.id.bt_login_Submit:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MemberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.bt_login_Login:
//                boolean isValid = inputFormat.isValidAccount(etUser) & inputFormat.isValidPassword(etPassword);
//                if (isValid) {
                    loginSpinKit.setVisibility(View.VISIBLE);
                    if (btLogin.isEnabled()) {
                        Thread mThread = new Thread(runnable);
                        mThread.start();
                    }
                    btLogin.setEnabled(false);
                    btSubmit.setEnabled(false);
//                }
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println(requestCode);
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
//            Log.d("測試--Info",
//                    "DisplayName = " + account.getDisplayName() +
//                            "; GivenName = " + account.getGivenName() +
//                            "; FamilyName = " + account.getFamilyName() +
//                            "; Email = " + account.getEmail() +
//                            "; getId = " + account.getId() +
//                            "; getPhotoUrl = " + account.getPhotoUrl());
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.d("測試--" + TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
//        }
//    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            findViewById(R.id.btn_sign_in).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_sign_out).setVisibility(View.VISIBLE);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
                        updateUI(null);
//                        // [END_EXCLUDE]
                    }
                });
    }

//    public void getUserInfo() {
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            Toast.makeText(LoginActivity.this, "" + personName, Toast.LENGTH_SHORT).show();
//            Log.d("測試--UserInfo", "personName = " + personName);
//            Log.d("測試--UserInfo", "personGivenName = " + personGivenName);
//            Log.d("測試--UserInfo", "personFamilyName = " + personFamilyName);
//            Log.d("測試--UserInfo", "personEmail = " + personEmail);
//            Log.d("測試--UserInfo", "personId = " + personId);
//            Log.d("測試--UserInfo", "personPhoto = " + personPhoto);
//
//        } else {
//            Toast.makeText(LoginActivity.this, "null", Toast.LENGTH_SHORT).show();
//        }
//    }

    private Button.OnClickListener btflistener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btForgetPassword) {
                final EditText edit = new EditText(LoginActivity.this);
                //產生視窗物件
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("取回密碼")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage("請輸入信箱:example@gmail.com")//設定顯示的文字
                        .setView(edit)
                        .setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            //                                    finish();
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        }
    };
}
