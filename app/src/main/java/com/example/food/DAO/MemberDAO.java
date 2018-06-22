package com.example.food.DAO;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.food.AppModel.Member;
import com.example.food.DAO.task.CommonTask;
import com.example.food.DAO.task.MemberImageTask;
import com.example.food.Settings.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MemberDAO {
    private Activity inputActivity;
    private String TAG;

    public MemberDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
    }

    public boolean checkAccount(String userAccount) {
        boolean usable = true;
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "checkAccount");
            jsonObject.addProperty("UserAccount", userAccount);
            String jsonOut = jsonObject.toString();
            CommonTask checkAccountTask = new CommonTask(url, jsonOut);
            try {
                String result = checkAccountTask.execute().get();
                usable = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return usable;
    }

    public Member getUserDate(String userAccount) {
        Member member = null;
        if (Common.networkConnected(inputActivity)) {
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();

            //jsonObject新增屬性action其值為getUserDate
            jsonObject.addProperty("action", "getUserDate");
            //jsonObject新增屬性UserAccount其值為UserAccount
            jsonObject.addProperty("UserAccount", userAccount);

            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            CommonTask memberGetAllTask = new CommonTask(url, jsonOut);
            //CommonTask會將傳入的jsonOut字串送給伺服器

            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的getUserDate方法，傳入參數UserAccount
            try {
                //用字串儲存伺服器回應的json格式字串。
                String jsonIn = memberGetAllTask.execute().get();
                //利用TypeToken指定資料型態為Member
                Type listType = new TypeToken<Member>() {  }.getType();
                //利用Gson把json字串轉成Type指定的型態(Member)後放入member(Member)。
                member = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return member;
    }

    public void getPortrait(String userAccount, ImageView imageView) {
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";

            int imageSize = inputActivity.getResources().getDisplayMetrics().widthPixels / 4;
            MemberImageTask memberImageTask = new MemberImageTask(url, userAccount, imageSize, imageView, inputActivity);
            memberImageTask.execute();

        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
    }

    public Boolean updatePortrait(String userAccount, byte[] portrait) {
        Boolean updateSuccess = false;
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("update", "updatePortrait");

            jsonObject.addProperty("UserAccount", userAccount);
            String imageBase64 = Base64.encodeToString(portrait, Base64.DEFAULT);
            jsonObject.addProperty("updatePortrait", imageBase64);

            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count > 0) {
                updateSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return updateSuccess;
    }

    public boolean updateMemberDate(Member member) {
        Boolean updateSuccess = false;
        if (Common.networkConnected(inputActivity)) {
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為updateMemberDate
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("update", "updateMemberDate");

            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的updateMemberDate方法
            // 找 member table  傳入修改後的資料包  member
            jsonObject.addProperty("Member", new Gson().toJson(member));

            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count > 0) {
                updateSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return updateSuccess;
    }

    public boolean updatePreference(String userAccount, String preference) {
        Boolean updateSuccess = false;
        if (Common.networkConnected(inputActivity)) {

            String url = Common.URL + "/MemberServlet";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("update", "updatePreference");

            jsonObject.addProperty("UserAccount", userAccount);
            jsonObject.addProperty("Preference", preference);

            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count > 0) {
                updateSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return updateSuccess;
    }

    public boolean insertMemberDate(Member member, byte[] portrait) {
        Boolean insertSuccess = false;
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insertMemberDate");
            jsonObject.addProperty("Member", new Gson().toJson(member));
            //若會員有給頭像 傳給Server 若沒有 傳0
            String imageBase64 = "0";
            if (portrait != null) {
                imageBase64 = Base64.encodeToString(portrait, Base64.DEFAULT);
            }
            jsonObject.addProperty("insertPortrait", imageBase64);
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count > 0) {
                insertSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return insertSuccess;
    }

    public boolean userLogin(final String userAccount, final String userPassword) {
        boolean isUser = false;
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userLogin");
            jsonObject.addProperty("UserAccount", userAccount);
            jsonObject.addProperty("UserPassword", userPassword);
            String jsonOut = jsonObject.toString();
            CommonTask userLoginTask = new CommonTask(url, jsonOut);
            try {
                String result = userLoginTask.execute().get();
                isUser = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return isUser;
    }

}