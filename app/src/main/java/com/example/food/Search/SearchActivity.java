package com.example.food.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.AppModel.SortAs;
import com.example.food.Collection.CollectionActivity;
import com.example.food.Main.MainActivity;
import com.example.food.Map.MapActivity;
import com.example.food.Member.LoginActivity;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.UnderDevelopmentActivity;
import com.example.food.R;
import com.example.food.Settings.SettingsActivity;
import com.example.food.Sort.SortActivity;
import com.example.food.Sort.SortDAO;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private static final String TAG = "SearchActivity";
    private RecyclerView rvUp,rvDown;
    private MaterialSearchBar searchBar;
    private List<SortAs> sortItemList = null;
    private searchAdapter searchAdapter;
    private sortAdapter sortAdapter;
    private boolean ordercont = true;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private TextView tvAbc;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //建立ToolBar
        initContent();
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        setupNavigationDrawerMenu();
        tvAbc = findViewById(R.id.search_tvAbc);

        rvUp = findViewById(R.id.search_rvUp);
        rvUp.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        rvDown = findViewById(R.id.search_rvDown);
        rvDown.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
//        rvUp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(searchBar.isSearchEnabled()){
//                    searchBar.disableSearch();
//                }
//                return false;
//            }
//        });
        searchBar = findViewById(R.id.searchBar);
        searchBar.inflateMenu(R.menu.search_order,R.drawable.order);
        searchBar.setMenuIconTint(0xffff0000);
//        searchBar.hideSuggestionsList();
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
//                hideSoftKeyboard(SearchActivity.this);
            }
            @Override
            public void onSearchConfirmed(CharSequence text) {
                mThread=new HandlerThread("bb");
                mThread.start();
                mThreadHandler=new Handler(mThread.getLooper());
                mThreadHandler.post(r1);
//                try {
//                    mThread.join(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(rvUp.getAdapter()==null||rvDown.getAdapter()==null){
//                try {
//                    mThread.join();
                rvUp.setAdapter(searchAdapter);
                rvDown.setAdapter(sortAdapter);
//                } catch (InterruptedException e) {
////                    rvUp.setAdapter(searchAdapter);
////                    rvDown.setAdapter(sortAdapter);
////                }
//                }else{
//                    sortAdapter.notifyDataSetChanged();
//                    searchAdapter.notifyDataSetChanged();
//                    rvUp.setAdapter(searchAdapter);
////                    rvDown.setAdapter(sortAdapter);
//                }
                tvAbc.setVisibility(View.VISIBLE);

                searchBar.disableSearch();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_BACK:
                        searchBar.disableSearch();break;

                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        drawerLayout.openDrawer(Gravity.LEFT);break;
                }
