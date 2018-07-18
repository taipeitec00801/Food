package com.example.food.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;
import com.example.food.DAO.MemberDAO;
import com.example.food.R;
import com.example.food.AppModel.Sort;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreferencesSettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button outBt;
    private Button btSetBy;
    private SpinKitView skvSetBy;
    private SharedPreferences prefs;
    private int colorCvRip, colorCvNoRip, colorTvRip, colorTvNoRip;
    private int prefNum;
    private int[] nowPreference = new int[10];
    private boolean updateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_settings);

        initContent();
        findById();

        //儲存按鈕
        btSetBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skvSetBy.setVisibility(View.VISIBLE);
                if (outBt.isEnabled()) {
                    outBt.setEnabled(false);
                    Thread mThread = new Thread(runnable);
                    mThread.start();
                    try {
                        mThread.join();
                    } catch (InterruptedException e) {
                        System.out.println("執行緒被中斷");
                    }
                }
                checkDialog(updateResult);
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                1, StaggeredGridLayoutManager.VERTICAL));

        List<Sort> prefList = getSortList();
        recyclerView.setAdapter(new prefAdapter(PreferencesSettingsActivity.this, prefList));
    }


    //這裡放執行緒要執行的程式。
    private Runnable runnable = new Runnable() {
        public void run() {
            //編寫Preference 內容
            StringBuilder newPreference = new StringBuilder();
            for (int i = 0; i < nowPreference.length; i++) {
                if (nowPreference[i] == 1) {
                    newPreference.append(i).append(",");
                }
            }
            newPreference = new StringBuilder(newPreference.substring(0, newPreference.length() - 1));

            prefs.edit().putString("preference", newPreference.toString()).apply();
            MemberDAO memberDAO = new MemberDAO(PreferencesSettingsActivity.this);
            String userAccount = prefs.getString("userAccount", "");
            updateResult = memberDAO.updatePreference(userAccount, newPreference.toString());

        }
    };

    //更新結果 提示視窗
    private void checkDialog(boolean updateResult) {
        String result = "更新喜好種類失敗";
        if (updateResult) {
            result = "更新喜好種類成功";
        }
        new MaterialDialog.Builder(PreferencesSettingsActivity.this)
                .title(R.string.textPreferencesSettings)
                .backgroundColorRes(R.color.colorDialogBackground)
                .positiveColorRes(R.color.colorText)
                .content(result)
                .positiveText(R.string.textIKnow)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        skvSetBy.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent();
                        intent.setClass(PreferencesSettingsActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).show();
    }

    private void findById() {
        skvSetBy = findViewById(R.id.pref_spinKit);
        outBt = findViewById(R.id.pref_bt);
        btSetBy = findViewById(R.id.bt_set_by);
        recyclerView = findViewById(R.id.rvSetting_pref);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        outBt.setEnabled(true);
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
            colorCvRip = 0xd02e2a2a;
            colorCvNoRip = 0xfffbd786;
            colorTvRip = 0xaa444444;
            colorTvNoRip = 0xaaffffff;

            viewHolder.ivImageLeft.setImageResource(prefSet.getIvLsrc());
            viewHolder.tvNameLeft.setText(String.valueOf(prefSet.getTvLname()));

            viewHolder.ivImageRight.setImageResource(prefSet.getIvRsrc());
            viewHolder.tvNameRight.setText(String.valueOf(prefSet.getTvRname()));

            viewHolder.mrlL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (outBt.isEnabled()) {
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        outBt.performClick();

                        prefNum = 2 * position;
                        //判斷是點擊狀態 1=已點擊 0=未點擊
                        if (nowPreference[prefNum] == 0) {
                            //選取
                            viewHolder.cvLeft.setCardBackgroundColor(colorCvRip);
                            viewHolder.tvNameLeft.setBackgroundColor(colorTvRip);
                            nowPreference[prefNum] = 1;
                        } else if (nowPreference[prefNum] == 1) {
                            //取消
                            viewHolder.cvLeft.setCardBackgroundColor(colorCvNoRip);
                            viewHolder.tvNameLeft.setBackgroundColor(colorTvNoRip);
                            nowPreference[prefNum] = 0;
                        }
                    }
                }
            });

            viewHolder.mrlR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (outBt.isEnabled()) {
                        String number = String.valueOf(position);
                        outBt.setText(number);
                        outBt.performClick();

                        prefNum = (2 * position) + 1;
                        //判斷是點擊狀態 1=已點擊 0=未點擊
                        if (nowPreference[prefNum] == 0) {
                            //被點擊
                            viewHolder.cvRight.setCardBackgroundColor(colorCvRip);
                            viewHolder.tvNameRight.setBackgroundColor(colorTvRip);
                            nowPreference[prefNum] = 1;
                        } else if (nowPreference[prefNum] == 1) {
                            //取消
                            viewHolder.cvRight.setCardBackgroundColor(colorCvNoRip);
                            viewHolder.tvNameRight.setBackgroundColor(colorTvNoRip);
                            nowPreference[prefNum] = 0;
                        }
                    }
                }
            });

            //設定每個item動畫延遲時間，position超過4，不使用動畫。
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    //分類的資料庫
    public List<Sort> getSortList() {
        List<Sort> prefList = new ArrayList<>();
        prefList.add(new Sort(R.drawable.s03, 0, "中式餐廳",
                R.drawable.s02, 1, "西式餐廳"));

        prefList.add(new Sort(R.drawable.s05, 3, "韓式餐廳",
                R.drawable.s04, 2, "日式餐廳"));

        prefList.add(new Sort(R.drawable.s06, 5, "港式餐廳",
                R.drawable.s07, 4, "泰式餐廳"));

        prefList.add(new Sort(R.drawable.s08, 6, "路邊美食",
                R.drawable.s09, 8, "冰涼滋味"));

        prefList.add(new Sort(R.drawable.s10, 7, "甜點飲品",
                R.drawable.s01, 9, "其他美食"));

        return prefList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(PreferencesSettingsActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
