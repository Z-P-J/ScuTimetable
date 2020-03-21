package com.scu.timetable.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.scu.timetable.R;

/**
 * @author Z-P-J
 */
public final class SuperLinkUtil {

    private SuperLinkUtil() {

    }

    /**
     * 为TextView设置可点击的超链接
     * */
    public static void setSuperLink(TextView textView, String title, String link) {
        // 创建一个 SpannableString对象
        SpannableString sp = new SpannableString(title);
        // 设置超链接
        sp.setSpan(new URLSpan(link), 0, title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sp);
        //设置TextView可点击
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
