package com.example.food.Member;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private EditText etUser , etPassword;
    private Button btSubmit, btForgetPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btn_sign_in;
    private Button btn_sign_out;
    MemderBeanActivity mb = new MemderBeanActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btSubmit = findViewById(R.id.btSubmit);
        findViews();
        googleSignIn();
        getUserInfo();
        btForgetPassword.setOnClickListener(btflistener);
//        initContent();
        btn_sign_in .setOnClickListener(this);
        btSubmit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MemberActivity.class);
                startActivity(intent);

            }
        });
    }


    //取得activity_login的ID
    private void findViews() {
        etPassword = findViewById(R.id.etPassword);
        etUser = findViewById(R.id.etUser);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_out = findViewById(R.id.btn_sign_out);
        btForgetPassword = findViewById(R.id.btForgetPassword);
        mb.inputFilter(etPassword);
        mb.inputFilter(etUser);
        //透過mb.inputFilter()來限制帳密字數;
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
                signIn();
            case R.id.btn_sign_out:
                signOut();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
            Log.d("Info",
                    "DisplayName = "+account.getDisplayName()+
                            "; GivenName = " + account.getGivenName()+
                            "; FamilyName = " + account.getFamilyName()+
                            "; Email = " + account.getEmail()+
                            "; getId = " + account.getId()+
                            "; getPhotoUrl = " + account.getPhotoUrl());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
//    private void googleToken(){
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                // Specify the CLIENT_ID of the app that accesses the backend:
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                // Or, if multiple clients access the backend:
//                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                .build();
//
//// (Receive idTokenString by HTTPS POST)
//
//        GoogleIdToken idToken = verifier.verify(idTokenString);
//        if (idToken != null) {
//            Payload payload = idToken.getPayload();
//
//            // Print user identifier
//            String userId = payload.getSubject();
//            System.out.println("User ID: " + userId);
//
//            // Get profile information from payload
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
//
//            // Use or store profile information
//            // ...
//
//        } else {
//            System.out.println("Invalid ID token.");
//        }
//    }
//    public void checkSigIn(){
//    GoogleSignIn.silentSignIn()
//            .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
//        @Override
//        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//            handleSignInResult(task);
//        }
//    });
//    }
/*----------------------------------------------------------------------------------------------*/
//    @SuppressLint("StringFormatInvalid")
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
    // [END signOut]

    // [START revokeAccess]
//    private void revokeAccess() {
//        mGoogleSignInClient.revokeAccess()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
//                        updateUI(null);
//                        // [END_EXCLUDE]
//                    }
//                });
//    }
    // [END revokeAccess]
    public void getUserInfo() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Toast.makeText(LoginActivity.this, ""+personName,Toast.LENGTH_SHORT).show();
            Log.d("UserInfo", "personName = " + personName);
            Log.d("UserInfo", "personGivenName = " + personGivenName);
            Log.d("UserInfo", "personFamilyName = " + personFamilyName);
            Log.d("UserInfo", "personEmail = " + personEmail);
            Log.d("UserInfo", "personId = " + personId);
            Log.d("UserInfo", "personPhoto = " + personPhoto);

        }else {
            Toast.makeText(LoginActivity.this, "null",Toast.LENGTH_SHORT).show();
        }
    }
//    public void onUserClick(){
//
//    }
//
//    //從MemderBeanActivity取得帳密布林值
    public void onSubmitClick(View view) {
        boolean isValid =
                mb.isValid(etUser) & mb.isValid(etPassword);
        if (!isValid) {
            return;
        }
//
//        String user = etUser.getText().toString();
//        String password = etPassword.getText().toString();
        //將帳號密碼打包傳送到資料庫驗證
//        Intent intent = new Intent(this, DAO);
//        Bundle bundle = new Bundle();
//        bundle.putString("user", user);
//        bundle.putString("password", password);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }
    private Button.OnClickListener btflistener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btForgetPassword){
                final EditText edit = new EditText(LoginActivity.this);
                //產生視窗物件
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("取回密碼")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage("請輸入信箱:example@gmail.com")//設定顯示的文字
                        .setView(edit)
                        .setPositiveButton("送出",new DialogInterface.OnClickListener(){
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
