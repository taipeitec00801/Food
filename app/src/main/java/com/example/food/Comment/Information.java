package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.AppModel.CommentForApp;
import com.example.food.AppModel.Message;
import com.example.food.R;
import com.example.food.Sort.SortAsActivity;
import com.ldoublem.thumbUplib.ThumbUpView;

import java.util.List;
import java.util.Objects;

public class Information extends AppCompatActivity {

    private Button gobutton;
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
        gobutton=findViewById(R.id.gobutton);
        gobutton.setOnClickListener(new View.OnClickListener() {
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
        toolbar.setTitle(getString(R.string.text_store_info));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    //留言
    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
        private Context context;
        private List<Message> mfaList;

        MemberAdapter(Context context, List<Message> mfaList) {
            this.context = context;
            this.mfaList = mfaList;
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView messageView;

            MyViewHolder(View itemview) {
                super(itemview);
                imageView = itemview.findViewById(R.id.Userpicture);
                messageView = itemview.findViewById(R.id.message);
            }
        }
        @Override
        public Information.MemberAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.message_item, viewGroup, false);
            return new Information.MemberAdapter.MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(Information.MemberAdapter.MyViewHolder viewHolder, int position) {
            final Message mfa = mfaList.get(position);
//            viewHolder.imageView.setImageResource(member.getImage());
            viewHolder.messageView.setText(mfa.getMessage());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("commen" , mfa);
//                    Intent intent = new Intent(Comment_interface.this, Information.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mfaList.size();
        }
    }
}