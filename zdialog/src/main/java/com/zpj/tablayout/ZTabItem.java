/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zpj.tablayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.zdialog.R;

/**
 * TabItem is a special 'view' which allows you to declare tab items for a {@link com.zpj.tablayout.ZTabLayout}
 * within a layout. This view is not actually added to TabLayout, it is just a dummy which allows
 * setting of a tab items's text, icon and custom layout. See TabLayout for more information on how
 * to use it.
 *
 * @attr ref android.support.design.R.styleable#TabItem_android_icon
 * @attr ref android.support.design.R.styleable#TabItem_android_text
 * @attr ref android.support.design.R.styleable#TabItem_android_layout
 *
 * @see ZTabLayout
 */
public final class ZTabItem extends View {
    final CharSequence mText;
    final Drawable mIcon;
    final int mCustomLayout;

    public ZTabItem(Context context) {
        this(context, null);
    }

    @SuppressLint("RestrictedApi")
    public ZTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.ZTabItem);
        mText = a.getText(R.styleable.ZTabItem_android_text);
        mIcon = a.getDrawable(R.styleable.ZTabItem_android_icon);
        mCustomLayout = a.getResourceId(R.styleable.ZTabItem_android_layout, 0);
        a.recycle();
    }
}