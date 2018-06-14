package com.example.food.Settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.MySharedPreferences;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Sort.SortActivity;

import java.io.File;
import java.util.Objects;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar settingsToolbar;
    private DrawerLayout settingsDrawerLayout;
    private SharedPreferences prefs;
    private boolean isMember;
    private CardView settingLogout;
    public static boolean cleanCache;

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
                CleanCacheDialog cleanCacheDialog = new CleanCacheDialog();
                cleanCacheDialog.show(getSupportFragmentManager(), "alert");
            }
        });
        /* 登出 */
        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(SettingsActivity.this)
                        .title(R.string.textLogout)
                        .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                        .content("你確定要登出嗎？")
                        .neutralText(R.string.text_btCancel)
                        .positiveText(R.string.text_btYes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //登出  初始化偏好設定中的會員資料
                                MySharedPreferences.initSharedPreferences(prefs);

                                Intent intent = new Intent();
                                intent.setClass(SettingsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                SettingsActivity.this.finish();
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    private void initContent() {
        settingsToolbar.setTitle(R.string.textSettings);
        // 是否登入
        isMember = prefs.getBoolean("login", false);
        if (isMember) {
        settingLogout.setVisibility(View.VISIBLE);
        }

        // 讀取快取大小
        File file = new File(this.getCacheDir().getPath());
        TextView tvClearApplicationCache = findViewById(R.id.tvClearApplicationCache);
        try {
            tvClearApplicationCache.setText(DataClearManager.getCacheSize(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                    .content("欲使用該功能，請先登入")
                    .positiveText(R.string.textIKnow)
                    .neutralText(R.string.textGoTo)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            intent.setClass(SettingsActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SettingsActivity.this.finish();
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }


    /* 清除内部 外部 快取 */
    public void cleanCache() {
        DataClearManager.clearInternalCache(getApplicationContext());
        DataClearManager.clearExternalCache(getApplicationContext());
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.settingsNavigationView);
        settingsDrawerLayout = findViewById(R.id.settingsDrawerLayout);
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
                        SettingsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(SettingsActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SettingsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(SettingsActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SettingsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(SettingsActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SettingsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        intent.setClass(SettingsActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SettingsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        initContent();
                        onResume();
                        break;
                    default:
                        intent.setClass(SettingsActivity.this, UnderDevelopmentActivity.class);
                        SettingsActivity.this.finish();
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
        if (settingsDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }

    //清除快取 Dialog
    public static class CleanCacheDialog extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.textCleanCache)
                    .setIcon(R.drawable.warn_icon)
                    .setMessage("Do you really want to clear cache?")
                    .setPositiveButton(R.string.text_btYes, this)
                    .setNegativeButton(R.string.text_btCancel, this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SettingsActivity activity = new SettingsActivity();
                    activity.cleanCache();
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }
}
