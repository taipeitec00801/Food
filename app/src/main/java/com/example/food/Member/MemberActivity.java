package com.example.food.Member;

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

import com.example.food.Other.InputFormat;
import com.example.food.R;
import com.example.food.Settings.Common;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    private EditText etUser, etPassword, cfPassword, etBirthday, etGender;
    private Button btNext;
    private int mYear, mMonth, mDay;
    private Spinner spGender;
    private CircleImageView cvRegisteredImage;
    private File file;
    private PopupWindow popWindow = new PopupWindow ();
    private InputFormat inputFormat = new InputFormat();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        findViews();
        showSpinner();
//        getDropDownView();

        //點擊事件
        clickEvent();

    }

    private void clickEvent() {
        //點擊下一頁
        btNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (inputDataCheck()) {
                    //將輸入資料 傳到下一頁
                    intent.setClass(MemberActivity.this, Member2Activity.class);
                    bundle.putString("UserAccount",etUser.getText().toString().trim());
                    bundle.putString("UserPassword",etPassword.getText().toString().trim());
                    bundle.putString("Birthday",etBirthday.getText().toString().trim());
                    bundle.putString("Gender",etGender.getText().toString().trim());
//                    bundle.putByte("Portrait",????);  //圖片 Byte[]
                    intent.putExtras(bundle);
                    startActivity(intent);
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
                Button btn_take_photo =  mView.findViewById(R.id.btn_take_photo);
                btn_take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        file = new File(file, "picture.jpg");
                        Uri contentUri = FileProvider.getUriForFile(
                                MemberActivity.this, getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        if (isIntentAvailable(MemberActivity.this, intent)) {
                            setResult(11);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(MemberActivity.this,
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
                        setResult(11);
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

    }

    private void findViews() {
        cvRegisteredImage = findViewById(R.id.cvRegisteredImage);
        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        cfPassword = findViewById(R.id.cfPassword);
        etBirthday = findViewById(R.id.etBirthday);
        btNext = findViewById(R.id.btNext);
        etGender = findViewById(R.id.etGender);
        spGender= findViewById(R.id.spGender);
        inputFormat.inputFilter(etPassword,12);
        inputFormat.inputFilter(cfPassword,12);
        inputFormat.inputFilter(etUser,40);
    }

    //檢查裝置有沒有應用程式可以拍照  若有則 >0
    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void showSpinner() {
        String[] places = {"Gender", "男姓", "女姓"};
        SpinnerAdapter adapterPlace = new SpinnerAdapter(MemberActivity.this,places);
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
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            etGender.setText(R.string.text_gender);
        }//被呼叫時代表下拉式選單有問題
    };

    private boolean inputDataCheck() {
        boolean inputPassword = false;
        boolean inputNotNull = false;
        //確認輸入的帳號格式與重複性
        boolean inputAccount = (inputFormat.isValidAccount(etUser) &
                inputFormat.unUsableAccount(MemberActivity.this, etUser));

        if (inputAccount) {
            //確認輸入的密碼
            inputPassword = inputFormat.isValidPassword(etPassword) &
                    inputFormat.isValidPassword(cfPassword);
        }
        if (inputPassword && etPassword.equals(cfPassword)) {
            //確認輸入的生日與性別非空值
            inputNotNull = inputFormat.isiInputNotNull(etBirthday) &
                    inputFormat.isiInputNotNull(etGender);
        }
        return inputNotNull;
    }

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
    public void onDateClick(View view) {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }
    //----------------------------------------------------------------------------
    //birthDay

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 11) {
            int newSize = 512;
            switch (requestCode) {
                case 1:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    Bitmap downsizedPicture = Common.downSize(srcPicture, newSize);
                    cvRegisteredImage.setImageBitmap(downsizedPicture);
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
                        cvRegisteredImage.setImageBitmap(downsizedImage);
                    }
                    break;
            }
        }
    }
}
