package com.example.food.Settings;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DataClearManager {
    //清除内部缓存(/data/data/com.example.food/cache)
    public static void clearInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    //清除外部cache下的内容(/sdcard/android/data/com.example.food/cache)
    public static void clearExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    // 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    //格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " TB";
    }

    public static String getCacheSize(File file) {
        return getFormatSize(getFolderSize(file));
    }

    // 获取文件
    // Context.getExternalFilesDir() --> SDCard/Android/data/com.example.food/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/com.example.food/cache/目录，一般存放临时缓存数据
    private static long getFolderSize(File file) {
        long size = 0;
        File[] fileLists = file.listFiles();
        try {
            if (fileLists.length > 0) {
                for (File fileList : fileLists) {
                    // 如果下面还有文件
                    if (fileList.isDirectory()) {
                        size += getFolderSize(fileList);
                    } else {
                        size += fileList.length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;

    }
}
