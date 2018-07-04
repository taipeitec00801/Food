package com.example.food.Comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.AppModel.Store;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.example.food.R;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    private View linearlayout_introduce,floatingbutton;
    private RollPagerView mRollViewPager;
    private TextView tv,tv1,tv2,tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mRollViewPager =findViewById(R.id.roll_view_pager);
        tv = findViewById(R.id.tv);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        //get bundle From Map
        Bundle bundle = getIntent().getExtras();
        Store store = (Store) bundle.getSerializable("store");
        Log.d("storeList",""+store.getStoreName());

        tv.setText("店名:"+store.getStoreName());
        tv1.setText("地址:"+store.getStoreAddress());
        tv2.setText("營業時間："+store.getServiceHours());
        tv3.setText("連絡電話:"+store.getStorePhone());


        //设置播放时间间隔
//        mRollViewPager.setPlayDelay(3000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.GRAY,Color.WHITE));
//        new commentTask().execute();

    }

    @Override
    protected void onStart() {
        new commentTask().execute();
        super.onStart();
    }

    class commentTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            changeview();
            RecyclerView recyclerView=findViewById(R.id.Comment_recycleview);
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(
                            1,StaggeredGridLayoutManager.VERTICAL));
            List<Member> memberList = getMemberList();
            recyclerView.setAdapter(new MemberAdapter(CommentActivity.this, memberList));
            return null;
        }
    }

    private class TestNormalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.drawable.food,
                R.drawable.food1,
                R.drawable.food2,
                R.drawable.food,
        };
        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }
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
//                Map intent還沒連結
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
            View itemView =LayoutInflater.from(context)
                        .inflate(R.layout.comment_item,viewGroup,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position){
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
                messageView=itemview.findViewById(R.id.Usermessage);
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
