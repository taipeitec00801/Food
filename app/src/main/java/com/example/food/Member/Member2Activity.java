package com.example.food.Member;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.food.R;


public class Member2Activity extends AppCompatActivity {
    private  Button btConfirm, btLike1, btLike2, btLike3, btLike4, btLike5;
    private  Button btLike6, btLike7, btLike8, btLike9, btLike10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered2);

        findViews();
        click(btLike1);
        click(btLike2);
        click(btLike3);
        click(btLike4);
        click(btLike5);
        click(btLike6);
        click(btLike7);
        click(btLike8);
        click(btLike9);
        click(btLike10);
    }

    private void findViews() {
        btConfirm = findViewById(R.id.btNext);
        btLike1 = findViewById(R.id.btLike1);
        btLike2 = findViewById(R.id.btLike2);
        btLike3 = findViewById(R.id.btLike3);
        btLike4 = findViewById(R.id.btLike4);
        btLike5 = findViewById(R.id.btLike5);
        btLike6 = findViewById(R.id.btLike6);
        btLike7 = findViewById(R.id.btLike7);
        btLike8 = findViewById(R.id.btLike8);
        btLike9 = findViewById(R.id.btLike9);
        btLike10 = findViewById(R.id.btLike10);
    }

//    public void onClick(Button button) {
//        button.setBackgroundResource(R.drawable.btn_bg_set);
//    }
//
    public void click(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            private boolean i = true;
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(i){
                    v.setBackgroundResource(R.drawable.ripple_sample);
//                    v.setTextDirection(R.);
//                    v.setBackgroundColor(R.color.lgColorText);
                    i = false;
                }else{
                    v.setBackgroundResource(R.drawable.btn_bg);
                    i = true;
                }

            }
        });

    }

}
