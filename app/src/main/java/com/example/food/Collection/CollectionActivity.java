package com.example.food.Collection;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Map.StoreInfo;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;
import com.example.food.Sort.Common;
import com.example.food.Sort.SortActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SwipeMenuListView listView;
    private Toolbar collectionToolbar;
    private PullRefreshLayout pullRefreshLayout;
    private DrawerLayout collectionDrawerLayout;
    private AppAdapter mAdapter;
    private List<StoreInfo> storeInfos = getStoreInfoList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mAdapter = new AppAdapter(storeInfos,this);
        setupNavigationDrawerMenu();
        initSwipeListView();
        PullRefresh();
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    //Init DrawerLayout
    private void setupNavigationDrawerMenu() {
        collectionToolbar = findViewById(R.id.collectionToolbar);
        collectionToolbar.setTitle("收藏庫");
        NavigationView navigationView = findViewById(R.id.collectionNavigationView);
        collectionDrawerLayout = findViewById(R.id.collectionDrawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                collectionDrawerLayout,
                collectionToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        collectionDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                collectionDrawerLayout.closeDrawers();
                Intent intent = new Intent();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        // "0" 代表 首頁的requestCode
                        setResult(0, intent);
                        CollectionActivity.this.finish();
                        break;
                    case R.id.navSettings:
                        intent.setClass(CollectionActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.navMap:
                        intent.setClass(CollectionActivity.this, MapActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                        break;
                    case R.id.navGift:
                        intent.setClass(CollectionActivity.this, UnderDevelopmentActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                        break;
                    case R.id.navSort:
                        intent.setClass(CollectionActivity.this, SortActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                        break;
                    case R.id.navSearch:
                        intent.setClass(CollectionActivity.this, SearchActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                        break;
                    default:
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
        collectionDrawerLayout.closeDrawers();
        return true;
    }

    // Close the Drawer
    private void closeDrawer() {
        collectionDrawerLayout.closeDrawer(GravityCompat.START);
    }

    // Open the Drawer
    private void showDrawer() {
        collectionDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (collectionDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }

    // listen refresh event
    public void PullRefresh() {
        pullRefreshLayout = findViewById(R.id.collectionRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.setRefreshing(true);
                /*   Refresh Data   */
                // refresh complete
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }

    //initSwipeList
    public void initSwipeListView() {
        listView = findViewById(R.id.collectionListView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.collection_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // Close Interpolator
        listView.setCloseInterpolator(new BounceInterpolator());
        // Open Interpolator
        listView.setOpenInterpolator(new BounceInterpolator());
        //Click Listener
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete item
                        storeInfos.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    //dp to px
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    //Adapter
    class AppAdapter extends BaseAdapter {
        List<StoreInfo> storeInfos;
        Context context;

        public AppAdapter(List<StoreInfo> storeInfos, Context context){
            this.storeInfos = storeInfos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return storeInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return storeInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View item_view, ViewGroup viewGroup) {
            final StoreInfo storeInfo = storeInfos.get(position);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            listView.smoothOpenMenu(position);
            if(item_view == null) {
                item_view = layoutInflater.inflate(R.layout.collection_list,viewGroup,false);
            }
            ImageView imageView = item_view.findViewById(R.id.collection_image);
            imageView.setImageResource(storeInfo.getStore_img());
            TextView tvName = item_view.findViewById(R.id.collection_name);
            tvName.setText(storeInfo.getStrore_Name());
            //anim
            long aniTime = position * 10;
            Animation animation = AnimationUtils.loadAnimation(CollectionActivity.this,R.anim.collection_slide);
            animation.setStartOffset(aniTime);
            item_view.startAnimation(animation);
            return item_view;
        }
    }

    //StoreInfoList
    public List<StoreInfo> getStoreInfoList() {
        List<StoreInfo> StoreInfoList = new ArrayList<>();
        for(int i = 0;i<25;i++){
            StoreInfoList.add(new StoreInfo(i,R.drawable.drinks_and_desserts,"XXX甜點","dsfdfdsfdjfoie",25.042685,121.539954));
        }
        //get Data From DataBase
        return StoreInfoList;
    }

}
