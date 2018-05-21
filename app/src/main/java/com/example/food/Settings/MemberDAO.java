package com.example.food.Settings;

import android.app.Activity;
import android.util.Log;

import com.example.food.Settings.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MemberDAO {
    private Activity inputActivity;
    private String TAG;
    private List<Member> memberList = null;

    public MemberDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
    }

    public List<Member> userInformation(String userId) {
        if (Common.networkConnected(inputActivity)) {
            memberList = null;
            //建立商店物件的List，以便接收資料庫回傳的資料。
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為findMemberByUserId
            jsonObject.addProperty("action", "findMemberByUserId");

            //jsonObject新增屬性userId其值為userId
            jsonObject.addProperty("userId", userId);

            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            CommonTask spotGetAllTask = new CommonTask(url, jsonOut);

            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的findMemberByUserId方法，傳入參數userId，

            try {
                //用字串儲存伺服器回應的json格式字串。
//                spotGetAllTask.get(500, TimeUnit.MILLISECONDS);
                String jsonIn = spotGetAllTask.execute().get();
                //利用TypeToken指定資料型態為List<Member>
                Type listType = new TypeToken<List<Member>>() {
                }.getType();
                //利用Gson把json字串轉成Type指定的型態(List<Member>)後放入memberList(List<Member>)。
                memberList = gson.fromJson(jsonIn, listType);

//            }catch(TimeoutException te){
//                Common.showToast(inputActivity, "伺服器無回應");
            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (memberList == null || memberList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
//                Common.showToast(inputActivity, "查無資料");
                Log.e("memberList","無資料");
            } else {
                //回傳memberList。
                return memberList;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return memberList;
    }

}
