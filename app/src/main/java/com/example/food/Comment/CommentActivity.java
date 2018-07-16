package com.example.food.Comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AppModel.Comment;
import com.example.food.AppModel.CommentForApp;
import com.example.food.AppModel.Member;
import com.example.food.AppModel.Store;


import com.example.food.DAO.MemberDAO;
import com.example.food.DAO.StoreDAO;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.CommonTask;
import com.example.food.DAO.task.ImageTaskOIB;
import com.example.food.Settings.UserInformationActivity;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.DynamicPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.example.food.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private PopupWindow popWindow = new PopupWindow();
    private RollPagerView mRollViewPager;
    private TextView tv, tv1, tv2, tv3;
    private Store store;
    private ImageTaskOIB storeImgTask;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private StoreDAO sDAO;
    private List<CommentForApp> cfaList;
    private RecyclerView rvComment;
    private MemberAdapter commentAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        findById();

        //get bundle From Map
        Bundle bundle = getIntent().getExtras();
        store = (Store) bundle.getSerializable("store");
        Log.d("storeList", "" + store.getStoreName());

        tv.setText(store.getStoreName());
        tv1.setText(store.getStoreAddress());
        tv2.setText(store.getServiceHours());
        tv3.setText(store.getStorePhone());

        mThread = new HandlerThread("fdfdf");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(r1);


        rvComment.setLayoutManager(new StaggeredGridLayoutManager(
                1, StaggeredGridLayoutManager.VERTICAL));
        //设置播放时间间隔
//        mRollViewPager.setPlayDelay(3000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.GRAY, Color.WHITE));
//        new commentTask().execute();
        changeview();
    }

    private void findById() {
        rvComment = findViewById(R.id.Comment_recycleview);
        mRollViewPager = findViewById(R.id.roll_view_pager);
        tv = findViewById(R.id.tv_store_info_name);
        tv1 = findViewById(R.id.tv_store_info_address);
        tv2 = findViewById(R.id.tv_store_info_time);
        tv3 = findViewById(R.id.tv_store_info_phone);
    }

    private void changeview() {
        //跳地圖
        Button bt_storeInfo_map = findViewById(R.id.bt_storeInfo_map);
        bt_storeInfo_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton floating_Button = findViewById(R.id.floating_button);
        floating_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mView = LayoutInflater.from(CommentActivity.this)
                        .inflate(R.layout.store_info_pop, null, false);

                popWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);

                // 设置弹出窗体可点击
                popWindow.setFocusable(true);
                // 设置弹出窗体显示时的动画，从底部向上弹出
                popWindow.setAnimationStyle(R.style.take_photo_anim);

                // 设置按钮监听
                //推薦
                View store_recom = mView.findViewById(R.id.store_recom);
                store_recom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                //新增評論
                View store_comment = mView.findViewById(R.id.store_comment);
                store_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("storename" , store.getStoreName());
                        Intent intent = new Intent(CommentActivity.this, Comment_interface.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });

                //收藏
                View store_collect = mView.findViewById(R.id.store_collect);
                store_collect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                //分享
                View store_share = mView.findViewById(R.id.store_share);
                store_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // 设置外部可点击
                popWindow.setOutsideTouchable(true);
                // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
                popWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                        // 这里如果返回true的话，touch事件将被拦截
                        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                    }
                });

                //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
                popWindow.showAsDropDown(view, -50, -200);

                // 设置弹出窗体的背景
                // 实例化一个ColorDrawable颜色为半透明
                popWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
            }
        });



//        class commentTask extends AsyncTask<Void, Void, Void> {
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                RecyclerView recyclerView = findViewById(R.id.Comment_recycleview);
//                recyclerView.setLayoutManager(
//                        new StaggeredGridLayoutManager(
//                                1, StaggeredGridLayoutManager.VERTICAL));
////            List<Member> memberList = getMemberList();
////            recyclerView.setAdapter(new MemberAdapter(CommentActivity.this, memberList));
//                return null;
//            }
//        }
    }
    private Runnable r1 = new Runnable() {
        public void run() {
            //這裡放執行緒要執行的程式。
            Log.d("store.getStoreId()---------------------------" , store.getStoreId().toString());
            sDAO = new StoreDAO(CommentActivity.this);
            cfaList =  sDAO.getCommentForApp(store.getStoreId());
            Log.d("------------------------------------" , String.valueOf(cfaList.size()));
            commentAd = new MemberAdapter(CommentActivity.this , cfaList);
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
                    rvComment.setAdapter(commentAd);
                }
            });

        }
    };

    private class TestNormalAdapter extends DynamicPagerAdapter {

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            String url = Common.URL + "/appGetAllImages";
            int id = store.getStoreId();
            storeImgTask = new ImageTaskOIB(url, id, position, view);
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



    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
        private Context context;
        private List<CommentForApp> cfaList;

        MemberAdapter(Context context, List<CommentForApp> cfaList) {
            this.context = context;
            this.cfaList = cfaList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView , likeView;
            ImageView imageView;
            TextView messageView;


            MyViewHolder(View itemview) {
                super(itemview);

                likeView = itemview.findViewById(R.id.comment_RecomCount);
                textView = itemview.findViewById(R.id.customname);
                imageView = itemview.findViewById(R.id.iv);
                messageView = itemview.findViewById(R.id.Usermessage);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.comment_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int position) {
            final CommentForApp cfa = cfaList.get(position);
            viewHolder.textView.setText(cfa.getUserNickName());
            Log.d("CommentForApp====:",""+cfa.getMsgCid());
            String url = Common.URL + "/appGetCommentMember";
            storeImgTask = new ImageTaskOIB(url, Integer.parseInt(cfa.getMsgCid()), position, viewHolder.imageView);
            storeImgTask.execute();
            viewHolder.messageView.setText(cfa.getComment().substring(0,15)+"...");
            viewHolder.likeView.setText(cfa.getCommentRecomCount());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("commen" , cfa);
                    Intent intent = new Intent(CommentActivity.this, Information.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return cfaList.size();
        }
    }
}
