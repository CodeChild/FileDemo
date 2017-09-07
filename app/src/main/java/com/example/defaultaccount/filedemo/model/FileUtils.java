package com.example.defaultaccount.filedemo.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by DefaultAccount on 2017/8/29.
 */

public class FileUtils {
    public static String DIR_NAME="texts";
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e("FileError", "Directory not created");
        }
        return file;
    }
    public static File createFile(String pathName,String fileName) throws IOException {
        File file=new File(pathName+fileName);
        if(file.exists())
           Log.e("FileError","文件已存在");
        file.createNewFile();
        return file;
    }
}
