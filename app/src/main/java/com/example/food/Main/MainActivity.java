package com.example.food.Main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.R;
import com.example.food.Settings.SettingsActivity;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ImageView ivUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTheme();
        initContent();
        setupNavigationDrawerMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

//    判斷日夜間模式
    private void initTheme() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo("com.example.food", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences prefs = getSharedPreferences("MyTheme", MODE_PRIVATE);
        int lastVersion = prefs.getInt("versionKey", 0);
        //如果当前版本大于上次版本，该版本属于第一次啟動
        if (currentVersion > lastVersion) {
            prefs.edit().putString("theme","0").apply();
            //將當前版本寫入preference中，則下次啟動的時候，判断不再是首次啟動
            prefs.edit().putInt("versionKey",currentVersion).apply();
        } else {
            int myTheme = Integer.parseInt(prefs.getString("theme",""));
            if (myTheme == 2) {
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
        ivUser = head.findViewById(R.id.ivUser);
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

                    case R.id.navSettings:
                        intent.setClass(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                        break;
                    case R.id.navMap:
                        intent.setClass(MainActivity.this, MapActivity.class);
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




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
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
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,permissions,REQ_PERMISSIONS);
        }

    }




}
