package com.example.food.Main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.food.Collection.CollectionActivity;
import com.example.food.Comment.CommentActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;
import com.example.food.Sort.SortActivity;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private int firstTheme;

    //首頁主要四個icon
    private ImageView imgfork,gotocommon,imgmap,collection,magnifier;

    private RollPagerView mRollViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirst();
        initContent();
        changepage();
        setupNavigationDrawerMenu();
//        //Common Test 用
//        gotocommon = findViewById(R.id.goToCommon);
//        gotocommon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, CommentActivity.class);
//                startActivity(intent);
//            }
//        });

        mRollViewPager = (RollPagerView) findViewById(R.id.roll_view_pager);
        //設定播放時間間隔
        mRollViewPager.setPlayDelay(1000);
        //設定透明度
        mRollViewPager.setAnimationDurtion(500);
        //設定配置器
        mRollViewPager.setAdapter(new TestNormalAdapter());

        mRollViewPager.setHintView(new ColorPointHintView(this, Color.GRAY,Color.WHITE));

    }
    //首頁icon跳頁
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
    }

    //  首次 執行App 或是 重開App  的偏好設定
    private void initFirst() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo("com.example.food", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        int lastVersion = prefs.getInt("versionKey", 0);
        //如果當前版本大於上次版本，該版本屬於第一次啟動
        if (currentVersion > lastVersion) {
            //紀錄主題 初始化
            prefs.edit().putInt("theme", 0).apply();
            //紀錄會員資料 初始化
            prefs.edit().putInt("memberId", 0).apply();
            prefs.edit().putString("userAccount", "").apply();
            prefs.edit().putString("userPassword", "").apply();
            prefs.edit().putString("nickName", "").apply();
            prefs.edit().putString("birthday", "").apply();
            prefs.edit().putInt("gender", 0).apply();
            prefs.edit().putInt("userRank", 1).apply();
            prefs.edit().putString("preference", "").apply();

            //將當前版本寫入preference中，則下次啟動的時候，判断不再是首次啟動
            prefs.edit().putInt("versionKey", currentVersion).apply();
        } else {
            firstTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            //  讀取 SharedPreferences 中的模式
            int myTheme = prefs.getInt("theme", 0);
            if (firstTheme == 32 || myTheme == 2) {
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
        drawerLayout = findViewById(R.id.drawerLayout);
        //點擊投向 到登入
        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);
        ImageView ivUser = head.findViewById(R.id.ivUser);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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
                        onResume();
                        break;
                    case R.id.navSettings:
                        intent.setClass(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.navMap:
                        intent.setClass(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navGift:
                        intent.setClass(MainActivity.this, UnderDevelopmentActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(MainActivity.this, SortActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        initContent();
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



}
