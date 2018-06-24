package com.example.food.Sort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.AppModel.Sort;
import com.example.food.AppModel.SortAs;
import com.example.food.Collection.CollectionActivity;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SortActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Boolean btCont = true;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SharedPreferences prefs;
    private Button outBt, comeBt;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private RecyclerView recyclerView;

    @Override
    protected void onPause() {
        super.onPause();
        if (!outBt.isEnabled()) {
            int i = Integer.parseInt(outBt.getText().toString());
            recyclerView.findViewHolderForLayoutPosition(i).itemView.findViewById(R.id.sort_item_spin_kit_L).setVisibility(View.INVISIBLE);
            recyclerView.findViewHolderForLayoutPosition(i).itemView.findViewById(R.id.sort_item_spin_kit_R).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        outBt = findViewById(R.id.sort_bt);
        comeBt = findViewById(R.id.sort_comebt);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);

        //建立ToolBar
        initContent();
        setupNavigationDrawerMenu();
        //被點擊時，建立新的執行緒，並且將Button關閉，等到onPause()、onStart()時重新開啟。
        outBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outBt.isEnabled()) {
                    mThread = new HandlerThread("aa");
                    mThread.start();
                    mThreadHandler = new Handler(mThread.getLooper());
                    mThreadHandler.post(r1);
                }
                outBt.setEnabled(false);
            }
        });

        recyclerView = findViewById(R.id.sort_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        List<Sort> sortList = getSortList();
        recyclerView.setAdapter(new sortAdapter(this, sortList));
    }

    private Runnable r1 = new Runnable() {

        public void run() {
            //這裡放執行緒要執行的程式。
            int i = Integer.parseInt(outBt.getText().toString());
            portalToSortAs(getSortList().get(i), comeBt.isEnabled());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(r1);
        }
        if (mThread != null) {
            mThread.quit();
        }
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
        outBt.setEnabled(btCont);
    }

    private void initContent() {
        toolbar = findViewById(R.id.sort_toolbar);
        toolbar.setTitle("餐廳分類");
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.sort_navigationView);
        drawerLayout = findViewById(R.id.sort_drawerLayout);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號、暱稱和頭像顯示
        tv_nv_nickName.setText(prefs.getString("nickname", ""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount", ""));
        ImageInExternalStorage imgExStorage = new ImageInExternalStorage(SortActivity.this, prefs);
        imgExStorage.openFile(ivUserImage);

        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(SortActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                Intent intent = new Intent();
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        intent.setClass(SortActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(SortActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        initContent();
                        onResume();
                        break;
                    case R.id.navSearch:
                        intent.setClass(SortActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        intent.setClass(SortActivity.this, CollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        intent.setClass(SortActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(SortActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortActivity.this.finish();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }

    // Close the Drawer
    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    // Open the Drawer
    private void showDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
            Intent intent = new Intent();
            intent.setClass(SortActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    private class sortAdapter extends
            RecyclerView.Adapter<sortAdapter.SortViewHolder> {
        private Context context;
        private List<Sort> sortList;

        sortAdapter(Context context, List<Sort> sortList) {
            this.context = context;
            this.sortList = sortList;
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImageLeft, ivImageRight;
            TextView tvNameRight, tvNameLeft;
            CardView cvLeft, cvRight;
            CardView cvMoveL, cvMoveR;
            MaterialRippleLayout mrlL, mrlR;
            SpinKitView skvL, skvR;

            SortViewHolder(View itemView) {
                super(itemView);
                ivImageLeft = itemView.findViewById(R.id.sort_item_Left_iv);
                tvNameLeft = itemView.findViewById(R.id.sort_item_Left_tv);

                ivImageRight = itemView.findViewById(R.id.sort_item_Right_iv);
                tvNameRight = itemView.findViewById(R.id.sort_item_Right_tv);

                cvLeft = itemView.findViewById(R.id.sort_item_Lift_cv);
                cvMoveL = itemView.findViewById(R.id.sort_item_cv_moveL);

                cvRight = itemView.findViewById(R.id.sort_item_Right_cv);
                cvMoveR = itemView.findViewById(R.id.sort_item_cv_moveR);

                mrlL = itemView.findViewById(R.id.sort_item_ripple_L);
                mrlR = itemView.findViewById(R.id.sort_item_ripple_R);

                skvL = itemView.findViewById(R.id.sort_item_spin_kit_L);
                skvR = itemView.findViewById(R.id.sort_item_spin_kit_R);
            }
        }

        @Override
        public int getItemCount() {
            return sortList.size();
        }

        @NonNull
        @Override
        public SortViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.sort_item, viewGroup, false);

            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final SortViewHolder viewHolder, final int position) {
            final Sort sort = sortList.get(position);
            int colorL;
            int colorR;
            if (position % 2 == 0) {
                colorL = 0xff34CCD6;//藍色
                colorR = 0xffFF7676;//紅色
                Drawable drawable = getDrawable(R.drawable.sb);
                viewHolder.tvNameLeft.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                drawable = getDrawable(R.drawable.sr);
                viewHolder.tvNameRight.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            } else {
                colorL = 0xffFF7676;
                colorR = 0xff34CCD6;
                Drawable drawable = getDrawable(R.drawable.sr);
                viewHolder.tvNameLeft.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                drawable = getDrawable(R.drawable.sb);
                viewHolder.tvNameRight.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            }
            viewHolder.ivImageLeft.setImageResource(sort.getIvLsrc());
            viewHolder.tvNameLeft.setText(String.valueOf(sort.getTvLname()));
            viewHolder.tvNameLeft.setTextColor(colorL);
            viewHolder.cvLeft.setCardBackgroundColor(colorL);

            viewHolder.ivImageRight.setImageResource(sort.getIvRsrc());
            viewHolder.tvNameRight.setText(String.valueOf(sort.getTvRname()));
            viewHolder.tvNameRight.setTextColor(colorR);
            viewHolder.cvRight.setCardBackgroundColor(colorR);

            //點擊左item後將資料寫上Button上，此方法將會點擊背後outBt一次。
            // outBt寫上position以分辨是哪一個Item點擊
            // comeBt的開啟狀態(true)決定送來的Item是經由左邊送來的。
            viewHolder.mrlL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (outBt.isEnabled()) {
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        comeBt.setEnabled(true);
                        outBt.performClick();
                        viewHolder.skvL.setVisibility(View.VISIBLE);
                    }

                }
            });
            //點擊右item後將資料寫上Button上，此方法將會點擊背後outBt一次。
            // outBt寫上position以分辨是哪一個Item點擊
            // comeBt的開啟狀態(false)決定送來的Item是經由右邊送來的。
            viewHolder.mrlR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (outBt.isEnabled()) {
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        comeBt.setEnabled(false);
                        outBt.performClick();
                        viewHolder.skvR.setVisibility(View.VISIBLE);
                    }
                }
            });

            //設定每個item動畫延遲時間，position超過3，不使用動畫。
            if (position <= 3) {
                long aniTime = 100 * position;
                Animation am = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_down);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveL.setAnimation(am);
                am = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_up);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveR.setAnimation(am);
            }
        }
    }

    //傳送門，將送進來的資料傳到下一頁還有將資料送給SortDAO物件的方法
    //此方法將由執行續執行，否則會造成畫面凍結。
    public void portalToSortAs(Sort sort, Boolean whereCome) {
        final SortDAO sortDAO = new SortDAO(SortActivity.this);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        List<SortAs> sortAsList = null;
        String name = null;
        int number = 0;
        if (whereCome) {
            name = sort.getTvLname();
            number = sort.getSortLnum();
        } else {
            name = sort.getTvRname();
            number = sort.getSortRnum();
        }
        sortAsList = sortDAO.sortRestaurant(number);
        bundle.putString("SortName", name);
        bundle.putSerializable("SortAsList", (Serializable) sortAsList);
        intent.putExtras(bundle);
        intent.setClass(SortActivity.this, SortAsActivity.class);
        startActivity(intent);
    }


    //分類的資料庫
    public List<Sort> getSortList() {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort(R.drawable.s03, 0, "中式餐廳",
                R.drawable.s02, 1, "西式餐廳"));

        sortList.add(new Sort(R.drawable.s05, 2, "韓式餐廳",
                R.drawable.s04, 3, "日式餐廳"));

        sortList.add(new Sort(R.drawable.s06, 4, "港式餐廳",
                R.drawable.s07, 5, "泰式餐廳"));

        sortList.add(new Sort(R.drawable.s08, 6, "小吃",
                R.drawable.s09, 7, "冰品"));

        sortList.add(new Sort(R.drawable.s10, 8, "飲料甜點",
                R.drawable.s01, 9, "其他"));

        return sortList;
    }
}
