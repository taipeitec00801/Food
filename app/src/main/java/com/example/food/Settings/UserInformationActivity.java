package com.example.food.Settings;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private int mYear, mMonth, mDay, newGender;
    private Member member;
    private String newPassword, newNickName, newBirthday;
    private TextView tvUserNickname, tvUserBirthday;
    private MemberDAO memberDAO;
    private File file;
    private PopupWindow popWindow = new PopupWindow ();
    private CircleImageView cvUserImage;

    // 測試用 testUserAccount
    private static final String testUserAccount = "taipeitec00801@gmail.com";

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
        btUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberDAO.updateMemberDate(testUserAccount, newPassword, newNickName,
                        newBirthday, newGender);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        Common.askPermissions(UserInformationActivity.this, permissions, Common.PERMISSION_READ_EXTERNAL_STORAGE);
    }

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
                Button btn_take_photo =  mView.findViewById(R.id.btn_take_photo);
                btn_take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        file = new File(file, "picture.jpg");
                        Uri contentUri = FileProvider.getUriForFile(
                                UserInformationActivity.this, getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        if (isIntentAvailable(UserInformationActivity.this, intent)) {
                            setResult(10);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(UserInformationActivity.this,
                                    R.string.msg_NoCameraAppsFound,
                                    Toast.LENGTH_SHORT).show();
                        }
                        popWindow.dismiss();
                    }
                });
                //相簿
                Button btn_pick_photo =  mView.findViewById(R.id.btn_pick_photo);
                btn_pick_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        setResult(10);
                        startActivityForResult(intent, 2);
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
                        .inputRange(6,12,0)
                        .widgetColorRes(R.color.colorBody)
                        .input(0, 0,  new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                //  密碼是否與資料庫內相同
                                if (discernMemberPassword(newPassword,input.toString())) {
                                    // 確認密碼 視窗
                                    new MaterialDialog.Builder(UserInformationActivity.this)
                                            .title(R.string.confirmPassword)
                                            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                            .widgetColorRes(R.color.colorBody)
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
                                            .widgetColorRes(R.color.colorBody)
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
                        .widgetColorRes(R.color.colorBody)
                        .input(0, 0,  new MaterialDialog.InputCallback() {
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
        mMonth = month+1;
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
        if (member != null) {
            TextView tvUserId = findViewById(R.id.tvUserId);
            tvUserId.setText(testUserAccount);

            discernMemberPassword(member.getUserPassword(),"");
            newNickName = member.getNickName();
            tvUserNickname.setText(newNickName);
            newBirthday = member.getBirthday();
            tvUserBirthday.setText(newBirthday);
            discernMemberGender(member.getGender());
        }
    }

    // 未完成
    // 判斷密碼 original與input相關關係
    private boolean discernMemberPassword(String original, String input) {
        Pattern pattern = Pattern.compile("[ \\t\\n\\x0B\\f\\r]");
        TextView tvUserPassword = findViewById(R.id.tvUserPassword);
        if (input != null || input.length() !=0) {
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
        } else {
            tvUserGender.setText(R.string.textMale);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 10) {
            int newSize = 512;
            switch (requestCode) {
                case 1:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    Bitmap downsizedPicture = Common.downSize(srcPicture, newSize);
                    cvUserImage.setImageBitmap(downsizedPicture);
                    break;

                case 2:
                    Uri uri = intent.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                        Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                        cvUserImage.setImageBitmap(downsizedImage);
                    }
                    break;
            }
        }
    }
}
