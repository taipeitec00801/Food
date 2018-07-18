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
import android.content.res.ColorStateList;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.food.AppModel.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.DAO.task.Common;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.Other.InputFormat;
import com.example.food.R;
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
    private TextView tvUserNickname, tvUserBirthday, tvUserPassword;
    private EditText etSetPassword, cfSetPassword;
    private InputFormat inputFormat;
    private SpinKitView skvSetBy;
    private File file;
    private ImageInExternalStorage imgExStorage;
    private PopupWindow popWindow = new PopupWindow();
    private Button btUserDataSetting;
    private byte[] image;
    private SharedPreferences prefs;
    private boolean inputOK;
    private boolean imageChange, dateChange, updateResult, updateImageResult;
    private CircleImageView cvUserImage;
    private Uri contentUri, croppedImageUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        findById();
        initContent();

        showMemberData();
        clickCardView();

        //點擊確認鍵後，建立新的執行緒。
        btUserDataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skvSetBy.setVisibility(View.VISIBLE);
                if (btUserDataSetting.isEnabled()) {
                    btUserDataSetting.setEnabled(false);
                    Thread mThread = new Thread(runnable);
                    mThread.start();
                    try {
                        mThread.join();
                    } catch (InterruptedException e) {
                        System.out.println("執行緒被中斷");
                    }
                }
                checkDialog(updateResult, updateImageResult);
            }
        });

    }

    //這裡放執行緒要執行的程式。
    private Runnable runnable = new Runnable() {
        public void run() {
            MemberDAO memberDAO = new MemberDAO(UserInformationActivity.this);
            Member newMember = new Member(userAccount, newPassword,
                    newNickName, newBirthday, newGender);
            updateResult = false;
            if (dateChange) {
                updateResult = memberDAO.updateMemberDate(newMember);
                //變更資料 改回
                dateChange = false;
            }

            updateImageResult = false;
            // 若有改變頭像 執行 updatePortrait
            if (imageChange) {
                updateImageResult = memberDAO.updatePortrait(userAccount, image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imgExStorage.saveImage(bitmap);
                // 變更頭像 改回
                imageChange = false;
            }
            if (updateResult) {
                //將會員資料 寫入偏好設定檔中
                prefs.edit().putString("userPassword", newPassword).apply();
                prefs.edit().putString("nickname", newNickName).apply();
                prefs.edit().putString("birthday", newBirthday).apply();
                prefs.edit().putInt("gender", newGender).apply();
            }
        }
    };

    //更新結果 提示視窗
    private void checkDialog(boolean updateResult, boolean updateImageResult) {
        String result = "更新 個人資料 失敗";
        String imageResult = " ";
        if (updateResult) {
            if (updateImageResult) {
                imageResult = " 與 大頭照 ";
            }
            result = "更新 個人資料" + imageResult + "成功";
        } else if (updateImageResult) {
            result = "更新 大頭照 成功";
        }
        new MaterialDialog.Builder(UserInformationActivity.this)
                .title(R.string.settingUserData)
                .backgroundColorRes(R.color.colorDialogBackground)
                .positiveColorRes(R.color.colorText)
                .content(result)
                .positiveText(R.string.textIKnow)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        btUserDataSetting.setEnabled(true);
                        skvSetBy.setVisibility(View.INVISIBLE);
                        recreate();
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
        tvUserPassword = findViewById(R.id.tvUserPassword);
        tvUserBirthday = findViewById(R.id.tvUserBirthday);
        tvUserNickname = findViewById(R.id.tvUserNickname);
        cvUserImage = findViewById(R.id.cvUserImage);
        skvSetBy = findViewById(R.id.userInfo_spinKit);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
    }

    private void clickCardView() {
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
                // 取消
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
                popWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
            }
        });

        /* 密碼 */
        CardView cvUserPassword = findViewById(R.id.cvUserPassword);
        cvUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .backgroundColorRes(R.color.colorDialogBackground)
                        .positiveColorRes(R.color.colorText)
                        .neutralColorRes(R.color.colorText)
                        .neutralText(R.string.text_btCancel)
                        .customView(R.layout.reset_password, true)
                        .positiveText(R.string.text_btYes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                etSetPassword = dialog.getView().findViewById(R.id.et_set_Password);
                                cfSetPassword = dialog.getView().findViewById(R.id.cf_set_Password);

                                //限制密碼輸入長度
                                inputFormat.inputFilter(etSetPassword, 12);
                                //限制密碼輸入長度
                                inputFormat.inputFilter(cfSetPassword, 12);
                                inputOK = discernMemberPassword(etSetPassword, cfSetPassword);

                                if (inputOK) {
                                    newPassword = cfSetPassword.getText().toString().trim();
                                    tvUserPassword.setText(newPassword);
                                    //資料有變更
                                    dateChange = true;
                                } else {
                                    Common.showToast(UserInformationActivity.this, "請確認輸入的密碼");
                                }
                                dialog.dismiss();
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
                        .backgroundColorRes(R.color.colorDialogBackground)
                        .positiveColorRes(R.color.colorText)
                        .neutralColorRes(R.color.colorText)
                        .neutralText(R.string.text_btCancel)
                        .input(0, 0, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                newNickName = input.toString();
                                tvUserNickname.setText(newNickName);
                                //資料有變更
                                dateChange = true;
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
        final ColorStateList csl = UserInformationActivity.this
                .getResources()
                .getColorStateList(R.color.colorRadioButtons, null);
        cvUserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserInformationActivity.this)
                        .title(R.string.text_gender)
                        .items(R.array.genderArray)
                        .choiceWidgetColor(csl)
                        .backgroundColorRes(R.color.colorDialogBackground)
                        .positiveColorRes(R.color.colorText)
                        .neutralColorRes(R.color.colorText)
                        .itemsColorRes(R.color.colorText)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                    case 1:
                                        newGender = which;
                                        discernMemberGender(newGender);
                                        //資料有變更
                                        dateChange = true;
                                        break;
                                    default:
                                        dialog.cancel();
                                        break;
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.text_btYes)
                        .neutralText(R.string.text_btCancel)
                        .show();
            }
        });

    }

    private void initContent() {
        Toolbar toolbar = findViewById(R.id.settingsUserInformationToolbar);
        toolbar.setTitle(R.string.settingUserData);
        //是否有變更頭像
        imageChange = false;
        dateChange = false;
        inputOK = false;
        inputFormat = new InputFormat();
        userAccount = prefs.getString("userAccount", "");
        imgExStorage = new ImageInExternalStorage(UserInformationActivity.this, prefs);
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

        newPassword = prefs.getString("userPassword", "");
        tvUserPassword.setText(newPassword);

        newNickName = prefs.getString("nickname", "");
        tvUserNickname.setText(newNickName);

        newBirthday = prefs.getString("birthday", "");
        tvUserBirthday.setText(newBirthday);

        newGender = prefs.getInt("gender", 2);
        discernMemberGender(newGender);

        imgExStorage.openFile(cvUserImage);
    }

    private boolean discernMemberPassword(EditText password, EditText confirmPassword) {
        //密碼
        //確認輸入的密碼格式
        boolean inputPassword = inputFormat.isValidPassword(password) &&
                inputFormat.passwordLength(password);

        //確認密碼
        //確認輸入的密碼格式 與 2組密碼是否相同
        boolean inputConfirm = inputFormat.isValidPassword(confirmPassword) &&
                inputFormat.passwordLength(confirmPassword) &&
                inputPasswordCheck(confirmPassword, confirmPassword);

        return inputConfirm && inputPassword;
    }

    private boolean inputPasswordCheck(EditText password, EditText pwConfirm) {
        boolean inputOk = false;
        String pswd = password.getText().toString().trim();
        String cfpswd = pwConfirm.getText().toString().trim();
        if (cfpswd.equals(pswd)) {
            pwConfirm.setError(null);
            inputOk = true;
        } else {
            pwConfirm.setError("密碼不一致");
            Common.showToast(UserInformationActivity.this, "密碼不一致");
        }
        return inputOk;
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(UserInformationActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
                        if (image != null) {
                            imageChange = true;
                        }
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
