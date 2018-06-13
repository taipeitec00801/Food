package com.example.food.Settings;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.DAO.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.R;
import com.example.food.Settings.task.MemberImageTask;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay, newGender;
    private String newPassword, newNickName, newBirthday;
    private String userAccount;
    private TextView tvUserNickname, tvUserBirthday;
    private SpinKitView skvSetBy;
    private File file;
    private MemberDAO memberDAO;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private PopupWindow popWindow = new PopupWindow();
    private Button btUserDataSetting;
    private byte[] image;
    private SharedPreferences prefs;
    private boolean imageChange;
    private CircleImageView cvUserImage;
    private Uri contentUri, croppedImageUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    // 測試用 testUserAccount
    private static final String testUserAccount = "hikarumiyasaki@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        findById();
        initContent();

        // 暫時-->讀取資料庫內的會員照片
        memberDAO = new MemberDAO(UserInformationActivity.this);
        ImageView imageView = findViewById(R.id.cvUserImage);
        memberDAO.getPortrait(testUserAccount, imageView);
        showMemberData();

        selectCardView();

        //點擊確認鍵後，建立新的執行緒。
        btUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skvSetBy.setVisibility(View.VISIBLE);

//                btUserDataSetting.setEnabled(false);

//                Log.e("測試 updateMemberDate = ", "開始");
//                Log.e("測試 newPassword = ", newPassword);
//                Log.e("測試 newNickName = ", newNickName);
//                Log.e("測試 newBirthday = ", newBirthday);
//                String s = String.valueOf(newGender);
//                Log.e("測試 newGender = ", s);



