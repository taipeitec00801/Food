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
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.food.DataCleanManager;
import com.example.food.Main.MainActivity;
import com.example.food.R;

import java.io.File;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar settingsToolbar;
    private DrawerLayout settingsDrawerLayout;
    public static boolean cleanCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initContent();
        setupNavigationDrawerMenu();

        File file = new File(this.getCacheDir().getPath());
        TextView tvClearApplicationCache = findViewById(R.id.tvClearApplicationCache);
        try {
            tvClearApplicationCache.setText(DataCleanManager.getCacheSize(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  點選不同的 CardView
        selectCardView();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            Intent intent = new Intent();
            intent.setClass(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            SettingsActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void selectCardView() {
        /* 更改個人資料 */
        CardView cvUerInformation = findViewById(R.id.userInformation);
        cvUerInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, UserInformationActivity.class);
                startActivity(intent);
            }
        });
        /* 偏好種類設定 */
        CardView cvSettingsPreferences = findViewById(R.id.settingsPreferences);
        cvSettingsPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, PreferencesSettingsActivity.class);
                startActivity(intent);
            }
        });
        /* 日夜間模式 */
        CardView cvChangeDayTimeOrNight = findViewById(R.id.changeDayTimeOrNight);
        cvChangeDayTimeOrNight.setOnClickListener(new View.OnClickListener() {
            int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            @Override
            public void onClick(View view) {
                String modeTheme;
                SharedPreferences pref = getSharedPreferences("MyTheme", MODE_PRIVATE);
                if (mode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    modeTheme = String.valueOf(MODE_NIGHT_NO);
                    pref.edit().putString("theme", modeTheme).apply();
                } else if (mode == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    modeTheme = String.valueOf(MODE_NIGHT_YES);
                    pref.edit().putString("theme", modeTheme).apply();
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
                if (cleanCache) {
                    DataCleanManager.cleanInternalCache(getApplicationContext());
                    DataCleanManager.cleanExternalCache(getApplicationContext());
                }
            }
        });
        /* 登出 */
        CardView settingLogout = findViewById(R.id.settingLogout);
        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutDialog logoutDialog = new LogoutDialog();
                logoutDialog.show(getSupportFragmentManager(), "alert");
            }
        });
    }

    //清除快取 Dialog
    public static class CleanCacheDialog extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.textCleanCache)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMessage("Do you really want to clear cache?")
                    .setPositiveButton(R.string.text_btYes,this)
                    .setNegativeButton(R.string.text_btCancel,this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SettingsActivity.cleanCache = true;
                    break;
                default:
                    dialog.cancel();
                    SettingsActivity.cleanCache = false;
                    break;
            }
        }
    }

    //登出 Dialog
    public static class LogoutDialog extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.textLogout)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMessage("Do you really want to logout?")
                    .setPositiveButton(R.string.text_btYes,this)
                    .setNegativeButton(R.string.text_btCancel,this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }

    private void initContent() {
        settingsToolbar = findViewById(R.id.settingsToolbar);
        settingsToolbar.setTitle(R.string.textSettings);
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
                settingsDrawerLayout.closeDrawers();
                Intent intent = new Intent();
                switch (menuItem.getItemId()) {

                    case R.id.navHome:
                        intent.setClass(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                        SettingsActivity.this.finish();
                        break;

                    default:
                        initContent();
                        recreate();
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

}
