package com.example.food.Other;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.food.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SaveImageInExStorage {
    private final static String TAG = "SaveImageInExStorage";
    private Activity activity;
    private SharedPreferences prefs;
    private File file;
    private String imageName;

    public SaveImageInExStorage(Activity activity, SharedPreferences prefs) {
        this.activity = activity;
        this.prefs = prefs;
        int memberId = prefs.getInt("memberId", 0);
        imageName = "/member_" + memberId + ".jpg";
        file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + imageName);
    }

    //如果外部儲存體掛載成功 回傳 true
    private boolean mediaMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public void saveImage(ImageView imageView) {
        if (!mediaMounted()) {
            showToast(R.string.msg_ExternalStorageNotFound);
            return;
        }
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void openFile(ImageView imageView) {
        if (!mediaMounted()) {
            showToast(R.string.msg_ExternalStorageNotFound);
            return;
        }
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            boolean isMember = prefs.getBoolean("login", false);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else if (isMember) {
                int gender = prefs.getInt("gender", 2);
                if (gender == 0) {
                    imageView.setImageResource(R.drawable.woman);
                } else if (gender == 1) {
                    imageView.setImageResource(R.drawable.man);
                }
            } else {
                imageView.setImageResource(R.drawable.logo);
            }
        }
    }

    private void showToast(int messageResId) {
        Toast.makeText(activity, messageResId, Toast.LENGTH_SHORT).show();
    }
}
