package com.example.food.Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.example.food.AppModel.Store;
import com.example.food.DAO.StoreDAO;
import com.example.food.R;

import java.io.Serializable;
import java.util.List;

public class WelcomeActivity  extends Activity {
    private StoreDAO storeDAO;
    private List<Store> storeList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        storeDAO = new StoreDAO(WelcomeActivity.this);
        storeList = storeDAO.getStoreByTop();
        Bundle bundle = new Bundle();
        bundle.putSerializable("storeList", (Serializable) storeList);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        WelcomeActivity.this.finish();
       // mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 2000); //2秒跳轉
    }
//    private static final int GOTO_MAIN_ACTIVITY = 0;
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//
//            switch (msg.what) {
//                case GOTO_MAIN_ACTIVITY:
//                    Intent intent = new Intent();
//                    //將原本Activity的換成MainActivity
//                    intent.setClass(WelcomeActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    WelcomeActivity.this.finish();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//    };
}