package com.scu.timetable.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtil {

    private FileUtil() {

    }

    public static String readAssetFile(Context context, String str) throws IOException {
        StringBuilder str2 = new StringBuilder();
        try (InputStream inputStream = context.getAssets().open(str)) {
            byte[] bArr = new byte[8092];
            while (true) {
                int read = inputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                str2.append(new String(bArr, 0, read));
            }
            return str2.toString();
        }
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static void writeToJson(Context context, String fileName, String content) throws Exception {
        FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        outStream.write(content.getBytes());
        outStream.close();
    }

    public static String readFromJson(Context context, String fileName) throws FileNotFoundException {
        FileInputStream inStream = null;
        inStream = context.openFileInput(fileName);
        //输出到内存
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] contentByte = outStream.toByteArray();
            return new String(contentByte);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasJsonFile(Context context, String fileName) {
        File filesDir = context.getFilesDir();
        if (filesDir.exists()) {
            for (File file : filesDir.listFiles()) {
                if (file.getName().equals(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
