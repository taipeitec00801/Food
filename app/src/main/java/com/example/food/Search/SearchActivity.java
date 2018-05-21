package com.example.food.Search;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;
import com.example.food.Sort.Common;
import com.example.food.Sort.SortAs;
import com.example.food.Sort.SortDAO;
import com.example.food.Sort.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.lang.reflect.Type;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String TAG = "SearchActivity";
    private CommonTask spotGetAllTask;
    private RecyclerView recyclerView;
    private MaterialSearchBar searchBar;
    private List<SortAs> sortItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //建立ToolBar
        initContent();
        setupNavigationDrawerMenu();

        //
        recyclerView = findViewById(R.id.search_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setFocusable(true);
        recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(SearchActivity.this);
            }
        });
        searchBar = findViewById(R.id.searchBar);
        searchBar.setCardViewElevation(10);
//        searchBar.setFocusable(true);
//        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                hideSoftKeyboard(SearchActivity.this);
//            }
//        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchSetting(SearchActivity.this);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                searchSetting(SearchActivity.this);
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
            ImageView imageView ,likeView;
            TextView tvName,tvLike;
            SortViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.sortAs_item_iv);
                tvName =  itemView.findViewById(R.id.sortAs_item_tv);
                tvLike =  itemView.findViewById(R.id.sortAs_item_like_tv);
                likeView = itemView.findViewById(R.id.sortAs_item_like_iv);
            }
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
            viewHolder.likeView.setImageResource(R.drawable.like);
            viewHolder.imageView.setImageResource(R.drawable.p01);
            viewHolder.tvName.setText(String.valueOf(sort.getName()));
            viewHolder.tvLike.setText(String.valueOf(sort.getNumber()));
            viewHolder.itemView.setElevation(0);
        }
    }


    private void initContent() {
        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle("搜尋");
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.search_navigationView);
        drawerLayout = findViewById(R.id.search_drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void searchSetting(Activity inputActivity){
        hideSoftKeyboard(inputActivity);
        if(searchBar.getText() != null){
            if(recyclerView.getAdapter() != null){

                recyclerView.removeAllViews();
            }
            SortDAO sortDAO = new SortDAO(inputActivity);
            sortItemList = sortDAO.findResByName(searchBar.getText());
            recyclerView.setAdapter(new sortAdapter(inputActivity,sortItemList));
        }else{

        }
    }
}
