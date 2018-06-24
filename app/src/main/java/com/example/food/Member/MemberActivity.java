package com.example.food.Member;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.food.AppModel.Member;
import com.example.food.DAO.MemberDAO;
import com.example.food.Other.InputFormat;
import com.example.food.R;
import com.example.food.Settings.Common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private EditText etUser, etPassword, cfPassword, etBirthday, etGender;
    private Button btNext;
    private int mYear, mMonth, mDay;
    private Spinner spGender;
    private MemberDAO memberDAO;
    private String gender;
    private boolean inputAccount, inputPassword, inputConfirm;
    private CircleImageView cvRegisteredImage;
    private File file;
    private byte[] regImage;
    private PopupWindow popWindow = new PopupWindow();
    private InputFormat inputFormat;
    private String errorMessage;
    private Uri contentUri, croppedImageUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        findViews();
        showSpinner();
        //初始化
        initContent();
        // 偵測 現在在哪個 EditText 並 檢查輸入資訊
        editTextFocusEvent();
        //點擊事件
        clickEvent();
    }

    private void initContent() {
        inputAccount = false;
        inputPassword = false;
        inputConfirm = false;
        errorMessage = "請確認輸入資料";
        //頭像陣列初始化
        regImage = null;
    }

    private void editTextFocusEvent() {
        inputFormat = new InputFormat();
        //帳號
        etUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    inputFormat.inputFilter(etUser, 40);
                } else {
                    //確認輸入的帳號格式與是否已註冊
                    inputAccount = inputFormat.isValidAccount(etUser) &&
                            inputFormat.isInputNotNull(etUser) &&
                            unUsableAccount(etUser);
                    if (!inputAccount && !errorMessage.isEmpty()) {
                        Toast.makeText(MemberActivity.this,  errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //密碼
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    inputFormat.inputFilter(etPassword, 12);
                } else {
                    //確認輸入的密碼格式
                    inputPassword = inputFormat.isValidPassword(etPassword) &&
                            inputFormat.passwordLength(etPassword);
                    if (!inputPassword && !errorMessage.isEmpty()) {
                        Toast.makeText(MemberActivity.this,  errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //確認密碼
        cfPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    inputFormat.inputFilter(cfPassword, 12);
                } else {
                    //確認輸入的密碼格式 與 2組密碼是否相同
                    inputConfirm = inputFormat.isValidPassword(cfPassword) &&
                            inputFormat.passwordLength(cfPassword) &&
                            inputPasswordCheck(etPassword, cfPassword);
                    if (!inputConfirm && !errorMessage.isEmpty()) {
                        Toast.makeText(MemberActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //生日
        // 當Focus在Birthday & Gender的EditText時 不顯示虛擬鍵盤
        etBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    etBirthday.setInputType(InputType.TYPE_NULL); // 關閉虛擬鍵盤
                    DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    datePickerFragment.show(fm, "datePicker");
                }
            }
        });
        //性別
        etGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    etGender.setInputType(InputType.TYPE_NULL); // 關閉虛擬鍵盤
                }
            }
        });
    }

    private boolean inputDataCheck() {
        boolean inputGenderOk = inputGenderCheck();
        boolean inputBirthdayOk = inputFormat.isInputNotNull(etBirthday);
        return inputBirthdayOk && inputGenderOk && inputConfirm && inputPassword && inputAccount;
    }

    private boolean inputGenderCheck() {
        boolean inputOk = false;
        if (gender.equals(getResources().getString(R.string.textMale)) ||
                gender.equals(getResources().getString(R.string.textFemale))) {
            errorMessage = "";
            inputOk = true;
        } else {
            errorMessage = "請填入性別";
        }
        return inputOk;
    }

    private boolean inputPasswordCheck(EditText password, EditText pwConfirm) {
        boolean inputOk = false;
        String pswd = password.getText().toString().trim();
        String cfpswd = pwConfirm.getText().toString().trim();
        if (cfpswd.equals(pswd)) {
            pwConfirm.setError(null);
            errorMessage = "";
            inputOk = true;
        } else {
            pwConfirm.setError("密碼不一致");
            errorMessage = "密碼不一致";
        }
        return inputOk;
    }

    //傳送帳號到 server 帳號是否重複
    public boolean unUsableAccount(EditText editText) {
        boolean inputOk = false;
        memberDAO = new MemberDAO(MemberActivity.this);
        String inputAccount = etUser.getText().toString().trim();
        boolean accountExisted = memberDAO.checkAccount(inputAccount);

        if (accountExisted) {
            //帳號已存在
            editText.setError("帳號已存在");
            errorMessage = "帳號已存在";
        } else {
            //帳號尚未註冊
            editText.setError(null);
            errorMessage = "";
            inputOk = true;
        }
        return inputOk;
    }

    private void clickEvent() {
        //點擊下一頁
        btNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputDataCheck()) {
                    String userAccount = etUser.getText().toString().trim();
                    String userPassword = etPassword.getText().toString().trim();
                    String birthday = etBirthday.getText().toString().trim();
                    int mos = userAccount.indexOf("@");
                    String nickName = userAccount.substring(0, mos);
                    //判斷性別 並存入性別編號
                    int genderNumber = -1;
                    if (gender.equals(getResources().getString(R.string.textFemale))) {
                        genderNumber = 0;
                    } else if (gender.equals(getResources().getString(R.string.textMale))) {
                        genderNumber = 1;
                    }
                    //將資料封裝 用 Bundle 傳到下一頁
                    Member member = new Member(userAccount, userPassword, nickName,
                            birthday, genderNumber, regImage);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("MemberDate", member);

                    Intent intent = new Intent();
                    intent.setClass(MemberActivity.this, Member2Activity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    if (!errorMessage.isEmpty()) {
                        Toast.makeText(MemberActivity.this,  errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //點擊圖片
        cvRegisteredImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mView = LayoutInflater.from(MemberActivity.this)
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
                        file = MemberActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        file = new File(file, "picture.jpg");
                        contentUri = FileProvider.getUriForFile(
                                MemberActivity.this,
                                getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        if (isIntentAvailable(MemberActivity.this, intent)) {
                            startActivityForResult(intent, REQ_TAKE_PICTURE);
                        } else {
                            Toast.makeText(MemberActivity.this,
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

        //birthDay
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
    }

    private void findViews() {
        cvRegisteredImage = findViewById(R.id.cvRegisteredImage);
        etUser = findViewById(R.id.et_reg_User);
        etPassword = findViewById(R.id.et_reg_Password);
        cfPassword = findViewById(R.id.cf_reg_Password);
        etBirthday = findViewById(R.id.et_reg_Birthday);
        btNext = findViewById(R.id.bt_reg_Next);
        etGender = findViewById(R.id.et_reg_Gender);
        spGender = findViewById(R.id.spGender);
    }

    //檢查裝置有沒有應用程式可以拍照  若有則 >0
    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void showSpinner() {
        String[] places = {"Gender", "男性", "女性"};
        SpinnerAdapter adapterPlace = new SpinnerAdapter(MemberActivity.this, places);
        adapterPlace
                .setDropDownViewResource(android.R.layout.simple_gallery_item);
        spGender.setAdapter(adapterPlace);

        spGender.setOnItemSelectedListener(listener);
    }

    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {
            etGender.setText(" ");
            gender = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            etGender.setText(R.string.text_gender);
        }//被呼叫時代表下拉式選單有問題
    };

    //birthDay
    //----------------------------------------------------------------------------
    private void updateDisplay() {
        etBirthday.setText(new StringBuilder().append(mYear).append("-")
                .append(pad(mMonth + 1)).append("-").append(pad(mDay)));
    }

    private String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDisplay();
    }

    public static class DatePickerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MemberActivity memberActivity = (MemberActivity) getActivity();
            return new DatePickerDialog(
                    memberActivity, memberActivity, memberActivity.mYear, memberActivity.mMonth,
                    memberActivity.mDay);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(MemberActivity.this, LoginActivity.class);
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
                                MemberActivity.this.getContentResolver().openInputStream(croppedImageUri));
                        cvRegisteredImage.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        regImage = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        e.getMessage();
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = MemberActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            Common.showToast(MemberActivity.this, "This device doesn't support the crop action!");
        }
    }
}
