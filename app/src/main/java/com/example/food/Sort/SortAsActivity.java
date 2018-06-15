package com.example.food.Sort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AppModel.SortAs;
import com.example.food.Collection.CollectionActivity;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;

import java.util.List;

public class SortAsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "SortAsActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_as);
        initContent();
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        setupNavigationDrawerMenu();

        SortActivity.btCont = true;
        RecyclerView recyclerView = findViewById(R.id.sortAs_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(SortAsActivity.this));

        //將上一頁送來bundle打開。
        Bundle bundle = getIntent().getExtras();
        List<SortAs> sortAsList = (List<SortAs>) bundle.getSerializable("SortAsList");
        if(sortAsList == null || sortAsList.isEmpty()){
            TextView errorText = findViewById(R.id.sortAs_errorTv);
            errorText.setText("連線不穩，請重新嘗試。");
        }else{
            recyclerView.setAdapter(new sortAdapter(this, sortAsList));
        }

    }

    private void initContent() {
        String titleName = "分類失敗";
        if(getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            titleName = bundle.getString("SortName");
        }
        toolbar = findViewById(R.id.sortAs_toolbar);
        toolbar.setTitle(titleName);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.sortAs_navigationView);
        drawerLayout = findViewById(R.id.sortAs_drawerLayout);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號和暱稱顯示
        tv_nv_nickName.setText(prefs.getString("nickname",""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount",""));

        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(SortAsActivity.this, LoginActivity.class);
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
                        intent.setClass(SortAsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(SortAsActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(SortAsActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(SortAsActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        intent.setClass(SortAsActivity.this, CollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        intent.setClass(SortAsActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(SortAsActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SortAsActivity.this.finish();
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }

    private class sortAdapter extends
            RecyclerView.Adapter<sortAdapter.SortViewHolder> {
        private Context context;
        private List<SortAs> sortList;

        sortAdapter(Context context, List<SortAs> sortList) {
            this.context = context;
            this.sortList = sortList;
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
            ImageView resImg;
            TextView resName,likeNumber,textNumber,mapBt;
            SortViewHolder(View itemView) {
                super(itemView);
                resImg = itemView.findViewById(R.id.sortAs_item_resImg);
                resName =  itemView.findViewById(R.id.sortAs_item_resName);
                likeNumber =  itemView.findViewById(R.id.sortAs_item_likeNumber);
                textNumber = itemView.findViewById(R.id.sortAs_item_textNumber);
                mapBt = itemView.findViewById(R.id.sortAs_item_mapBt);
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
            View itemView = layoutInflater.inflate(R.layout.sort_as_item, viewGroup, false);
            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SortViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.resImg.setImageResource(R.drawable.food3);
            viewHolder.resName.setText(String.valueOf(sort.getStoreName()));
            viewHolder.likeNumber.setText(String.valueOf(sort.getSortNumber()));
            viewHolder.mapBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SortAsActivity.this,"有歐",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}


