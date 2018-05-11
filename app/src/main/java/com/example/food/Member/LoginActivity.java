package com.example.food.Member;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.food.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser = findViewById(R.id.etUser), etPassword, etForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        initContent();
    }

    private void initContent() {
    }

    private void findViews() {
        etPassword = findViewById(R.id.etPassword);
        etForgetPassword = findViewById(R.id.etForgetPassword);
    }
    private boolean isValid(EditText editText) {
        Pattern pattern = Pattern.compile("[a-pA-P]&[0-9]]");
        String text = etPassword.getText().toString();

        if (!text.matches(String.valueOf(pattern))) {
            editText.setError("格式錯誤");
            return false;
        } else {
            return true;
        }
        //直接帶入etUser有錯誤需要注意
    }

    public void onSubmitClick(View view) {
        boolean isValid =
                isValid(etUser) & isValid(etPassword);
        if (!isValid) {
            return;
        }

        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();
        //將帳號密碼打包傳送到資料庫驗證
//        Intent intent = new Intent(this, DAO);
//        Bundle bundle = new Bundle();
//        bundle.putString("user", user);
//        bundle.putString("password", password);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    public void onForgetClick(View view) {

    }



}
