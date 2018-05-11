package com.example.food.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.example.food.Main.MainActivity;
import com.example.food.R;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

        private Toolbar settingsToolbar;
        private DrawerLayout settingsDrawerLayout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            initContent();
            setupNavigationDrawerMenu();



            CardView cvSettingsPreferences = findViewById(R.id.settingsPreferences);
            cvSettingsPreferences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(SettingsActivity.this, PreferencesSettingsActivity.class);
                    startActivity(intent);
                }
            });

            CardView cvChangeDayTimeOrNight = findViewById(R.id.changeDayTimeOrNight);
            cvChangeDayTimeOrNight.setOnClickListener(new View.OnClickListener() {
                int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                @Override
                public void onClick(View view) {
                    if(mode == Configuration.UI_MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if(mode == Configuration.UI_MODE_NIGHT_NO) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    recreate();
                }
            });

        }


        private void initContent() {
            settingsToolbar = findViewById(R.id.settingsToolbar);
            settingsToolbar.setTitle(R.string.textSettings);
        }

        private void setupNavigationDrawerMenu() {
            NavigationView navigationView = findViewById(R.id.navigationView);
            settingsDrawerLayout = findViewById(R.id.settingsDrawerLayout);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                    settingsDrawerLayout,
                    settingsToolbar,
                    R.string.drawer_open,
                    R.string.drawer_close);
            settingsDrawerLayout.addDrawerListener(actionBarDrawerToggle);

            @SuppressLint("CutPasteId")
            NavigationView navView = findViewById(R.id.navigationView);
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
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
