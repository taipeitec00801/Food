package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.food.R;

public class Comment_interface extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_interface);
        review();
    }

    private void review() {
        button2=findViewById(R.id.Collection);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Comment_interface.this,CommentActivity.class);
                startActivity(intent);
            }
        });
    }
}