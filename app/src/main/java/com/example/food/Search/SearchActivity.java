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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.R;
import com.example.food.Sort.Common;
import com.example.food.Sort.SortAs;
import com.example.food.Sort.SortDAO;
import com.example.food.Sort.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private MaterialSearchBar searchBar;
    private List<SortAs> sortItemList = null;
    private sortAdapter sortAdapter = null;
    private boolean ordercont = true;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //建立ToolBar
        initContent();
        setupNavigationDrawerMenu();
        recyclerView = findViewById(R.id.search_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(searchBar.isSearchEnabled()){
                    searchBar.disableSearch();
                }
                return false;
            }
        });
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
                                    if (o1.getNumber() > o2.getNumber()) {
                                        //o1 排到 o2 之前
                                        return -1;
                                    } else if (o1.getNumber() < o2.getNumber()) {
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
                                    if (o1.getNumber() > o2.getNumber()) {
                                        //o2 排到 o1 之前
                                        return 1;
                                    } else if (o1.getNumber() < o2.getNumber()) {
                                        //o2 排到 o1 之後
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        }
                        if(sortAdapter != null){
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
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
                searchSetting(SearchActivity.this);
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
        private int imageSize;

        sortAdapter(Context context, List<SortAs> sortList) {
            this.context = context;
            this.sortList = sortList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 8;
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
//            ImageView imageView ,likeView;
//            TextView tvName,tvLike;
            SortViewHolder(View itemView) {
                super(itemView);
//                imageView = itemView.findViewById(R.id.sortAs_item_iv);
//                tvName =  itemView.findViewById(R.id.sortAs_item_tv);
//                tvLike =  itemView.findViewById(R.id.sortAs_item_like_tv);
//                likeView = itemView.findViewById(R.id.sortAs_item_like_iv);
            }
        }
        public void restartItem() {
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return sortList.size();
        }

        @Override
        public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.sort_as_item, viewGroup, false);
            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SortViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
//            viewHolder.likeView.setImageResource(R.drawable.like);
//            viewHolder.imageView.setImageResource(R.drawable.p01);
//            viewHolder.tvName.setText(String.valueOf(sort.getName()));
//            viewHolder.tvLike.setText(String.valueOf(sort.getNumber()));
//            viewHolder.itemView.setElevation(0);
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
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);

        InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void searchSetting(Activity inputActivity){
        if(searchBar.getText() != null) {
            sortItemList = null;
            recyclerView.setAdapter(null);
            SortDAO sortDAO = new SortDAO(inputActivity);
            sortItemList = sortDAO.findResByName(searchBar.getText());
            sortAdapter = new sortAdapter(this, sortItemList);
            recyclerView.setAdapter(sortAdapter);
        }
    }
}
