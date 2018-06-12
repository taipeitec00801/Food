package com.example.food.DAO;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.food.Settings.Common;
import com.example.food.Settings.task.CommonTask;
import com.example.food.Settings.task.MemberImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MemberDAO {
    private Activity inputActivity;
    private String TAG;
    private MemberImageTask memberImageTask;
    private int imageSize;

    public MemberDAO(Activity inputActivity) {
        this.inputActivity = inputActivity;
        TAG = inputActivity.getClass().getName();
        imageSize = inputActivity.getResources().getDisplayMetrics().widthPixels / 2;
    }


    public boolean checkAccount(String userAccount) {
        boolean usable = false;
        if (Common.networkConnected(inputActivity)) {
            String url = Common.URL + "/MemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "checkAccount");
            jsonObject.addProperty("UserAccount", userAccount);
            String jsonOut = jsonObject.toString();
            CommonTask userLoginTask = new CommonTask(url, jsonOut);
            try {
                String result = userLoginTask.execute().get();
                usable = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                usable = false;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return usable;
    }

    public Member getUserDate(String userAccount, ImageView imageView) {
        Member member = null;
        if (Common.networkConnected(inputActivity)) {
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //透過IP和資料庫名稱找到資料庫。

            String url = Common.URL + "/MemberServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();

            //jsonObject新增屬性action其值為findMemberByUserId
            //jsonObject新增屬性userId其值為UserAccount

            jsonObject.addProperty("action", "getUserDate");
            jsonObject.addProperty("UserAccount", userAccount);

            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            CommonTask memberGetAllTask = new CommonTask(url, jsonOut);
            //CommonTask會將傳入的jsonOut字串送給伺服器

            memberImageTask = new MemberImageTask(url, userAccount, imageSize, imageView);
            memberImageTask.execute();

            //ImageTask會將傳入的userAccount字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的getUserDate方法，傳入參數UserAccount，
            // 回傳的圖片會套用到指定的 imageView

            try {
                //用字串儲存伺服器回應的json格式字串。
//                spotGetAllTask.get(500, TimeUnit.MILLISECONDS);
                String jsonIn = memberGetAllTask.execute().get();
                //利用TypeToken指定資料型態為Member
                Type listType = new TypeToken<Member>() {
                }.getType();

                //利用Gson把json字串轉成Type指定的型態(Member)後放入member(Member)。
                member = gson.fromJson(jsonIn, listType);
//            }catch(TimeoutException te){
//                Common.showToast(inputActivity, "伺服器無回應");
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (member == null) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
//                Common.showToast(inputActivity, "查無資料");
            } else {
                //回傳memberList。
                return member;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return member;
    }

    public Boolean updatePortrait(String userAccount, byte[] portrait) {
        Boolean updateSuccess = false;
        if (Common.networkConnected(inputActivity)) {
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";

            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為updateMemberDate
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
            if (count == 0) {
                updateSuccess = false;
            } else {
                updateSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return updateSuccess;
    }

    public void updateMemberDate(String userAccount, String userPassword, String nickName,
                                 String birthday, int gender) {
        Boolean updateSuccess = false;
        if (Common.networkConnected(inputActivity)) {
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";
            Member newMember = new Member(userAccount, userPassword,
                    nickName, birthday, gender);

            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為updateMemberDate
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("update", "updateMemberDate");

            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的updateMemberDate方法
            // 找 member table  傳入修改後的資料newMember
            jsonObject.addProperty("Member", new Gson().toJson(newMember));

            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(inputActivity, "Update fail");
            } else {
                Common.showToast(inputActivity, "Update successfully");
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
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
            if (count == 0) {
                updateSuccess = false;
            } else {
                updateSuccess = true;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return updateSuccess;
    }

    public void insertMemberDate(String userAccount, String password, String nickName, String birthday,
                                 int gender, byte[] Portrait, String preference) {
        if (Common.networkConnected(inputActivity)) {
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/MemberServlet";

            //預設暱稱為 帳號 "@" 前的字串
            if (nickName == null || nickName.length() == 0) {
                nickName = userAccount.substring(0, userAccount.indexOf("@"));
            }
            Member newMember = new Member(userAccount, password, nickName, birthday,
                    gender, preference);

            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為updateMemberDate
            jsonObject.addProperty("action", "updateMemberDate");

            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的updateMemberDate方法
            // 找 member table  傳入修改後的資料newMember
            jsonObject.addProperty("Member", new Gson().toJson(newMember));
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(inputActivity, "Update fail");
            } else {
                Common.showToast(inputActivity, "Update successfully");
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
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
                isUser = false;
            }
        } else {
            Common.showToast(inputActivity, "no network connection available");
        }
        return isUser;
    }

}