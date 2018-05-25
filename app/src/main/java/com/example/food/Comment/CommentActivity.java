package com.example.food.Comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.food.R;

public class CommentActivity extends AppCompatActivity {


    private View text3,mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        changeview();


    }
    //        findViewById(R.id.mCardView1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this,Comment.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//            }
//
//        });
    private void changeview(){
        mCardView=findViewById(R.id.cv);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CommentActivity.this, Information.class);
                startActivity(intent);
            }
        });
        text3=findViewById(R.id.namelayout);
        text3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CommentActivity.this,Comment_interface.class);
                startActivity(intent);

            }
        });
    }


}




