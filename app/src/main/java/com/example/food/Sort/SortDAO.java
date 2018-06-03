package com.example.food.Sort;

import android.app.Activity;
import android.util.Log;

import com.example.food.Sort.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            //透過IP和資料庫名稱找到資料庫。
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

            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的findSortByRes方法，傳入參數sortNumbers，
            //以下為寫在伺服器的方法
            //public List<Sort> findSortByRes(int sortNumbers) {
            //		String sql = "SELECT SORTNAME,LIKENUMBER FROM RESTAURANT WHERE SORTRES=?;";
            //		Connection conn = null;
            //		PreparedStatement ps = null;
            //		List<Sort> sorts = new ArrayList<Sort>();
            //		try {
            //			conn = DriverManager.getConnection(Common.URL, Common.USER,
            //					Common.PASSWORD);
            //			ps = conn.prepareStatement(sql);
            //			ps.setInt(1, sortNumbers);
            //			ResultSet rs = ps.executeQuery();
            //			if (rs.next()) {
            //       /*********將查詢結果取出後寫入Sort物件在加進List<Sort>中回傳。********/
            //				String sortName = rs.getString(1);
            //				int likeNumber = rs.getInt(2);
            //				Sort sort = new Sort(sortNumbers,sortName,likeNumber);
            //				sorts.add(sort);
            //			}
            //		} catch (SQLException e) {
            //			e.printStackTrace();
            //		} finally {
            //			try {
            //				if (ps != null) {
            //					ps.close();
            //				}
            //				if (conn != null) {
            //					conn.close();
            //				}
            //			} catch (SQLException e) {
            //				e.printStackTrace();
            //			}
            //		}
            //		return sorts;
            //	}
            //}
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
