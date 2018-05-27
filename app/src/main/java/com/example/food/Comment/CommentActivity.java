package com.example.food.Comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.food.R;

import java.util.List;

public class CommentActivity extends AppCompatActivity {


    private View linearlayout_introduce,Comment_recycleview,floatingbutton;
    private RecyclerView recyclerView;
    private List<Member> memberList;
//
//    private CommentAdapter commentAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        changeview();
     RecyclerView recyclerView=(RecyclerView)findViewById(R.id.Comment_recycleview);
     recyclerView.setLayoutManager(
             new StaggeredGridLayoutManager(
                     1,StaggeredGridLayoutManager.VERTICAL));
        memberList = getMemberList();
//        recyclerView.setAdapter(new MemberAdapter(this, memberList));

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
        Comment_recycleview=findViewById(R.id.Comment_recycleview);
        Comment_recycleview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CommentActivity.this,Information.class);
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

    public List<Member> getMemberList() {
        return memberList;
    }

//    private class MemberAdapter extends RecyclerView.Adapter {
//        public MemberAdapter(CommentActivity commentActivity, List<Member> memberList) {
//    2018.055完    }
//    }




//    private class CommentAdapter extends
//            RecyclerView.Adapter<CommentAdapter.SortViewHolder> {
//        private Context context;
//        private List<CommentAs> CommentList;
//
//        sortAdapter(Context context, List<SortAs> sortList) {
//            this.context = context;
//            this.sortList = sortList;
//        }
//
//        class SortViewHolder extends RecyclerView.ViewHolder {
//            ImageView imageView ,likeView;
//            TextView tvName,tvLike;
//            SortViewHolder(View itemView) {
//                super(itemView);
//                imageView = itemView.findViewById(R.id.sortAs_item_iv);
//                tvName =  itemView.findViewById(R.id.sortAs_item_tv);
//                tvLike =  itemView.findViewById(R.id.sortAs_item_like_tv);
//                likeView = itemView.findViewById(R.id.sortAs_item_like_iv);
//            }
//        }
//        @Override
//        public int getItemCount() {
//            return sortList.size();
//        }
//
//        @Override
//        public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(context);
//            View itemView = layoutInflater.inflate(R.layout.sort_as_item, viewGroup, false);
//            return new SortViewHolder(itemView);
//        }

//        @Override
//        public void onBindViewHolder(SortViewHolder viewHolder, int position) {
//            SortAs sort = sortList.get(position);
//            viewHolder.likeView.setImageResource(R.drawable.like);
//            viewHolder.imageView.setImageResource(R.drawable.p01);
//            viewHolder.tvName.setText(String.valueOf(sort.getName()));
//            viewHolder.tvLike.setText(String.valueOf(sort.getNumber()));
//            viewHolder.itemView.setElevation(0);
//        }
//}
}