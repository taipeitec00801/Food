package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.Manifest;
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
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.food.R;
import com.example.food.Settings.*;
import com.example.food.Settings.Common;

import java.io.File;
import java.util.List;

public class Comment_interface extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button2;
    private ImageView imageView;
    private static final int REQUEST_PICK_PICTURE = 2;
    private PopupWindow popWindow = new PopupWindow ();
    private File file;
    private ImageButton ibPickPicture;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_interface);
        review();
        findViews();
        selectCardView();
    }

    private void review() {
        button2=findViewById(R.id.Collection);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Comment_interface.this,CommentActivity.class);
                startActivity(intent);
            }
        });
    }
    private void findViews() {
        ibPickPicture = findViewById(R.id.ibPickPicture);
    }
    private void selectCardView() {
        /* 個人頭像 */
        ibPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View mView = LayoutInflater.from(Comment_interface.this)
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
                        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        file = new File(file, "picture.jpg");
                        Uri contentUri = FileProvider.getUriForFile(
                                Comment_interface.this, getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        if (isIntentAvailable(Comment_interface.this, intent)) {
                            setResult(12);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(Comment_interface.this,
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

            public boolean isIntentAvailable(Context context, Intent intent) {
                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }


        });
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
                        ibPickPicture.setImageBitmap(downsizedPicture);
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
                            ibPickPicture.setImageBitmap(downsizedImage);
                        }
                        break;
                }
            }
        }
    }
