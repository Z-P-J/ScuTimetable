package com.scu.timetable.ui.widget;

import android.util.SparseArray;

import com.scu.timetable.R;
import com.zpj.utils.ColorUtils;
import com.zpj.utils.ContextUtils;

import java.util.HashMap;
import java.util.Map;

public class ColorPool {

    private final int[] colors = new int[]{
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4,
            R.color.color_5, R.color.color_6, R.color.color_7, R.color.color_8,
            R.color.color_9, R.color.color_10, R.color.color_11, R.color.color_31,
            R.color.color_32, R.color.color_33, R.color.color_34, R.color.color_35
    };

    private final Map<String, Integer> mColorMap = new HashMap<>();

    public int getColor(String key) {
        return getColor(key, 1f);
    }

    public int getColor(String key, float alpha) {
        int color;
        if (mColorMap.containsKey(key)) {
            Integer colorVal = mColorMap.get(key);
            if (colorVal == null) {
                colorVal = ContextUtils.getApplicationContext().getResources().getColor(colors[0]);
                mColorMap.put(key, colorVal);
            }
            color = colorVal;
        } else {
            color = ContextUtils.getApplicationContext().getResources().getColor(colors[mColorMap.size() % colors.length]);
            mColorMap.put(key, color);
        }
        if (alpha >= 1f) {
            return color;
        }
        return ColorUtils.alphaColor(color, alpha);
    }


}
