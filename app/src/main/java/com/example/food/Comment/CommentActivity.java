package com.example.food.Comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.R;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    public static boolean btcont;
    private View linearlayout_introduce,floatingbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);
        changeview();
     RecyclerView recyclerView=findViewById(R.id.Comment_recycleview);
     recyclerView.setLayoutManager(
             new StaggeredGridLayoutManager(
                     1,StaggeredGridLayoutManager.VERTICAL));
       List<Member> memberList = getMemberList();
        recyclerView.setAdapter(new MemberAdapter(this, memberList));

    }

    private void changeview(){
        linearlayout_introduce=findViewById(R.id.linearlayout_introduce);
        linearlayout_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CommentActivity.this, Introduce.class);
                startActivity(intent);
            }
        });
        floatingbutton=findViewById(R.id.floatingbutton);
        floatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CommentActivity.this, Comment_interface.class);
                startActivity(intent);
            }
        });
    }
    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
        private Context context;
        private List<Member> MembersList;

        MemberAdapter(Context context, List<Member> MembersList) {
            this.context = context;
            this.MembersList = MembersList;
        }


        @Override
        public  MyViewHolder onCreateViewHolder(ViewGroup viewGroup ,int viewType){
            LayoutInflater layoutInflater =LayoutInflater.from(context);
            View itemView =layoutInflater.inflate(R.layout.comment_item,viewGroup,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder ,int position){
            final Member member =MembersList.get(position);
            viewHolder.textView.setText(member.getName());
            viewHolder.imageView.setImageResource(member.getImage());
            viewHolder.textView.setText(member.getMessage());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(CommentActivity.this, Information.class);
                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
                return  MembersList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView imageView;
            TextView messageView;


            MyViewHolder(View itemview){
            super(itemview);

            textView=itemview.findViewById(R.id.customname);
            imageView=itemview.findViewById(R.id.iv);
            messageView=itemview.findViewById(R.id.custommessage);
        }
}
    }

    public List<Member> getMemberList() {
        List<Member> MemberList=new ArrayList<>();
        MemberList.add(new Member("1",R.drawable.boy,"David"));
        MemberList.add(new Member("2",R.drawable.boy,"Mary"));
        return MemberList;
    }
}
