package com.example.food.DAO;

import android.app.Activity;
import android.util.Log;

import com.example.food.AppModel.CommentForApp;
import com.example.food.AppModel.Member;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CommentDAO {
    private Activity inputActivity;
    private String TAG;
    private CommonTask MemberTask;
    private List<CommentForApp> commentForApp;

    public CommentDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
    }

    public List<CommentForApp> getStoreCommById(Integer storeId) {
        if (Common.networkConnected(inputActivity)) {
            commentForApp = null;
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL+"/appGetComment";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCommByStore");
            jsonObject.addProperty("storeId",storeId);
            String jsonOut = jsonObject.toString();
            Log.d("jsonObject",jsonObject.toString());
            MemberTask = new CommonTask(url, jsonOut);
            try {
                //用字串儲存伺服器回應的json格式字串。
                String jsonIn = MemberTask.execute().get();
                Log.d("jsonIn",jsonIn);
                //利用TypeToken指定資料型態為List<Store>
                Type listType = new TypeToken<List<CommentForApp>>() {
                }.getType();
                //利用Gson把json字串轉成Type指定的型態(List<SortAs>)後放入sortAsList(List<SortAs>)。
                commentForApp = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
            if (commentForApp == null || commentForApp.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
                // Common.showToast(inputActivity, "查無資料");
            } else {
                //回傳StoreList。
                return commentForApp;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return commentForApp;
    }

}
