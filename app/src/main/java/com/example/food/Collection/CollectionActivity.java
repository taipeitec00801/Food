package com.example.food.Collection;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.example.food.AppModel.Store;
import com.example.food.DAO.StoreDAO;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Map.StoreInfo;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
import com.example.food.Settings.SettingsActivity;
import com.example.food.Sort.SortActivity;
import com.example.food.Sort.SortAsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SwipeMenuListView listView;
    private Toolbar collectionToolbar;
    private DrawerLayout collectionDrawerLayout;
    private AppAdapter mAdapter;
    private List<StoreInfo> storeInfos = getStoreInfoList();
    private SharedPreferences prefs;
    private boolean isMember;
    private List<Store> collectionList;
    private String userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        if(isMember) {
            mAdapter = new AppAdapter(storeInfos,this);
            initContent();
            setupNavigationDrawerMenu();
            initSwipeListView();
            // PullRefresh();
            listView.setAdapter(mAdapter);
        } else {
            checkLogin();
        }
    }

    private Runnable r1 = new Runnable() {
        @Override
        public void run() {
            userCollection = prefs.getString("collection", "");
            
            collectionList = new ArrayList<>();
            StoreDAO storeDAO = new StoreDAO(CollectionActivity.this);
            //collectionList = storeDAO.getCollectionListByUser();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        isMember = prefs.getBoolean("login", false);
    }

    private void checkLogin() {
        final Intent intent = new Intent();
        new MaterialDialog.Builder(CollectionActivity.this)
                .title("訪客您好!")
                .backgroundColorRes(R.color.colorDialogBackground)
                .positiveColorRes(R.color.colorText)
                .neutralColorRes(R.color.colorText)
                .icon(Objects.requireNonNull(getDrawable(R.drawable.warn_icon)))
                .content("欲使用該功能，請先登入")
                .positiveText(R.string.textIKnow)
                .neutralText(R.string.textGoTo)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        intent.setClass(CollectionActivity.this, MainActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        intent.setClass(CollectionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        CollectionActivity.this.finish();
                    }
                })
                .show();
    }


    //Init DrawerLayout
    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.collectionNavigationView);
        collectionDrawerLayout = findViewById(R.id.collectionDrawer);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號、暱稱和頭像顯示
        tv_nv_nickName.setText(prefs.getString("nickname", ""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount", ""));
        ImageInExternalStorage imgExStorage = new ImageInExternalStorage(CollectionActivity.this, prefs);
        imgExStorage.openFile(ivUserImage);

        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(CollectionActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

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
                Intent intent = new Intent();
                collectionDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        intent.setClass(CollectionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(CollectionActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(CollectionActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        intent.setClass(CollectionActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navCollection:
                        initContent();
                        onResume();
                        break;
                    case R.id.navSettings:
                        intent.setClass(CollectionActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(CollectionActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        CollectionActivity.this.finish();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        actionBarDrawerToggle.syncState();
    }

    private void initContent() {
        collectionToolbar = findViewById(R.id.collectionToolbar);
        collectionToolbar.setTitle("收藏庫");
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
        if (collectionDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
            Intent intent = new Intent();
            intent.setClass(CollectionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
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
            imageView.setImageResource(storeInfo.getStoreImg());
            TextView tvName = item_view.findViewById(R.id.collection_name);
            tvName.setText(storeInfo.getStoreName());
            //anim
            long aniTime = position * 10;
            Animation animation = AnimationUtils.loadAnimation(CollectionActivity.this, R.anim.collection_slide);
            animation.setStartOffset(aniTime);
            item_view.startAnimation(animation);
            return item_view;
        }
    }

    //StoreInfoList
    public List<StoreInfo> getStoreInfoList() {
        List<StoreInfo> StoreInfoList = new ArrayList<>();
        for(int i = 0;i<8;i++){
            StoreInfoList.add(new StoreInfo(i, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區新生南路一段103-2號","11:00-22:00","02-23389881"));
        }
        //get Data From DataBase
        return StoreInfoList;
    }

}