package com.example.food.Member;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.example.food.R;

public class MemberActivity extends AppCompatActivity {
    private Toolbar toolbar;
    //    private EditText
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        initContent();
    }
    private void initContent() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.text_btSubmit);
    }
}

