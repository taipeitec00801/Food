package com.example.food.Main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.food.AppModel.Store;
import com.example.food.Collection.CollectionActivity;
import com.example.food.Comment.CommentActivity;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.ImageTaskOIB;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.MySharedPreferences;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;
import com.example.food.Sort.SortActivity;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private int firstTheme;
    private SharedPreferences prefs;
    private ImageTaskOIB storeImgTask;

    //首頁主要四個icon
    private ImageView imgfork,gotocommon,imgmap,collection,magnifier;

    private RollPagerView mRollViewPager;
    private List<Store> sList;
    private  RecyclerView recyclerView;
private FoodpicAdapter foodpicAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirst();
        initContent();
        changepage();
        setupNavigationDrawerMenu();
        Bundle bundle = getIntent().getExtras();
        try{
            sList = (List<Store>) bundle.getSerializable("storeList");
            Log.d("-------sList---------", String.valueOf(sList.size()));
        }catch(Exception e){
            sList = getStoreList();
            Log.d("-------sList---------", String.valueOf(sList.size()));
        }
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new FoodpicAdapter(this, sList));

        mRollViewPager =  findViewById(R.id.roll_view_pager);
        //設定播放時間間隔
        mRollViewPager.setPlayDelay(3000);
        //設定透明度
        mRollViewPager.setAnimationDurtion(500);
        //設定配置器
        mRollViewPager.setAdapter(new TestNormalAdapter());

        mRollViewPager.setHintView(new ColorPointHintView(this, Color.GRAY,Color.WHITE));

    }
    //首頁icon跳頁轉換
    private void changepage() {
        imgfork = findViewById(R.id.imgfork);
        imgfork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SortActivity.class);
                startActivity(intent);
            }
        });
        imgmap = findViewById(R.id.imgmap);
        imgmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        collection = findViewById(R.id.collection);
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
            }
        });
        magnifier = findViewById(R.id.magnifier);
        magnifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();

        //判斷 主題是否有變動  若有 recreate 這個 Activity
        int nowTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nowTheme != firstTheme) {
            recreate();
        }
        Log.e("測試--login",String.valueOf(prefs.getBoolean("login",false)));
    }

    //  首次 執行App 或是 重開App  的偏好設定
    private void initFirst() {
        PackageInfo info = null;
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        try {
            info = getPackageManager().getPackageInfo("com.example.food", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;

        int lastVersion = prefs.getInt("versionKey", 0);
        //如果當前版本大於上次版本，該版本屬於第一次啟動
        if (currentVersion > lastVersion) {
            prefs.edit().putInt("theme", 0).apply();
            // 初始化 會員
            MySharedPreferences.initSharedPreferences(prefs);

            //將當前版本寫入preference中，則下次啟動的時候，判断不再是首次啟動
            prefs.edit().putInt("versionKey", currentVersion).apply();
        } else {
            firstTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            //  讀取 SharedPreferences 中的主題模式
            int myTheme = prefs.getInt("theme", 0);
            if (firstTheme == 32 || myTheme == MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
            setContentView(R.layout.activity_main);
        }
    }

    private void initContent() {
        toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle(R.string.textHome);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.main_drawerLayout);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號、暱稱和頭像顯示
        tv_nv_nickName.setText(prefs.getString("nickname", ""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount", ""));
        ImageInExternalStorage imgExStorage = new ImageInExternalStorage(MainActivity.this, prefs);
        imgExStorage.openFile(ivUserImage);

        Log.e("測試--logo", String.valueOf(((BitmapDrawable) ivUserImage.getDrawable()).getBitmap()));


        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
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
                drawerLayout.closeDrawers();
                Intent intent = new Intent();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        initContent();
                        onResume();
                        break;
                    case R.id.navMap:
                        intent.setClass(MainActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(MainActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(MainActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        intent.setClass(MainActivity.this, CollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        intent.setClass(MainActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(MainActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        actionBarDrawerToggle.syncState();
    }

    /*   每個 Activity  的要使用
            @Override
            protected void onActivityResult ...       */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // requestCode "RESULT_OK" 代表 前一頁
            // 一定要有   RESULT_OK
            case RESULT_OK:
                break;
            // requestCode "0" 代表 首頁
            case 0:
                break;
            default:
                break;
        }
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
        }
    }
    //要求User Permissions
    private static final int REQ_PERMISSIONS = 0;

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, permissions, REQ_PERMISSIONS);
        }

    }



    //rollpageview
    private class TestNormalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.drawable.test1,
                R.drawable.test2,
                R.drawable.test3,

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


    private class FoodpicAdapter extends RecyclerView.Adapter<FoodpicAdapter.MyViewHolder> {
        private Context context;
        private List<Store> foodList;

        FoodpicAdapter(Context context, List<Store> foodList) {
            this.context = context;
            this.foodList = foodList;


        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvId, tvName;

            MyViewHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvId = itemView.findViewById(R.id.tvId);
                tvName = itemView.findViewById(R.id.tvName);

            }
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.mainfood_view, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
            final Store food = foodList.get(position);

            String url = Common.URL+"/appGetImages";
            int id = food.getStoreId();
            storeImgTask = new ImageTaskOIB(url, id, viewHolder.ivImage);
            storeImgTask.execute();

           // viewHolder.ivImage.setImageResource(R.drawable.food1);
            //viewHolder.tvId.setText(String.valueOf(food.getStoreId()));
            viewHolder.tvName.setText(food.getStoreName());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateIntent(position,v);

                }
            });
        }

    }

    //點擊店家跳至店家頁面的animate
    public void animateIntent(int storeNum,View view) {
        Intent intent = new Intent(MainActivity.this,CommentActivity.class);
        String transitionName = getString(R.string.map_transition_string);
        //String transitionName2 = getString(R.string.map_storeInfo_name);
        View viewStart = view.findViewById(R.id.ivImage);
      //  View viewStart2 = findViewById(R.id.storeInfo);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String> (viewStart,transitionName);


        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,pairs);

        //將資料送至商店頁
        Bundle bundle = new Bundle();
        bundle.putSerializable("store",sList.get(storeNum));
        bundle.putAll(options.toBundle());
        intent.putExtras(bundle);
        intent.putExtras(options.toBundle());
        startActivity(intent,options.toBundle());

    }

    public List<Store> getStoreList() {
        List<Store> storeList = new ArrayList<>();
        storeList.add(new Store(1,  "詠豐堂EIHODO"));
        storeList.add(new Store(2,  "彡耕居食事所"));
        storeList.add(new Store(3, "花漾點點"));
        Log.d("-------storeList---------", String.valueOf(storeList.size()));
        return storeList;
    }
}
