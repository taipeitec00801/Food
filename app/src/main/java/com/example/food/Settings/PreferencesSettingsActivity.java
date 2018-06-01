package com.example.food.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.food.R;

public class PreferencesSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_settings);

        initContent();


    }

    private void initContent() {
        Toolbar settingsPreferencesToolbar = findViewById(R.id.settingsPreferencesToolbar);
        settingsPreferencesToolbar.setTitle(R.string.textPreferencesSettings);

        setSupportActionBar(settingsPreferencesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
