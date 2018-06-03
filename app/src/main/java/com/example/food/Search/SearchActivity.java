package com.example.food.Search;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.R;
import com.example.food.Sort.SortAs;
import com.example.food.Sort.SortDAO;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String TAG = "SearchActivity";
    private RecyclerView rvUp,rvDown;
    private MaterialSearchBar searchBar;
    private List<SortAs> sortItemList = null;
    private sortAdapter sortAdapter = null;
    private searchAdapter searchAdapter = null;
    private boolean ordercont = true;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private TextView tvAbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //建立ToolBar
        initContent();
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
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.search_order_like) {
                    if (sortItemList != null) {
                        if(ordercont) {
                            ordercont = false;
                            Collections.sort(sortItemList, new Comparator<SortAs>() {
                                @Override
                                public int compare(SortAs o1, SortAs o2) {
                                    if (o1.getStoreRecomCount() > o2.getStoreRecomCount()) {
                                        //o1 排到 o2 之前
                                        return -1;
                                    } else if (o1.getStoreRecomCount() < o2.getStoreRecomCount()) {
                                        //o1 排到 o2 之後
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        }else{
                            ordercont = true;
                            Collections.sort(sortItemList, new Comparator<SortAs>() {
                                @Override
                                public int compare(SortAs o1, SortAs o2) {
                                    if (o1.getStoreRecomCount() > o2.getStoreRecomCount()) {
                                        //o2 排到 o1 之前
                                        return 1;
                                    } else if (o1.getStoreRecomCount() < o2.getStoreRecomCount()) {
                                        //o2 排到 o1 之後
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        }
                        rvUp.getAdapter().notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

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
                try {
                    mThread.join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(rvUp.getAdapter()==null||rvDown.getAdapter()==null){
                    rvUp.setAdapter(searchAdapter);
                    rvDown.setAdapter(sortAdapter);
                }else{
                    sortAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetChanged();
                    rvUp.setAdapter(searchAdapter);
//                    rvDown.setAdapter(sortAdapter);
                }
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

        @Override
        public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.search_down_item, viewGroup, false);
            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SortViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.resImg.setImageResource(R.drawable.cf);
            viewHolder.resName.setText(String.valueOf(sort.getStoreName()));
            viewHolder.likeNumber.setText(String.valueOf(sort.getSortNumber()));
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

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.search_item, viewGroup, false);
            return new SearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SearchViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.resName.setText(sort.getStoreName());
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
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
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

    public void hideSoftKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void searchSetting(Activity inputActivity){
        if(searchBar.getText() != null) {
            SortDAO sortDAO = new SortDAO(SearchActivity.this);
            sortItemList = sortDAO.findResByName(searchBar.getText());
            sortAdapter = new sortAdapter(SearchActivity.this, sortItemList);
            searchAdapter = new searchAdapter(SearchActivity.this, sortItemList);
        }
    }
    private Runnable r1 = new Runnable(){
        public void run(){
            //這裡放執行緒要執行的程式。
            searchSetting(SearchActivity.this);

//            mThreadHandler.post();
        }
    };
//
//    private Runnable r2 = new Runnable() {
//        @Override
//        public void run() {
//
//        }
//    };
}
