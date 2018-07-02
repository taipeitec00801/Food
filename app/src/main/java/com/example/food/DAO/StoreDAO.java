package com.example.food.DAO;

import android.app.Activity;
import android.util.Log;


import com.example.food.AppModel.Store;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.CommonTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StoreDAO {
    private Activity inputActivity;
    private String TAG;
    private CommonTask StoreTask;
    private List<Store> StoreList;

    public StoreDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
    }

    public List<Store> getStoreByDistance() {
        if (Common.networkConnected(inputActivity)) {
            System.out.println("getStoreByDistance() Start!!!");
            StoreList = null;
           // String userLocation = String.valueOf(latLng);
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL+"/StoreDataServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為getStoreByDistance
            jsonObject.addProperty("action", "findStoreByUserlocation");
            //jsonObject新增屬性sortNumber其值為userLocation
            //jsonObject.addProperty("userLocation", userLocation);
            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            Log.d("jsonObject",jsonObject.toString());
            StoreTask = new CommonTask(url, jsonOut);

            try {
                //用字串儲存伺服器回應的json格式字串。
                String jsonIn = StoreTask.execute().get();
                Log.d("jsonIn",jsonIn);
                //利用TypeToken指定資料型態為List<Store>
                Type listType = new TypeToken<List<Store>>() {
                }.getType();
                //利用Gson把json字串轉成Type指定的型態(List<SortAs>)後放入sortAsList(List<SortAs>)。
                StoreList = gson.fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
            if (StoreList == null || StoreList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
                //System.out.println("StoreDAOLIST IS EMPTY");
                Common.showToast(inputActivity, "查無資料");
            } else {
                System.out.println("StoreDAOLIST" + StoreList.size());
                //回傳StoreList。
                return StoreList;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return StoreList;
    }
}
