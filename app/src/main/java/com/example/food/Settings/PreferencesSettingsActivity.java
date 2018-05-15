package com.example.food.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;

import java.util.ArrayList;
import java.util.List;

public class PreferencesSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_settings);

        initContent();
        RecyclerView reSettingsPreferences = findViewById(R.id.reSettingsPreferences);
        reSettingsPreferences.setLayoutManager(new LinearLayoutManager(PreferencesSettingsActivity.this));
        List<UserPreferencesSetting> UserPreferencesSettings = getUserPreferencesSettings();
        reSettingsPreferences.setAdapter(new UserPreferencesSettingsAdapter(UserPreferencesSettings, PreferencesSettingsActivity.this));


    }

    private void initContent() {
        Toolbar settingsPreferencesToolbar = findViewById(R.id.settingsPreferencesToolbar);
        settingsPreferencesToolbar.setTitle(R.string.textPreferencesSettings);

        setSupportActionBar(settingsPreferencesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private List<UserPreferencesSetting> getUserPreferencesSettings() {
        List<UserPreferencesSetting> userPreferencesSettings = new ArrayList<>();
        userPreferencesSettings.add(new UserPreferencesSetting(R.drawable.chinese_style,
                this.getString(R.string.textChineseStyle), R.drawable.western_food,
                this.getString(R.string.textWesternStyle)));
        userPreferencesSettings.add(new UserPreferencesSetting(R.drawable.japan_food,
                this.getString(R.string.textJapaneseStyle), R.drawable.korea_food,
                this.getString(R.string.textKoreaStyle)));
        userPreferencesSettings.add(new UserPreferencesSetting(R.drawable.thai_food,
                this.getString(R.string.textThaiStyle), R.drawable.guangdong_food,
                this.getString(R.string.textGuangdongStyle)));
        userPreferencesSettings.add(new UserPreferencesSetting(R.drawable.local_dishes,
                this.getString(R.string.textLocalDishes), R.drawable.drinks_and_desserts,
                this.getString(R.string.textDrinksDesserts)));
        userPreferencesSettings.add(new UserPreferencesSetting(R.drawable.ice_cream,
                this.getString(R.string.textIceCream), R.drawable.other_food,
                this.getString(R.string.textOther)));
        return userPreferencesSettings;
    }

    private class UserPreferencesSettingsAdapter extends RecyclerView.Adapter<UserPreferencesSettingsViewHolder> {
        List<UserPreferencesSetting> userPreferencesSettings;
        Context context;

        public UserPreferencesSettingsAdapter(List<UserPreferencesSetting> userPreferencesSettings, Context context) {
            this.context = context;
            this.userPreferencesSettings = userPreferencesSettings;
        }

        @NonNull
        @Override   //呼叫順序(2)
        public UserPreferencesSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate
                    (R.layout.item_preferences, parent, false);
            return new UserPreferencesSettingsViewHolder(itemView);
        }

        /*  onBindViewHolder中的RecyclerView.ViewHolder
            會讀取 onCreateViewHolder方法的回傳值 UserPreferencesSettingsViewHolder(itemView) */
        @Override   //呼叫順序(3)
        public void onBindViewHolder(@NonNull UserPreferencesSettingsViewHolder holder, int position) {
            final UserPreferencesSetting userPreferencesSetting = userPreferencesSettings.get(position);    //position = index
            holder.ivPreferencesLeft.setImageResource(userPreferencesSetting.getIvPreferencesLeft());
            holder.ivPreferencesRight.setImageResource(userPreferencesSetting.getIvPreferencesRight());
            holder.tvPreferencesLeft.setText(userPreferencesSetting.getTvPreferencesLeft());
            holder.tvPreferencesRight.setText(userPreferencesSetting.getTvPreferencesRight());
            //  點擊事件這
            holder.cvPreferencesLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.cvPreferencesRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }


        @Override   //呼叫順序(1)
        public int getItemCount() {
            return userPreferencesSettings.size();   //詢問資料筆數  return 總數
        }
    }
    //  要 hold 助我們想變動(管理、修改)部分
    private class UserPreferencesSettingsViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPreferencesLeft, ivPreferencesRight;
        TextView tvPreferencesLeft, tvPreferencesRight;
        CardView cvPreferencesLeft , cvPreferencesRight;
        public UserPreferencesSettingsViewHolder(View itemView) {
            super(itemView);
            ivPreferencesLeft = itemView.findViewById(R.id.ivPreferencesLeft);
            ivPreferencesRight = itemView.findViewById(R.id.ivPreferencesRight);
            tvPreferencesLeft = itemView.findViewById(R.id.tvPreferencesLeft);
            tvPreferencesRight = itemView.findViewById(R.id.tvPreferencesRight);
            cvPreferencesLeft = itemView.findViewById(R.id.cvPreferencesLeft);
            cvPreferencesRight = itemView.findViewById(R.id.cvPreferencesRight);
        }
    }
}
