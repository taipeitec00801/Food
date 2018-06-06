package com.example.food.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.R;
import com.example.food.Sort.Sort;
import com.example.food.Sort.SortActivity;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

public class PreferencesSettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_settings);

        initContent();

        recyclerView = findViewById(R.id.pref_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));

        List<PreferencesSet> prefList = getPreferencesSetList();
        recyclerView.setAdapter(new prefAdapter(PreferencesSettingsActivity.this, prefList));
    }
    private class prefAdapter extends
            RecyclerView.Adapter<PreferencesSettingsActivity.prefAdapter.PrefViewHolder> {
        private Context context;
        private List<PreferencesSet> prefList;

        prefAdapter(Context context, List<PreferencesSet> prefList) {
            this.context = context;
            this.prefList = prefList;
        }

        class PrefViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImageLeft, ivImageRight;
            TextView tvNameRight, tvNameLeft;
            CardView cvLeft, cvRight;
            CardView cvMoveL,cvMoveR;
            MaterialRippleLayout mrlL,mrlR;
            SpinKitView skvL , skvR;

            PrefViewHolder(View itemView) {
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

                skvL = itemView.findViewById(R.id.sort_item_spin_kit_L);
                skvR = itemView.findViewById(R.id.sort_item_spin_kit_R);
            }
        }

        @Override
        public int getItemCount() {
            return prefList.size();
        }

        @Override
        public SortActivity.prefAdapter.SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.sort_item, viewGroup, false);

            return new SortActivity.prefAdapter.SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final SortActivity.prefAdapter.SortViewHolder viewHolder, final int position) {
            final Sort sort = sortList.get(position);
            viewHolder.ivImageLeft.setImageResource(sort.getIvLsrc());
            viewHolder.tvNameLeft.setText(String.valueOf(sort.getTvLname()));
            viewHolder.tvNameLeft.setTextColor(colorL);
            viewHolder.cvLeft.setCardBackgroundColor(colorL);

            viewHolder.ivImageRight.setImageResource(sort.getIvRsrc());
            viewHolder.tvNameRight.setText(String.valueOf(sort.getTvRname()));
            viewHolder.tvNameRight.setTextColor(colorR);
            viewHolder.cvRight.setCardBackgroundColor(colorR);

            //點擊左item後將資料寫上Button上，此方法將會點擊背後outBt一次。
            // outBt寫上position以分辨是哪一個Item點擊
            // comeBt的開啟狀態(true)決定送來的Item是經由左邊送來的。
            viewHolder.mrlL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(outBt.isEnabled()){
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        comeBt.setEnabled(true);
                        outBt.performClick();
                        viewHolder.skvL.setVisibility(View.VISIBLE);
                    }

                }
            });
            //點擊右item後將資料寫上Button上，此方法將會點擊背後outBt一次。
            // outBt寫上position以分辨是哪一個Item點擊
            // comeBt的開啟狀態(false)決定送來的Item是經由右邊送來的。
            viewHolder.mrlR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(outBt.isEnabled()){
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        comeBt.setEnabled(false);
                        outBt.performClick();
                        viewHolder.skvR.setVisibility(View.VISIBLE);
                    }
                }
            });

            //設定每個item動畫延遲時間，position超過3，不使用動畫。
            if(position <= 3){
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
    public List<PreferencesSet> getPreferencesSetList() {
        List<PreferencesSet> prefList = new ArrayList<>();
        prefList.add(new PreferencesSet(R.drawable.s03,0,"中式餐廳",
                R.drawable.s02,1,"西式餐廳"));

        prefList.add(new PreferencesSet(R.drawable.s05,2,"韓式餐廳",
                R.drawable.s04,3,"日式餐廳"));

        prefList.add(new PreferencesSet(R.drawable.s06,4,"港式餐廳",
                R.drawable.s07,5,"泰式餐廳"));

        prefList.add(new PreferencesSet(R.drawable.s08,6,"小吃美食",
                R.drawable.s09,7,"冰涼滋味"));

        prefList.add(new PreferencesSet(R.drawable.s10,8,"甜點飲品",
                R.drawable.s01,9,"隱藏美食"));

        return prefList;
    }
}
