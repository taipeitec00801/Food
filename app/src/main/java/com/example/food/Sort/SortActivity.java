package com.example.food.Sort;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;

import java.util.ArrayList;
import java.util.List;

public class SortActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        //建立ToolBar
        setTitle(R.string.textNull);
        initContent();
        setupNavigationDrawerMenu();

        //
        RecyclerView recyclerView = findViewById(R.id.sort_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        List<Sort> sortList = getSortList();
        recyclerView.setAdapter(new sortAdapter(this, sortList));


    }

    private void initContent() {
        toolbar = findViewById(R.id.sort_toolbar);
        toolbar.setTitle(R.string.Sort_Activity_Title);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.sort_navigationView);
        drawerLayout = findViewById(R.id.sort_drawerLayout);
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


    private class sortAdapter extends
            RecyclerView.Adapter<sortAdapter.SortViewHolder> {
        private Context context;
        private List<Sort> sortList;

        sortAdapter(Context context, List<Sort> sortList) {
            this.context = context;
            this.sortList = sortList;
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImageLeft, ivImageRight;
            TextView tvNameRight, tvNameLeft;
            CardView cvUp, cvDown, cv;

            SortViewHolder(View itemView) {
                super(itemView);
                ivImageLeft = itemView.findViewById(R.id.sort_item_ivLeft);
                ivImageRight = itemView.findViewById(R.id.sort_item_ivRight);
                tvNameRight = itemView.findViewById(R.id.sort_item_tvRight);
                tvNameLeft = itemView.findViewById(R.id.sort_item_tvLeft);
                cv = itemView.findViewById(R.id.sort_item_cv);
                cvUp = itemView.findViewById(R.id.sort_item_cvUp);
                cvDown = itemView.findViewById(R.id.sort_item_cvDown);
            }
        }

        @Override
        public int getItemCount() {
            return sortList.size();
        }

        @Override
        public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.sort_item, viewGroup, false);

            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SortViewHolder viewHolder, int position) {
            final Sort sort = sortList.get(position);
            viewHolder.ivImageLeft.setImageResource(sort.getImageL());
            viewHolder.tvNameRight.setText(String.valueOf(sort.getNameR()));
            viewHolder.ivImageRight.setImageResource(sort.getImageR());
            viewHolder.tvNameLeft.setText(String.valueOf(sort.getNameL()));
//            viewHolder.cv.setMinimumHeight(getResources().getDisplayMetrics().heightPixels / 2);
            viewHolder.cvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("Sort", sort.getNameR());
                    bundle.putInt("SortRes", sort.getSortResUp());
                    intent.putExtras(bundle);
                    intent.setClass(SortActivity.this, SortAsActivity.class);
                    startActivity(intent);
//                    SortActivity.this.finish();
                }
            });
            viewHolder.cvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("Sort", sort.getNameL());
                    bundle.putInt("SortRes", sort.getSortResDown());
                    intent.putExtras(bundle);
                    intent.setClass(SortActivity.this, SortAsActivity.class);
                    startActivity(intent);
                }
            });
            Animation am = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_down);
            viewHolder.cvDown.setAnimation(am);
            Animation am1 = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_up);
            viewHolder.cvUp.setAnimation(am1);
        }
    }

    public List<Sort> getSortList() {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("中式", R.drawable.c01, 1,
                "西式", R.drawable.c01, 0));
        sortList.add(new Sort("泰式", R.drawable.c01, 3,
                "日式", R.drawable.c01, 2));
        sortList.add(new Sort("韓式", R.drawable.c01, 5,
                "港式", R.drawable.c01, 4));
        sortList.add(new Sort("小吃", R.drawable.c01, 7,
                "飲料", R.drawable.c01, 6));
        sortList.add(new Sort("冰品", R.drawable.c01, 9,
                "其他", R.drawable.c01, 8));

        return sortList;
    }
}
