package com.example.food.DAO;

import android.app.Activity;
import android.util.Log;


import com.example.food.AppModel.CommentForApp;
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
    private CommonTask StoreTask, cTask;
    private List<Store> StoreList;
    private List<CommentForApp> cfaList;
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
            String url = Common.URL+"/appStore";
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
              //  Common.showToast(inputActivity, "查無資料");
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

    public List<Store> getStoreByTop() {
        if (Common.networkConnected(inputActivity)) {
            StoreList = null;
            Gson gson = new Gson();
            String url = Common.URL+"/appStore";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getTopStores");
            String jsonOut = jsonObject.toString();

            StoreTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = StoreTask.execute().get();
                Log.d("jsonIn",jsonIn);
                Type listType = new TypeToken<List<Store>>() {
                }.getType();
                StoreList = gson.fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
            if (StoreList == null || StoreList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
                //System.out.println("StoreDAOLIST IS EMPTY");
                //  Common.showToast(inputActivity, "查無資料");
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

    public List<Store> getCollectionListByUser(String userCollection) {
        if (Common.networkConnected(inputActivity)) {
            StoreList = null;
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL+"/appStore";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCollectionByUser");
            jsonObject.addProperty("userCollection",userCollection);
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
                // Common.showToast(inputActivity, "查無資料");
            } else {
                //回傳StoreList。
                return StoreList;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return StoreList;
    }



    public List<CommentForApp> getCommentForApp(Integer storeId) {
        if (Common.networkConnected(inputActivity)) {
            cfaList = null;
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL+"/appGetComment";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("storeId",storeId);
            String jsonOut = jsonObject.toString();
            Log.d("jsonObject",jsonObject.toString());
            cTask = new CommonTask(url, jsonOut);
            try {
                //用字串儲存伺服器回應的json格式字串。
                String jsonIn = cTask.execute().get();
                Log.d("jsonIn",jsonIn);
                //利用TypeToken指定資料型態為List<Store>
                Type listType = new TypeToken<List<CommentForApp>>() {
                }.getType();
                //利用Gson把json字串轉成Type指定的型態(List<SortAs>)後放入sortAsList(List<SortAs>)。
                cfaList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
            if (cfaList == null || cfaList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
                // Common.showToast(inputActivity, "查無資料");
            } else {
                //回傳StoreList。
                return cfaList;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return cfaList;
    }


    public Boolean updateStRecom(Integer memberId, Integer storeId, Integer recomYN) {
        Boolean isStRecom = false;
        if (Common.networkConnected(inputActivity)) {

            String url = Common.URL + "/appUpdateStRecom";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("memberId", memberId);
            jsonObject.addProperty("storeId", storeId);
            jsonObject.addProperty("recomYN", recomYN);

            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count > 0) {
                isStRecom = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return isStRecom;
    }

    public Integer getStRecom(Integer memberId, Integer storeId) {
        Integer isStRecom = 0;
        if (Common.networkConnected(inputActivity)) {

            String url = Common.URL + "/appGetStRecom";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("memberId", memberId);
            jsonObject.addProperty("storeId", storeId);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                isStRecom = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return isStRecom;
    }
}
