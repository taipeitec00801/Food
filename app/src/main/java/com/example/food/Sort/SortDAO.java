package com.example.food.Sort;

import android.app.Activity;
import android.util.Log;

import com.example.food.AppModel.SortAs;
import com.example.food.Sort.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SortDAO {
    private Activity inputActivity;
    private String TAG;
    private CommonTask spotGetAllTask;
    private List<SortAs> sortAsList = null;

    public SortDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
    }

    public List<SortAs> sortRestaurant(int sortNumber) {
        if (Common.networkConnected(inputActivity)) {
            sortAsList = null;
            //建立商店物件的List，以便接收資料庫回傳的資料。
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //將收到的餐廳編號轉成字串。
            String sortNumbers = String.valueOf(sortNumber);
            //透過IP和資料庫名稱找到伺服器。
            String url = Common.URL + "/StoreDataServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為findSortByRes
            jsonObject.addProperty("action", "findStoreByNumber");
            //jsonObject新增屬性sortNumber其值為sortNumbers
            jsonObject.addProperty("sortNumber", sortNumbers);
            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            spotGetAllTask = new CommonTask(url, jsonOut);

            try {
                //用字串儲存伺服器回應的json格式字串。
//                spotGetAllTask.get(500, TimeUnit.MILLISECONDS);
                String jsonIn = spotGetAllTask.execute().get();
                //利用TypeToken指定資料型態為List<SortAs>
                Type listType = new TypeToken<List<SortAs>>() {
                }.getType();
                //利用Gson把json字串轉成Type指定的型態(List<SortAs>)後放入sortAsList(List<SortAs>)。
                sortAsList = gson.fromJson(jsonIn, listType);
//            }catch(TimeoutException te){
//                Common.showToast(inputActivity, "伺服器無回應");
            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (sortAsList == null || sortAsList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
//                Common.showToast(inputActivity, "查無資料");
            } else {
                //回傳sortAsList。
                return sortAsList;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return sortAsList;
    }


    public List<SortAs> findResByName(String resName){
//        if (Common.networkConnected(inputActivity)) {
//            sortAsList = null;
//            Gson gson = new Gson();
//            String url = Common.URL + "/ssServlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "findByName");
//            jsonObject.addProperty("resName",resName);
//            String jsonOut = jsonObject.toString();
//            spotGetAllTask = new CommonTask(url, jsonOut);
//            try {
//                String jsonIn = spotGetAllTask.execute().get();
//                Type listType = new TypeToken<List<SortAs>>() {}.getType();
//                sortAsList = gson.fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (sortAsList == null || sortAsList.isEmpty()) {
//                Common.showToast(inputActivity, "No spots found");
//            } else {
//                return sortAsList;
//            }
//        } else {
//            Common.showToast(inputActivity, "no network connection available");
//        }
        return  null;
//        return sortAsList;
    }
}
