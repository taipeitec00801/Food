package com.example.food.Sort;

import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;
import com.example.food.Sort.task.CommonTask;
import com.example.food.Sort.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SortAsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "SortAsActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CommonTask spotGetAllTask;
    private ImageTask spotImageTask;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_as);
        initContent();
        setupNavigationDrawerMenu();

        recyclerView = findViewById(R.id.sortAs_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(SortAsActivity.this));
        Bundle bundle = getIntent().getExtras();
//        showAllSpots();
        sortRestaurant(bundle.getInt("SortRes"));
    }

//    private void showAllSpots() {
//
//        if (Common.networkConnected(SortAsActivity.this)) {
//            List<SortAs> sortAsList = null;
//            String url = Common.URL + "/ssServlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAllRes");
//
//            String jsonOut = jsonObject.toString();
//            spotGetAllTask = new CommonTask(url, jsonOut);
//            try {
//                String jsonIn = spotGetAllTask.execute().get();
//                Type listType = new TypeToken<List<SortAs>>() {
//                }.getType();
//                sortAsList = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (sortAsList == null || sortAsList.isEmpty()) {
//                Common.showToast(SortAsActivity.this, "No spots found");
//            } else {
//                recyclerView.setAdapter(new sortAdapter(SortAsActivity.this, sortAsList));
//            }
//        } else {
//            Common.showToast(SortAsActivity.this, "no network connection available");
//        }
//    }


    private void sortRestaurant(int sortNumber){
        if (Common.networkConnected(SortAsActivity.this)) {
            List<SortAs> sortAsList = null;
            Gson gson = new Gson();
            String sortNumbers = String.valueOf(sortNumber);
            String url = Common.URL + "/ssServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findSortByRes");
            jsonObject.addProperty("sortNumber",sortNumbers);
            String jsonOut = jsonObject.toString();
            spotGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = spotGetAllTask.execute().get();
                Type listType = new TypeToken<List<SortAs>>() {}.getType();
                sortAsList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (sortAsList == null || sortAsList.isEmpty()) {
                Common.showToast(SortAsActivity.this, "No spots found");
            } else {
                recyclerView.setAdapter(new sortAdapter(SortAsActivity.this, sortAsList));
            }
        } else {
            Common.showToast(SortAsActivity.this, "no network connection available");
        }
    }
    private void initContent() {
        Bundle bundle = getIntent().getExtras();
        String a = bundle.getString("Sort");
        toolbar = findViewById(R.id.sortAs_toolbar);
        toolbar.setTitle(a);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.sortAs_navigationView);
        drawerLayout = findViewById(R.id.sortAs_drawerLayout);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
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
//            String url = Common.URL + "/ssServlet";
//            String name = sort.getName();
//            spotImageTask = new ImageTask(url, name, imageSize, viewHolder.imageView);
//            spotImageTask.execute();
            viewHolder.likeView.setImageResource(R.drawable.like);
            viewHolder.imageView.setImageResource(R.drawable.p01);
            viewHolder.tvName.setText(String.valueOf(sort.getName()));
            viewHolder.tvLike.setText(String.valueOf(sort.getNumber()));

//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }

}


