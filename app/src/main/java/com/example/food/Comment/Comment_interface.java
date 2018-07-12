package com.example.food.Comment;

/**
 * Created by PC-26 on 5/22/2018.
 */

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AppModel.CommentForApp;
import com.example.food.DAO.task.Common;
import com.example.food.Other.ImageInExternalStorage;
import com.example.food.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment_interface extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button2;
    private PopupWindow popWindow = new PopupWindow ();
    private File file;
    private SharedPreferences prefs;
    private ImageButton ibPickPicture;
    private ImageView ivComment;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri, croppedImageUri;
    private byte[] image;
    private Toolbar toolbar;
    private CommentForApp cfa;
    private TextView mediumText;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_interface);
        findViews();
        initContent();
        review();
        selectCardView();

        mediumText = findViewById(R.id.MediumText);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        mediumText.setText(prefs.getString("nickname", "訪客"));
        CircleImageView cv_comment_inf = findViewById(R.id.cv_comment_inf);

        ImageInExternalStorage imgExStorage = new ImageInExternalStorage(Comment_interface.this, prefs);
        imgExStorage.openFile(cv_comment_inf);
   }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        Common.askPermissions(Comment_interface.this, permissions, Common.PERMISSION_READ_EXTERNAL_STORAGE);
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
    private void initContent() {
//         toolbar = findViewById(R.id.comment_interface_toolbar);
//        toolbar.setTitle(R.string.text_insert_Comment);
//        toolbar.setTitle(getString(R.string.text_insert_Comment));
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void findViews() {
        ivComment=findViewById(R.id.ivComment);
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
                            startActivityForResult(intent, REQ_TAKE_PICTURE);
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
                                    Comment_interface.this.getContentResolver().openInputStream(croppedImageUri));
                            ivComment.setImageBitmap(picture);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            image = out.toByteArray();
                        } catch (FileNotFoundException e) {
                        }
                        break;
                }
            }
        }
    private void crop(Uri sourceImageUri) {
        File file = Comment_interface.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            Common.showToast(Comment_interface.this, "This device doesn't support the crop action!");
        }
    }

}
