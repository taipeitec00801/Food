package com.example.food.Comment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.AppModel.CommentForApp;
import com.example.food.AppModel.Store;
import com.example.food.DAO.StoreDAO;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.ImageTaskOIB;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.R;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.DynamicPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Delayed;

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
    private SharedPreferences prefs;
    private boolean isMember, collected;
    private RecyclerView rvComment;
    private MemberAdapter commentAd;
    private ImageView store_recom_img, store_collect_img;
    private Integer recom;
    private TextView com_count_zero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        findById();
        sDAO = new StoreDAO(CommentActivity.this);
        recom = 0;
        collected = false;
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
        changeView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isMember = prefs.getBoolean("login", false);
    }

    private void findById() {
        rvComment = findViewById(R.id.Comment_recycleview);
        mRollViewPager = findViewById(R.id.roll_view_pager);
        tv = findViewById(R.id.tv_store_info_name);
        tv1 = findViewById(R.id.tv_store_info_address);
        tv2 = findViewById(R.id.tv_store_info_time);
        tv3 = findViewById(R.id.tv_store_info_phone);
        com_count_zero = findViewById(R.id.com_count_zero);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
    }

    //判斷為會員時才能有該功能 若沒有登入 跳出訊息提示
    private void isLogin(View view) {
        if (isMember) {
            View mView = LayoutInflater.from(CommentActivity.this)
                    .inflate(R.layout.store_info_pop, null, false);

            popWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // 设置弹出窗体可点击
            popWindow.setFocusable(true);
            // 设置弹出窗体显示时的动画，从底部向上弹出
            popWindow.setAnimationStyle(R.style.take_photo_anim);

            //檢查持是否已推薦
            store_recom_img = mView.findViewById(R.id.store_recom_img);
            store_collect_img = mView.findViewById(R.id.store_collect_img);

            if (recom == 1){
                store_recom_img.setImageResource(R.drawable.comment_is_like);
            }
            if (collected){
                store_collect_img.setImageResource(R.drawable.bookmark_is);
            }

            //推薦
            View store_recom = mView.findViewById(R.id.store_recom);
            store_recom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recom == 1) {
                        store_recom_img.setImageResource(R.drawable.comment_like);
                        recom = 0;
                    }else if (recom == 0) {
                        store_recom_img.setImageResource(R.drawable.comment_is_like);
                        recom = 1;
                    }
                    boolean isYN = sDAO.updateStRecom(prefs.getInt("memberId", 0), store.getStoreId(), recom);
                    if (isYN) {
                        popWindow.dismiss();
                    }
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
                    if (collected) {
                        store_collect_img.setImageResource(R.drawable.bookmark);
                        collected = false;
                    }else {
                        store_collect_img.setImageResource(R.drawable.bookmark_is);
                        collected = true;
                    }
                    //修改收藏
                    popWindow.dismiss();
                }
            });

            //分享
            View store_share = mView.findViewById(R.id.store_share);
            store_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
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
            popWindow.showAsDropDown(view, -50, -300);

            // 设置弹出窗体的背景
            // 实例化一个ColorDrawable颜色为半透明
            popWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        } else {
            new MaterialDialog.Builder(CommentActivity.this)
                    .title("訪客您好!")
                    .backgroundColorRes(R.color.colorDialogBackground)
                    .positiveColorRes(R.color.colorText)
                    .neutralColorRes(R.color.colorText)
                    .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                    .content("欲使用該功能，請先登入")
                    .positiveText(R.string.textIKnow)
                    .neutralText(R.string.textGoTo)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent();
                            intent.setClass(CommentActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void changeView(){

        FloatingActionButton floatingButton = findViewById(R.id.floating_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //執行 登入狀態判斷
                isLogin(view);

            }
        });

        //會跳到地圖頁
        Button bt_storeInfo_map = findViewById(R.id.bt_storeInfo_map);
        bt_storeInfo_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent(CommentActivity.this, MapActivity.class);
                //...
            }
        });
    }




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
//    }
    private Runnable r1 = new Runnable() {
        public void run() {
            //這裡放執行緒要執行的程式。
            Log.d("store.getStoreId()---------------------------" , store.getStoreId().toString());

            cfaList =  sDAO.getCommentForApp(store.getStoreId());
            if (cfaList == null || cfaList.size() < 1) {
                com_count_zero.setText("尚無評論");
            }
            if (isMember) {
                recom = sDAO.getStRecom(prefs.getInt("memberId", 0), store.getStoreId());
            }
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

                    if (isMember) {
                        String userCollection = prefs.getString("collection", "");
                        String[] coll = userCollection.split(",");
                        for (String n : coll) {
                            if (Integer.valueOf(n) == store.getStoreId()) {
                                collected = true;
                            }
                        }
                    }
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
