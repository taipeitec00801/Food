package com.example.food.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.food.R;

import java.util.Objects;

public class UserInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        initContent();
    }

    private void initContent() {
        Toolbar settingsUserInformationToolbar = findViewById(R.id.settingsUserInformationToolbar);
        settingsUserInformationToolbar.setTitle(R.string.settingUserData);

        setSupportActionBar(settingsUserInformationToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}
