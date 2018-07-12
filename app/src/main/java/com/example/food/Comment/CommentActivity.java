package com.example.food.Comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.AppModel.CommentForApp;
import com.example.food.AppModel.Store;
import com.example.food.AppModel.Member;

import com.example.food.DAO.CommentDAO;
import com.example.food.DAO.MemberDAO;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.CommonTask;
import com.example.food.DAO.task.ImageTaskOIB;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.DynamicPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.example.food.R;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    private View linearlayout_introduce,floatingbutton;
    private RollPagerView mRollViewPager;
    private TextView tv,tv1,tv2,tv3;
    private Store store;
    private ImageTaskOIB storeImgTask;
    private CommonTask commonTask;
    private List<CommentForApp> commentforapp;
    private Integer storeId = null;
    private MemberAdapter memberAdapter;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private RecyclerView recyclerView;
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
        store = (Store) bundle.getSerializable("store");
        Log.d("storeList",""+store.getStoreName());
        storeId = store.getStoreId();
        tv.setText("店名:"+store.getStoreName());
        tv1.setText("地址:"+store.getStoreAddress());
        tv2.setText("營業時間："+store.getServiceHours());
        tv3.setText("連絡電話:"+store.getStorePhone());

        recyclerView = findViewById(R.id.Comment_recycleview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
      //  recyclerView.setAdapter(memberAdapter);
        //设置播放时间间隔
//        mRollViewPager.setPlayDelay(3000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.GRAY,Color.WHITE));
//        new commentTask().execute();

        mThread=new HandlerThread("bb");
        mThread.start();
        mThreadHandler=new Handler(mThread.getLooper());
        mThreadHandler.post(r1);
    }
    private Runnable r1 = new Runnable(){
        public void run(){
            //這裡放執行緒要執行的程式。
//            rvUp.setAdapter(searchAdapter);
//            CommentDAO commentDAO = new CommentDAO(CommentActivity.this);
//            commentForApp = commentDAO.getStoreCommById(storeId);
//
//            memberAdapter = new MemberAdapter(CommentActivity.this, commentForApp);
//            Log.e("commentForApp" , commentForApp.get(0).getUserNickName());
            comment();
            mThreadHandler.post(r3);
        }
    };
    private Runnable r3 = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // update TextView here!
                    Log.e("r3" , "success");
                    //recyclerView.setAdapter(memberAdapter);
                   // memberAdapter = new MemberAdapter(CommentActivity.this, commentForApp);
                    recyclerView.setAdapter(memberAdapter);
                    //memberAdapter.notifyDataSetChanged();
                }
            });

        }
    };

    public void comment(){
        CommentDAO commentDAO = new CommentDAO(CommentActivity.this);
        commentforapp = commentDAO.getStoreCommById(storeId);
        memberAdapter = new MemberAdapter(CommentActivity.this, commentforapp);
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
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    memberAdapter = new MemberAdapter(CommentActivity.this, commentForApp);
//                    RecyclerView recyclerView=findViewById(R.id.Comment_recycleview);
//                    recyclerView.setAdapter(memberAdapter);
//                }
//            });

//            recyclerView.setLayoutManager(
//                    new StaggeredGridLayoutManager(
//                            1,StaggeredGridLayoutManager.VERTICAL));
//            //List<Member> memberList = getMemberList();
//
//            recyclerView.setAdapter(new MemberAdapter(CommentActivity.this, commentForApp));
            return null;
        }
    }



    private class TestNormalAdapter extends DynamicPagerAdapter {
//        private int[] imgs = {
//                R.drawable.food,
//                R.drawable.food1,
//                R.drawable.food2,
//                R.drawable.food,
//        };
        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            String url = Common.URL + "/appGetAllImages";
            int id = store.getStoreId();
            storeImgTask = new ImageTaskOIB(url,id,position,view);
            storeImgTask.execute();
           // view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }


        @Override
        public int getCount() {
            return 4;
        }
    }

    private void changeview(){
//        linearlayout_introduce=findViewById(R.id.linearlayout_introduce);
//        linearlayout_introduce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(CommentActivity.this, Introduce.class);
//                startActivity(intent);
//            }
//        });
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
    private class MemberAdapter extends
            RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
        private Context context;
        private List<CommentForApp> commentForApp;

        MemberAdapter(Context context, List<CommentForApp> commentForApp) {
            this.context = context;
            this.commentForApp = commentForApp;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView messageView , likeView , textView;


            MyViewHolder(View itemview){
                super(itemview);
                likeView = itemview.findViewById(R.id.comment_RecomCount);
                textView=itemview.findViewById(R.id.customname);
                imageView=itemview.findViewById(R.id.iv);
                messageView=itemview.findViewById(R.id.Usermessage);
            }
        }

        @Override
        public  MyViewHolder onCreateViewHolder(ViewGroup viewGroup ,int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.comment_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position){
            final CommentForApp commen = commentForApp.get(position);
            viewHolder.textView.setText(commen.getUserNickName());
            viewHolder.imageView.setImageResource(R.drawable.woman);
            viewHolder.messageView.setText(commen.getComment().substring(0,15) + "...");
            if(commen.getCommentRecomCount() == null){
                commen.setCommentRecomCount("0");
            }
            viewHolder.likeView.setText(commen.getCommentRecomCount());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("commen",commen);
                    Intent intent=new Intent(CommentActivity.this, Information.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d("commentForApp.size()",String.valueOf(commentForApp.size()));
            return  commentForApp.size();
        }
    }

//    public List<> getMemberList() {
//        List<Member> MemberList=new ArrayList<>();
//
//        return MemberList;
//    }
}
