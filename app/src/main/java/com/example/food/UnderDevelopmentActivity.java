package com.example.food;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

public class UnderDevelopmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_development);

        initContent();
    }

    private void initContent() {
        Toolbar underConstructionToolbar = findViewById(R.id.UnderConstructionToolbar);
        underConstructionToolbar.setTitle(R.string.textGift);

        setSupportActionBar(underConstructionToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}
