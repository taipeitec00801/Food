package com.example.food.Other;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.food.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageInExternalStorage {
    private final static String TAG = "SaveImageInExStorage";
    private SharedPreferences prefs;
    private Activity activity;
//    private File file;

    public ImageInExternalStorage(Activity activity, SharedPreferences prefs) {
        this.prefs = prefs;
        this.activity = activity;
    }

    private File getFile() {
        int memberId = prefs.getInt("memberId", 0);
        String imageName = "member_" + memberId + ".jpg";
        return new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageName);
    }

    //如果外部儲存體掛載成功 回傳 true
    private boolean mediaMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public void saveImage(Bitmap bitmap) {
        File file = getFile();
        if (!mediaMounted()) {
            showToast(R.string.msg_ExternalStorageNotFound);
            return;
        }
        if (file.exists()) {
            file.delete();
        }
        if (bitmap != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
        File file = getFile();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (file.exists() && bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //這裡需加判斷性別  並顯示不同預設圖
            int gender = prefs.getInt("gender", 2);
            if (gender == 0) {
                imageView.setImageResource(R.drawable.woman);
            } else if (gender == 1) {
                imageView.setImageResource(R.drawable.man);
            } else {
                imageView.setImageResource(R.drawable.logo);
            }
        }

    }

    public void deleteFile() {
        File file = getFile();
        if (file.exists()) {
            file.delete();
        }
    }

    private void showToast(int messageResId) {
        Toast.makeText(activity, messageResId, Toast.LENGTH_SHORT).show();
    }
}
