package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.food.R;
import com.ldoublem.thumbUplib.ThumbUpView;

public class Information extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button5;

    ThumbUpView  tpv;
    TextView tv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_information);
        goview();

        tpv =findViewById(R.id.tpv);
        tpv.setLike();
        tv =findViewById(R.id.tv);

        tpv.setUnLikeType(ThumbUpView.LikeType.broken);
        tpv.setCracksColor(Color.WHITE);
        tpv.setFillColor(Color.rgb(11, 200, 77));
        tpv.setEdgeColor(Color.rgb(33, 3, 219));
        tpv.setOnThumbUp(new ThumbUpView.OnThumbUp() {
            @Override
            public void like(boolean like) {
                if (like) {
                    tv.setText(String.valueOf(Integer.valueOf(tv.getText().toString()) + 1));
                } else {
                    tv.setText(String.valueOf(Integer.valueOf(tv.getText().toString()) - 1));

                }
            }
        });

    }
    public void like(View view) {
        tpv.Like();
    }

    public void unlike(View view) {
        tpv.UnLike();
    }


    private void goview(){
        button5=findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Information.this,CommentActivity.class);
                startActivity(intent);
            }
        });
    }

}