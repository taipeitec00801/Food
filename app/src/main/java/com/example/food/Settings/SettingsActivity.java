package com.example.food.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.Collection.CollectionActivity;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.MySharedPreferences;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Sort.SortActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.Objects;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar settingsToolbar;
    private DrawerLayout settingsDrawerLayout;
    private SharedPreferences prefs;
    private GoogleSignInClient mGoogleSignInClient;
    private CardView settingLogout;
    private ImageInExternalStorage imgExStorage;
    private boolean isMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findById();
        initContent();
        setupNavigationDrawerMenu();

        //  點選不同的 CardView
        selectCardView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isMember = prefs.getBoolean("login", false);
        // 是否登入　若是 則顯示登出鍵
//        if (isMember) {
            settingLogout.setVisibility(View.VISIBLE);
//        }
    }

    private void findById() {
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        settingsToolbar = findViewById(R.id.settingsToolbar);
        settingLogout = findViewById(R.id.settingLogout);
    }

    private void selectCardView() {
        /* 更改個人資料 */
        CardView cvUerInformation = findViewById(R.id.userInformation);
        cvUerInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //執行 登入狀態判斷
                isLogin(UserInformationActivity.class);
            }
        });
        /* 喜好種類設定 */
        CardView cvSettingsPreferences = findViewById(R.id.settingsPreferences);
        cvSettingsPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //執行 登入狀態判斷
                isLogin(PreferencesSettingsActivity.class);
            }
        });
        /* 日夜間模式 */
        CardView cvChangeDayTimeOrNight = findViewById(R.id.changeDayTimeOrNight);
        cvChangeDayTimeOrNight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int myTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (myTheme == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    prefs.edit().putInt("theme", MODE_NIGHT_NO).apply();
                } else if (myTheme == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    prefs.edit().putInt("theme", MODE_NIGHT_YES).apply();
                }
                recreate();
            }
        });
        /* 清除快取 */
        CardView cvClearApplicationCache = findViewById(R.id.clearApplicationCache);
        cvClearApplicationCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title(R.string.textCleanCache)
                        .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                        .backgroundColorRes(R.color.colorDialogBackground)
                        .positiveColorRes(R.color.colorText)
                        .content("Do you really want to clear cache?")
                        .positiveText(R.string.text_btYes)
                        .neutralText(R.string.text_btCancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DataClearManager.clearInternalCache(getApplicationContext());
                                DataClearManager.clearExternalCache(getApplicationContext());
                                recreate();
                            }
                        })
                        .show();
            }
        });
        /* 登出 */
        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = prefs.getString("userPassword", "");
                if (password.equals("googleLogin")) {
                    googleSignOut();
                } else {
                    signOut();
                }
            }
        });
    }

    private void initContent() {
        settingsToolbar.setTitle(R.string.textSettings);

        imgExStorage = new ImageInExternalStorage(SettingsActivity.this, prefs);
        // 讀取快取大小
        File file = new File(getDiskCacheDir(getApplicationContext()));

        TextView tvClearApplicationCache = findViewById(R.id.tvClearApplicationCache);
        tvClearApplicationCache.setText(DataClearManager.getCacheSize(file));
    }

    //讀取 cache 檔位置
    public String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    // google 登出
    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOut();
                    }
                });
    }

    //登出 訊息提示
    private void signOut() {
        new MaterialDialog.Builder(SettingsActivity.this)
                .title(R.string.textLogout)
                .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                .content("你確定要登出嗎？")
                .backgroundColorRes(R.color.colorDialogBackground)
                .positiveColorRes(R.color.colorText)
                .neutralColorRes(R.color.colorText)
                .neutralText(R.string.text_btCancel)
                .positiveText(R.string.text_btYes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //刪除會員頭像
                        imgExStorage.deleteFile();
                        //登出  初始化偏好設定中的會員資料
                        MySharedPreferences.initSharedPreferences(prefs);
                        //隱藏登出按鈕
                        settingLogout.setVisibility(View.INVISIBLE);
                        //回首頁
                        Intent intent = new Intent();
                        intent.setClass(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .show();
    }

    //判斷為會員時才能有該功能 若沒有登入 跳出訊息提示
    private void isLogin(Class wantToGo) {
        final Intent intent = new Intent();
        if (isMember) {
            intent.setClass(SettingsActivity.this, wantToGo);
            startActivity(intent);
        } else {
            new MaterialDialog.Builder(SettingsActivity.this)
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
                            intent.setClass(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.settingsNavigationView);
        settingsDrawerLayout = findViewById(R.id.settings_DrawerLayout);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號、暱稱和頭像顯示
        tv_nv_nickName.setText(prefs.getString("nickname", ""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount", ""));
        imgExStorage.openFile(ivUserImage);

        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                settingsDrawerLayout,
                settingsToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        settingsDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                Intent intent = new Intent();
                settingsDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        intent.setClass(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(SettingsActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(SettingsActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(SettingsActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        intent.setClass(SettingsActivity.this, CollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        initContent();
                        onResume();
                        break;
                    default:
                        intent.setClass(SettingsActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        settingsDrawerLayout.closeDrawers();
        return true;
    }

    // Close the Drawer
    private void closeDrawer() {
        settingsDrawerLayout.closeDrawer(GravityCompat.START);
    }

    // Open the Drawer
    private void showDrawer() {
        settingsDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (settingsDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
            Intent intent = new Intent();
            intent.setClass(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
