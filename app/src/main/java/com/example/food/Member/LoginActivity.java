package com.example.food.Member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.food.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser , etPassword;
    private TextView  etForgetPassword;
    private Button btSubmit;
    MemderBeanActivity mb = new MemderBeanActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btSubmit = findViewById(R.id.btSubmit);
        findViews();
//        initContent();
        btSubmit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MemberActivity.class);
                startActivity(intent);

            }
        });
    }
    public void onUserClick(){

    }
    //取得activity_login的ID
    private void findViews() {
        etPassword = findViewById(R.id.etPassword);
        etUser = findViewById(R.id.etUser);
        etForgetPassword = findViewById(R.id.etForgetPassword);
        mb.inputFilter(etPassword);
        mb.inputFilter(etUser);
        //透過mb.inputFilter()來限制帳密字數;
    }
    //從MemderBeanActivity取得帳密布林值
    public void onSubmitClick(View view) {
        boolean isValid =
                mb.isValid(etUser) & mb.isValid(etPassword);
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