//                if (btUserDataSetting.isEnabled()) {
//                    mThread = new HandlerThread("uis");
//                    mThread.start();
//                    mThreadHandler = new Handler(mThread.getLooper());
//                    mThreadHandler.post(runnable);
//                }
//                btUserDataSetting.setEnabled(false);
            }
        });

    }

    //這裡放執行緒要執行的程式。
    private Runnable runnable = new Runnable() {
        public void run() {
            //將會員資料 寫入偏好設定檔中
            prefs.edit().putString("userPassword", newPassword).apply();
            prefs.edit().putString("nickname", newNickName).apply();
            prefs.edit().putString("birthday", newBirthday).apply();
            prefs.edit().putInt("gender", newGender).apply();

//            boolean updateResult = memberDAO.updateMemberDate(userAccount, newPassword,
//                    newNickName, newBirthday, newGender);

            //用假帳號測試
            boolean updateResult = memberDAO.updateMemberDate(testUserAccount, newPassword,
                    newNickName, newBirthday, newGender);
            checkDialog(updateResult);

        }
    };

    private void checkDialog(boolean updateResult) {
        String result = "更新失敗";
        if (updateResult) {
            result = "更新成功";
        }
        new MaterialDialog.Builder(UserInformationActivity.this)
                .title(R.string.settingUserData)
                .content(result)
                .positiveText(R.string.textIKnow)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (mThreadHandler != null) {
                            mThreadHandler.removeCallbacks(runnable);
                        }
                        if (mThread != null) {
                            mThread.quit();
                        }
                        skvSetBy.setVisibility(View.INVISIBLE);
                    }
                }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        Common.askPermissions(UserInformationActivity.this, permissions, Common.PERMISSION_READ_EXTERNAL_STORAGE);
        btUserDataSetting.setEnabled(true);
    }

    private void findById() {
        btUserDataSetting = findViewById(R.id.btUserDataSetting);
        tvUserBirthday = findViewById(R.id.tvUserBirthday);
        tvUserNickname = findViewById(R.id.tvUserNickname);
        cvUserImage = findViewById(R.id.cvUserImage);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        skvSetBy = findViewById(R.id.userInfo_spinKit);
    }

    private void selectCardView() {
        /* 個人頭像 */
        cvUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mView = LayoutInflater.from(UserInformationActivity.this)
                        .inflate(R.layout.take_photo_pop, null, false);

                popWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);

                // 设置弹出窗体可点击
                popWindow.setFocusable(true);
                // 设置弹出窗体显示时的动画，从底部向上弹出
                popWindow.setAnimationStyle(R.style.take_photo_anim);

                // 设置按钮监听
                //照相
                Button btn_take_photo = mView.findViewById(R.id.btn_take_photo);
                btn_take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = UserInformationActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        file = new File(file, "picture.jpg");
                        contentUri = FileProvider.getUriForFile(
                                UserInformationActivity.this,
                                getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        if (isIntentAvailable(UserInformationActivity.this, intent)) {
                            startActivityForResult(intent, REQ_TAKE_PICTURE);
                        } else {
                            Toast.makeText(UserInformationActivity.this,
                                    R.string.msg_NoCameraAppsFound,
                                    Toast.LENGTH_SHORT).show();
                        }
                        popWindow.dismiss();
                    }
                });
                //相簿
                Button btn_pick_photo = mView.findViewById(R.id.btn_pick_photo);
                btn_pick_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQ_PICK_IMAGE);
                        popWindow.dismiss();
                    }
                });
                // 取消按钮
                Button btn_cancel = mView.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 销毁弹出框
                        popWindow.dismiss();
                    }
                });

                // 设置外部可点击
                popWindow.setOutsideTouchable(true);
                // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
                popWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                        // 这里如果返回true的话，touch事件将被拦截
                        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                    }
                });

                //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
                popWindow.showAsDropDown(view, -50, 0);

                // 设置弹出窗体的背景
                // 实例化一个ColorDrawable颜色为半透明
                popWindow.setBackgroundDrawable(new ColorDrawable(0xb0ffffff));
            }
        });

        /* 密碼 */
        CardView cvUserPassword = findViewById(R.id.cvUserPassword);
        cvUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.textPassword)
                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .inputRange(6, 12, 0)
                        .widgetColorRes(R.color.cardBackgroundStart)
                        .input(0, 0, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                //  密碼是否與資料庫內相同
                                if (discernMemberPassword(newPassword, input.toString())) {
                                    // 確認密碼 視窗
                                    new MaterialDialog.Builder(UserInformationActivity.this)
                                            .title(R.string.confirmPassword)
                                            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                            .widgetColorRes(R.color.cardBackgroundStart)
                                            .input(0, 0, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                    discernMemberPassword(newPassword, input.toString());
                                                }
                                            }).show();
                                } else {
                                    new MaterialDialog.Builder(UserInformationActivity.this)
                                            .title("")
                                            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                            .widgetColorRes(R.color.cardBackgroundStart)
                                            .input(0, 0, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                    discernMemberPassword(newPassword, input.toString());
                                                }
                                            }).show();
                                }
                            }
                        }).show();
            }
        });
        /* 暱稱 */
        CardView cvUserNickname = findViewById(R.id.cvUserNickname);
        cvUserNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.textNickname)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .widgetColorRes(R.color.cardBackgroundStart)
                        .input(0, 0, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                newNickName = input.toString();
                                tvUserNickname.setText(newNickName);
                            }
                        }).show();
            }
        });
        /* 生日 */
        CardView cvUserBirthday = findViewById(R.id.cvUserBirthday);
        cvUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
        /* 性別 */
        CardView cvUserGender = findViewById(R.id.cvUserGender);
        cvUserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.text_gender)
                        .items(R.array.genderArray)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                    case 1:
                                        newGender = which;
                                        discernMemberGender(newGender);
                                        break;
                                    default:
                                        dialog.cancel();
                                        break;
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.text_btYes)
                        .show();
            }
        });

    }

    private void initContent() {
        Toolbar toolbar = findViewById(R.id.settingsUserInformationToolbar);
        toolbar.setTitle(R.string.settingUserData);
        //是否有變更頭像
        imageChange = false;
        userAccount = prefs.getString("userAccount", "");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    //檢查裝置有沒有應用程式可以拍照  若有則 >0
    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /* 選擇日期的跳脫視窗 */
    public static class DatePickerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            UserInformationActivity uiActivity = (UserInformationActivity) getActivity();
            return new DatePickerDialog(uiActivity, uiActivity,
                    uiActivity.mYear, uiActivity.mMonth, uiActivity.mDay);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month + 1;
        mDay = day;
        newBirthday = mYear + "-" + pad(mMonth) + "-" + pad(mDay);
        tvUserBirthday.setText(newBirthday);
    }

    //判斷 是否大於十位數 若為個位數 補 0
    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    private void showMemberData() {
        TextView tvUserAccount = findViewById(R.id.tvUserAccount);
        tvUserAccount.setText(userAccount);

        discernMemberPassword("demo", "");

        newNickName = prefs.getString("nickname","");
        tvUserNickname.setText(newNickName);

        newBirthday = prefs.getString("birthday","");
        tvUserBirthday.setText(newBirthday);

        newGender = prefs.getInt("gender",2);
        discernMemberGender(newGender);

//        Bitmap bitmap = ((BitmapDrawable) cvUserImage.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        image = baos.toByteArray();

    }

    // 未完成
    // 判斷密碼 original與input相關關係
    private boolean discernMemberPassword(String original, String input) {
        TextView tvUserPassword = findViewById(R.id.tvUserPassword);
        if (input == null || input.length() == 0) {
            newPassword = original;
            return false;
        } else if (!original.equals(input)) {
            tvUserPassword.setText(input);
            newPassword = input;
            return true;
        } else {
            tvUserPassword.setText(original);
            newPassword = original;
            return false;
        }
    }

    // 分析 性別
    private void discernMemberGender(int gender) {
        TextView tvUserGender = findViewById(R.id.tvUserGender);
        if (gender == 0) {
            tvUserGender.setText(R.string.textFemale);
        } else if (gender == 1) {
            tvUserGender.setText(R.string.textMale);
        } else {
            tvUserGender.setText(R.string.textNotFind);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = intent.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(
                                UserInformationActivity.this.getContentResolver().openInputStream(croppedImageUri));
                        cvUserImage.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        e.getMessage();
                    } finally {
                        // 頭像有變更
                        imageChange = true;
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = UserInformationActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Common.showToast(UserInformationActivity.this, "This device doesn't support the crop action!");
        }
    }
}
