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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.food.R;

import java.io.File;
import java.util.List;

public class Comment_interface extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private Button button2;
    private ImageView imageView;
    private static final int REQUEST_PICK_PICTURE = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_interface);
        review();
        findViews();
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
        imageView = (ImageView) findViewById(R.id.imageView);
    }
    @Override
    protected void onStart() {
            super.onStart();
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            Common.askPermissions(this, permissions, Common.PERMISSION_READ_EXTERNAL_STORAGE);
        }

    public void onPickPictureClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {

                case REQUEST_PICK_PICTURE:
                    Uri uri = intent.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                        Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                        imageView.setImageBitmap(downsizedImage);
                    }
                    break;
            }
        }
    }
}