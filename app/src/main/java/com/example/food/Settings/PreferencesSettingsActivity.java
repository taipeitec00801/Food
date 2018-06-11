package com.example.food.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.R;
import com.example.food.Sort.Sort;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

public class PreferencesSettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button outBt, comeBt;
    private Button btSetBy;
    private SpinKitView skvSetBy;
    private String newPreference;
    private int colorRip, colorNoRip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_settings);

        initContent();
        skvSetBy = findViewById(R.id.pref_spinKit);
        outBt = findViewById(R.id.pref_bt);
        comeBt = findViewById(R.id.pref_comebt);
        //儲存按鈕
        btSetBy = findViewById(R.id.bt_set_by);
        btSetBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skvSetBy.setVisibility(View.VISIBLE);
            }
        });

        recyclerView = findViewById(R.id.rvSetting_pref);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        List<Sort> prefList = getSortList();
        recyclerView.setAdapter(new prefAdapter(PreferencesSettingsActivity.this, prefList));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        outBt.setEnabled(true);
    }

    private class prefAdapter extends
            RecyclerView.Adapter<PreferencesSettingsActivity.prefAdapter.PrefViewHolder> {
        private Context context;
        private List<Sort> prefList;

        prefAdapter(Context context, List<Sort> prefList) {
            this.context = context;
            this.prefList = prefList;
        }

        //PrefViewHolder 要 hold 助我們想變動(管理、修改)部分
        class PrefViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImageLeft, ivImageRight;
            TextView tvNameRight, tvNameLeft;
            CardView cvLeft, cvRight;
            CardView cvMoveL, cvMoveR;
            MaterialRippleLayout mrlL, mrlR;
            CardView cvrL, cvrR;

            PrefViewHolder(View itemView) {
                super(itemView);
                ivImageLeft = itemView.findViewById(R.id.pref_item_Left_iv);
                tvNameLeft = itemView.findViewById(R.id.pref_item_Left_tv);

                ivImageRight = itemView.findViewById(R.id.pref_item_Right_iv);
                tvNameRight = itemView.findViewById(R.id.pref_item_Right_tv);

                cvLeft = itemView.findViewById(R.id.pref_item_Lift_cv);
                cvMoveL = itemView.findViewById(R.id.pref_item_cv_moveL);

                cvRight = itemView.findViewById(R.id.pref_item_Right_cv);
                cvMoveR = itemView.findViewById(R.id.pref_item_cv_moveR);

                mrlL = itemView.findViewById(R.id.pref_item_ripple_L);
                mrlR = itemView.findViewById(R.id.pref_item_ripple_R);

            }
        }

        @Override
        public int getItemCount() {
            return prefList.size();
        }

        @NonNull
        @Override
        public PrefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.pref_item, parent, false);

            return new PreferencesSettingsActivity.prefAdapter.PrefViewHolder(itemView);
        }

        //onBindViewHolder中的RecyclerView.ViewHolder
        //會讀取 onCreateViewHolder方法的回傳值 PrefViewHolder(itemView)
        @Override
        public void onBindViewHolder(@NonNull final PrefViewHolder viewHolder, final int position) {
            final Sort prefSet = prefList.get(position);        //position = index
            colorRip = 0xd02e2a2a;
            colorNoRip = 0xfffbd786;

            viewHolder.ivImageLeft.setImageResource(prefSet.getIvLsrc());
            viewHolder.tvNameLeft.setText(String.valueOf(prefSet.getTvLname()));

            viewHolder.ivImageRight.setImageResource(prefSet.getIvRsrc());
            viewHolder.tvNameRight.setText(String.valueOf(prefSet.getTvRname()));

            viewHolder.mrlL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String isS = String.valueOf(viewHolder.cvLeft.getCardBackgroundColor());
                            int isR = Integer.parseInt(isS);
                            Log.e("CardBackgroundColor", isS);
                            if (isR == colorRip) {

                            } else if (isR == colorNoRip) {

                            }
//                    if (outBt.isEnabled()) {
//                        String number = String.valueOf(position);
//                        outBt.setText(number);
////                        comeBt.setEnabled(true);
////                        outBt.performClick();
//                        viewHolder.cvLeft.setCardBackgroundColor(colorRip);
//                        outBt.setEnabled(false);
//                    } else {
//                        viewHolder.cvLeft.setCardBackgroundColor(colorNoRip);
//                        outBt.setEnabled(true);
//                    }
                        }
                    });
                }
            });

            viewHolder.mrlR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (outBt.isEnabled()) {
                        String number = String.valueOf(position);
                        outBt.setText(number);
//                        comeBt.setEnabled(false);
//                        outBt.performClick();
                        viewHolder.cvRight.setCardBackgroundColor(colorRip);
                    } else {
                        viewHolder.cvRight.setCardBackgroundColor(colorNoRip);
                    }
                }
            });

            //設定每個item動畫延遲時間，position超過3，不使用動畫。
            if (position <= 3) {
                long aniTime = 100 * position;
                Animation am = AnimationUtils.loadAnimation(PreferencesSettingsActivity.this, R.anim.sort_item_down);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveL.setAnimation(am);
                am = AnimationUtils.loadAnimation(PreferencesSettingsActivity.this, R.anim.sort_item_up);
                am.setStartOffset(aniTime);
                viewHolder.cvMoveR.setAnimation(am);
            }
        }
    }

    private void initContent() {
        Toolbar settingsPreferencesToolbar = findViewById(R.id.settingsPreferencesToolbar);
        settingsPreferencesToolbar.setTitle(R.string.textPreferencesSettings);

        setSupportActionBar(settingsPreferencesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //分類的資料庫
    public List<Sort> getSortList() {
        List<Sort> prefList = new ArrayList<>();
        prefList.add(new Sort(R.drawable.s03, 0, "中式餐廳",
                R.drawable.s02, 1, "西式餐廳"));

        prefList.add(new Sort(R.drawable.s05, 2, "韓式餐廳",
                R.drawable.s04, 3, "日式餐廳"));

        prefList.add(new Sort(R.drawable.s06, 4, "港式餐廳",
                R.drawable.s07, 5, "泰式餐廳"));

        prefList.add(new Sort(R.drawable.s08, 6, "小吃美食",
                R.drawable.s09, 7, "冰涼滋味"));

        prefList.add(new Sort(R.drawable.s10, 8, "甜點飲品",
                R.drawable.s01, 9, "隱藏美食"));

        return prefList;
    }
}