//                searchSetting(SearchActivity.this);
            }
        });
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
            TextView resName,likeNumber;
            SortViewHolder(View itemView) {
                super(itemView);
                resImg = itemView.findViewById(R.id.search_downItem_resImg);
                resName =  itemView.findViewById(R.id.search_downItem_resName);
                likeNumber =  itemView.findViewById(R.id.search_downItem_likeNumber);
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
            View itemView = layoutInflater.inflate(R.layout.search_down_item, viewGroup, false);
            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SortViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.resImg.setImageResource(R.drawable.logo);
            viewHolder.resName.setText(String.valueOf(sort.getStoreName()));
            viewHolder.likeNumber.setText(String.valueOf(sort.getStoreRecomCount()));
        }
    }

    private class searchAdapter extends
            RecyclerView.Adapter<searchAdapter.SearchViewHolder> {
        private Context context;
        private List<SortAs> sortList;

        searchAdapter(Context context, List<SortAs> sortList) {
            this.context = context;
            this.sortList = sortList;
        }

        class SearchViewHolder extends RecyclerView.ViewHolder {
            ImageView bgImg,leftImg,rightImg;
            TextView resName,resSort,mapBt;
            CircleImageView circleImg;
            MaterialRippleLayout mrLayout;

            SearchViewHolder(View itemView) {
                super(itemView);
                bgImg = itemView.findViewById(R.id.search_item_bgImg);
                leftImg = itemView.findViewById(R.id.search_item_leftImg);
                rightImg = itemView.findViewById(R.id.search_item_rightImg);
                resName = itemView.findViewById(R.id.search_item_resName);
                resSort = itemView.findViewById(R.id.search_item_resSort);
                circleImg = itemView.findViewById(R.id.search_item_circleImg);
                mapBt = itemView.findViewById(R.id.search_item_mapBt);
                mrLayout = itemView.findViewById(R.id.search_item_ripple);
            }
        }
        @Override
        public int getItemCount() {
            return sortList.size();
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.search_item, viewGroup, false);
            return new SearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.resName.setText(sort.getStoreName());
            viewHolder.resSort.setText(sortName(sort.getSortNumber()));
            viewHolder.mapBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this,"有喔",Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.mrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(SearchActivity.this,"有喔",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void initContent() {
        searchBar = findViewById(R.id.searchBar);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.search_navigationView);
        drawerLayout = findViewById(R.id.search_drawerLayout);

        View headerView = navigationView.getHeaderView(0);
        RelativeLayout head = headerView.findViewById(R.id.menuHeader);

        TextView tv_nv_nickName = head.findViewById(R.id.tv_nv_nickName);
        TextView tv_nv_UserAccount = head.findViewById(R.id.tv_nv_User_Account);
        ImageView ivUserImage = head.findViewById(R.id.cv_nv_User_image);

        //若已登入 將會員帳號、暱稱和頭像顯示
        tv_nv_nickName.setText(prefs.getString("nickname", ""));
        tv_nv_UserAccount.setText(prefs.getString("userAccount", ""));
        ImageInExternalStorage imgExStorage = new ImageInExternalStorage(SearchActivity.this, prefs);
        imgExStorage.openFile(ivUserImage);

        if (!prefs.getBoolean("login", false)) {
            //尚未登入點擊頭像 到登入頁
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(SearchActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
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
                        intent.setClass(SearchActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navMap:
                        intent.setClass(SearchActivity.this, MapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSort:
                        intent.setClass(SearchActivity.this, SortActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSearch:
                        initContent();
                        onResume();
                        break;
                    case R.id.navCollection:
                        intent.setClass(SearchActivity.this, CollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
                        startActivity(intent);
                        break;
                    case R.id.navSettings:
                        intent.setClass(SearchActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
                        startActivity(intent);
                        break;
                    default:
                        intent.setClass(SearchActivity.this, UnderDevelopmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SearchActivity.this.finish();
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
            Intent intent = new Intent();
            intent.setClass(SearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void hideSoftKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void searchSetting(Activity inputActivity){
        if(searchBar.getText() != null) {
            SortDAO sortDAO = new SortDAO(SearchActivity.this);
            sortItemList = sortDAO.findResByName(searchBar.getText());

            searchAdapter = new searchAdapter(SearchActivity.this, sortItemList);
        }
    }
    public void searchOther(Activity inputActivity , int sortNumber){
        SortDAO sortDAO = new SortDAO(SearchActivity.this);
        List<SortAs> sortDownList = sortDAO.sortRestaurant(sortNumber);
        sortAdapter = new sortAdapter(SearchActivity.this, sortDownList);
    }

    public String sortName(int sortNumber){
        String[] sortNameAry = {"中式餐廳","西式餐廳","日式餐廳","韓式餐廳","泰式餐廳"
                                ,"港式餐廳","小吃","飲料甜點","冰品","其他"};

        return sortNameAry[sortNumber];
    }

    private Runnable r1 = new Runnable(){
        public void run(){
            //這裡放執行緒要執行的程式。
            searchSetting(SearchActivity.this);
//            rvUp.setAdapter(searchAdapter);
            mThreadHandler.post(r2);
        }
    };

    private Runnable r2 = new Runnable(){
        public void run(){
            //這裡放執行緒要執行的程式。
            searchOther(SearchActivity.this , sortItemList.get(0).getSortNumber());
//            rvDown.setAdapter(sortAdapter);
            mThreadHandler.post(r3);
        }
    };
    private Runnable r3 = new Runnable() {
        @Override
        public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update TextView here!
                            rvUp.setAdapter(searchAdapter);
                            rvDown.setAdapter(sortAdapter);
                        }
                    });

        }
    };
}
