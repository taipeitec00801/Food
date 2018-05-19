package com.example.food.Sort;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
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
        toolbar.setTitle("餐廳分類");
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
            CardView cvLeft, cvRight;
            CardView cvMoveL,cvMoveR;
            MaterialRippleLayout mrlL,mrlR;


            SortViewHolder(View itemView) {
                super(itemView);
                ivImageLeft = itemView.findViewById(R.id.sort_item_Left_iv);
                tvNameLeft = itemView.findViewById(R.id.sort_item_Left_tv);

                ivImageRight = itemView.findViewById(R.id.sort_item_Right_iv);
                tvNameRight = itemView.findViewById(R.id.sort_item_Right_tv);

                cvLeft = itemView.findViewById(R.id.sort_item_Lift_cv);
                cvMoveL = itemView.findViewById(R.id.sort_item_cv_moveL);

                cvRight = itemView.findViewById(R.id.sort_item_Right_cv);
                cvMoveR = itemView.findViewById(R.id.sort_item_cv_moveR);

                mrlL = itemView.findViewById(R.id.sort_item_ripple_L);
                mrlR = itemView.findViewById(R.id.sort_item_ripple_R);
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
        public void onBindViewHolder(final SortViewHolder viewHolder, int position) {
            final Sort sort = sortList.get(position);
            final Bundle bundle = new Bundle();

            viewHolder.ivImageLeft.setImageResource(sort.getIvLsrc());
            viewHolder.tvNameLeft.setText(String.valueOf(sort.getTvLname()));
            viewHolder.tvNameLeft.setTextColor(sort.getCvLcolor());
            viewHolder.cvLeft.setCardBackgroundColor(sort.getCvLcolor());

            viewHolder.ivImageRight.setImageResource(sort.getIvRsrc());
            viewHolder.tvNameRight.setText(String.valueOf(sort.getTvRname()));
            viewHolder.tvNameRight.setTextColor(sort.getCvRcolor());
            viewHolder.cvRight.setCardBackgroundColor(sort.getCvRcolor());

            //點擊左item後將資料打包放進傳送門。
            viewHolder.mrlL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("Sort", sort.getTvLname());
                    bundle.putInt("SortRes", sort.getSortLnum());
                    portalToSortAs(bundle);
                }
            });
            //點擊右item後將資料打包放進傳送門。
            viewHolder.mrlR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putString("Sort", sort.getTvLname());
                    bundle.putInt("SortRes", sort.getSortLnum());
                    portalToSortAs(bundle);
                }
            });

            //設定每個item動畫延遲時間，position超過3，不使用動畫。
            if(position <= 3){
                long aniTime = 100 * position;
                Animation am = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_down);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveL.setAnimation(am);
                am = AnimationUtils.loadAnimation(SortActivity.this, R.anim.sort_item_up);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveR.setAnimation(am);
            }


        }
    }
    //傳送門，將送進來的資料傳到下一頁。
    public void portalToSortAs(Bundle itemBundle){
        Intent intent = new Intent();
        Bundle bundle = itemBundle;
        intent.putExtras(bundle);
        intent.setClass(SortActivity.this, SortAsActivity.class);
        startActivity(intent);
    }
    //分類的資料庫，顏色使用16進位
    public List<Sort> getSortList() {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort(0xff34CCD6,R.drawable.s03,0,"中式餐廳",
                0xffFF7676,R.drawable.s02,1,"西式餐廳"));

        sortList.add(new Sort(0xffFF7676,R.drawable.s05,2,"韓式餐廳",
                0xff34CCD6,R.drawable.s04,3,"日式餐廳"));

        sortList.add(new Sort(0xff34CCD6,R.drawable.s06,4,"港式餐廳",
                0xffFF7676,R.drawable.s07,5,"泰式餐廳"));

        sortList.add(new Sort(0xffFF7676,R.drawable.s08,6,"小吃",
                0xff34CCD6,R.drawable.s09,7,"冰品"));

        sortList.add(new Sort(0xff34CCD6,R.drawable.s10,8,"飲料甜點",
                0xffFF7676,R.drawable.s01,9,"其他"));


        return sortList;
    }
}
