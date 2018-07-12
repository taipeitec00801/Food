package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.AppModel.CommentForApp;
import com.example.food.R;
import com.example.food.Sort.SortAsActivity;
import com.ldoublem.thumbUplib.ThumbUpView;

import java.util.List;
import java.util.Objects;

public class Information extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button5;
    private RecyclerView recyclerView;
    ThumbUpView  tpv;
    private TextView mediumText , tv , comment_info;
    private CommentForApp cfa;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        cfa = (CommentForApp) bundle.get("commen");
        setContentView(R.layout.activity_comment_information);
        goview();
        initContent();

        mediumText = findViewById(R.id.MediumText);
        comment_info = findViewById(R.id.information_comment_tv);
        mediumText.setText(cfa.getUserNickName());
        comment_info.setText(cfa.getComment());

//          愛心按鈕
        tpv =findViewById(R.id.tpv);
        tpv.setLike();
        tv =findViewById(R.id.tv);
        tv.setText(cfa.getCommentRecomCount());
        tpv.setUnLikeType(ThumbUpView.LikeType.broken);
        tpv.setCracksColor(Color.WHITE);
        tpv.setFillColor(Color.rgb(255, 0, 0));
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
        tpv.Like();
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

    private void initContent() {
        Toolbar toolbar = findViewById(R.id.CommentInformationToolbar);
//        toolbar.setTitle(R.string.text_insert_Comment);
        toolbar.setTitle(getString(R.string.text_information));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


}