package com.zhuangfei.timetable.utils;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Liu ZhuangFei on 2018/7/25.
 */

public class ColorUtils {

    /**
     * 合成指定颜色、指定不透明度的颜色，
     * 0:完全透明，1：不透明
     *
     * @param color
     * @param alpha 0:完全透明，1：不透明
     * @return
     */
    public static int alphaColor(int color, float alpha) {
        Log.d("alphaColor", "alpha=" + alpha);
        if (alpha == 1.0f) {
            alpha = 0.8f;
        }
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

    public static int lightColor(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 + 0.2));
        green = (int) Math.floor(green * (1 + 0.2));
        blue = (int) Math.floor(blue * (1 + 0.2));
        Log.e("testcolor", red + "" + green + "" + blue);
        return Color.rgb(red, green, blue);
    }

    public static int lightColor(int RGBValues, float lightRatio) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 + lightRatio));
        green = (int) Math.floor(green * (1 + lightRatio));
        blue = (int) Math.floor(blue * (1 + lightRatio));
        Log.e("testcolor", red + "" + green + "" + blue);
        return Color.rgb(red, green, blue);
    }

    //颜色变浅，可调度数：0~255
    public static int translateLight(String color, int lightValue) {
        int startAlpha = Integer.parseInt(color.substring(0, 2), 16);
        int startRed = Integer.parseInt(color.substring(2, 4), 16);
        int startGreen = Integer.parseInt(color.substring(4, 6), 16);
        int startBlue = Integer.parseInt(color.substring(6), 16);

        startRed += lightValue;
        startGreen += lightValue;
        startBlue += lightValue;

        if (startRed > 255) startRed = 255;
        if (startGreen > 255) startGreen = 255;
        if (startBlue > 255) startBlue = 255;

        return Color.argb(startAlpha, startRed, startGreen, startBlue);
    }

    //颜色变浅，可调度数：0~255
    public static int translateLight(int colorInt, int lightValue) {
        String color = intToString(colorInt);
        int startAlpha = Integer.parseInt(color.substring(0, 2), 16);
        int startRed = Integer.parseInt(color.substring(2, 4), 16);
        int startGreen = Integer.parseInt(color.substring(4, 6), 16);
        int startBlue = Integer.parseInt(color.substring(6), 16);

        startRed += lightValue;
        startGreen += lightValue;
        startBlue += lightValue;

        if (startRed > 255) startRed = 255;
        if (startGreen > 255) startGreen = 255;
        if (startBlue > 255) startBlue = 255;

        return Color.argb(startAlpha, startRed, startGreen, startBlue);
    }

    public static String intToString(int value) {
        String hexString = Integer.toHexString(value);
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    // 获取更深颜色
    public static int getDarkerColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv
        // make darker
        hsv[1] = hsv[1] + 0.1f; // 饱和度更高
        hsv[2] = hsv[2] - 0.1f; // 明度降低
        int darkerColor = Color.HSVToColor(hsv);
        return  darkerColor ;
    }
    // 获取更浅的颜色
    public static int getBrighterColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv

        hsv[1] = hsv[1] - 0.3f; // less saturation
        hsv[2] = hsv[2] * 1.5f; // more brightness
        int darkerColor = Color.HSVToColor(hsv);
        return  darkerColor ;
    }

}
