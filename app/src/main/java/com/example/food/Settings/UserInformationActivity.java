package com.example.food.Settings;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.DAO.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.R;
import com.example.food.Settings.task.MemberImageTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay, newGender;
    private Member member;
    //    private Button btUserDataSetting;O
    private String newPassword, newNickName, newBirthday;
    private TextView tvUserNickname, tvUserBirthday;
    private MemberDAO memberDAO;
    private File file;
    private PopupWindow popWindow = new PopupWindow();
    private byte[] image;
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

        // 讀取  資料庫內的會員資料
        memberDAO = new MemberDAO(UserInformationActivity.this);
        ImageView imageView = findViewById(R.id.cvUserImage);

        member = memberDAO.getUserDate(testUserAccount, imageView);
        showMemberData(member);

        selectCardView();


        Button btUserDataSetting = findViewById(R.id.btUserDataSetting);
        //點擊確認鍵後，建立新的執行緒，並且將Button關閉，等到onPause()、onStart()時重新開啟。
        btUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("測試 updateMemberDate = ", "開始");
                Log.e("測試 newPassword = ", newPassword);
                Log.e("測試 newNickName = ", newNickName);
                Log.e("測試 newBirthday = ", newBirthday);
                String s = String.valueOf(newGender);
                Log.e("測試 newGender = ", s);

                memberDAO.updateMemberDate(testUserAccount, newPassword,
                        newNickName, newBirthday, newGender, image);

//                if (btUserDataSetting.isEnabled()) {
//                    mThread = new HandlerThread("aa");
//                    mThread.start();
//                    mThreadHandler = new Handler(mThread.getLooper());
//                    mThreadHandler.post(runnable);
//                }
//                btUserDataSetting.setEnabled(false);
            }
        });

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mThreadHandler != null) {
//            mThreadHandler.removeCallbacks(runnable);
//        }
//        if (mThread != null) {
//            mThread.quit();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        Common.askPermissions(UserInformationActivity.this, permissions, Common.PERMISSION_READ_EXTERNAL_STORAGE);
//        btUserDataSetting.setEnabled(true);
    }

    //執行緒
//    private Runnable runnable = new Runnable() {
////        public void run() {
////            //這裡放執行緒要執行的程式。
////            memberDAO.updateMemberDate(testUserAccount, newPassword,
////                    newNickName, newBirthday, newGender, image);
////        }
////    };

    private void findById() {
        tvUserBirthday = findViewById(R.id.tvUserBirthday);
        tvUserNickname = findViewById(R.id.tvUserNickname);
        cvUserImage = findViewById(R.id.cvUserImage);
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
                                        discernMemberGender(which);
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
        Toolbar settingsUserInformationToolbar = findViewById(R.id.settingsUserInformationToolbar);
        settingsUserInformationToolbar.setTitle(R.string.settingUserData);

        setSupportActionBar(settingsUserInformationToolbar);
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

    //判斷 是否大於十位數 若為個位數 補0
    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    private void showMemberData(Member member) {
        TextView tvUserId = findViewById(R.id.tvUserId);
        if (member != null) {
            tvUserId.setText(testUserAccount);

            discernMemberPassword(member.getUserPassword(), "");
            newNickName = member.getNickName();
            tvUserNickname.setText(newNickName);
            newBirthday = member.getBirthday();
            tvUserBirthday.setText(newBirthday);
            discernMemberGender(member.getGender());

            Bitmap bitmap = ((BitmapDrawable) cvUserImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            image = baos.toByteArray();
        } else {
            tvUserId.setText(testUserAccount);
            discernMemberPassword("1234567890", "");
            tvUserNickname.setText(newNickName);
            tvUserBirthday.setText(newBirthday);
            discernMemberGender(2);
        }
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
        newGender = gender;
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
        // 頭像有變更
        imageChange = true;
    }
}
