package com.scu.timetable.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.timetable.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.setting.CommonSettingItem;

public class UpdateSettingItem extends CommonSettingItem {

    private ImageView rightIcon;

    public UpdateSettingItem(Context context) {
        this(context, null);
    }

    public UpdateSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.layout_update_item);
//        viewStub.setInflatedId(R.id.iv_right_icon);
        View view = viewStub.inflate();
        TextView tvVersion = view.findViewById(R.id.tv_version);
        ImageView ivNew = view.findViewById(R.id.iv_new);
//        ViewGroup.LayoutParams params = rightIcon.getLayoutParams();
//        int maxSize = ScreenUtils.dp2pxInt(rightIcon.getContext(), 36);
//        params.height = maxSize;
//        params.width = maxSize;
//        rightIcon.setMaxHeight(maxSize);
//        rightIcon.setMaxWidth(maxSize);
////        rightIcon.setBorderWidth(0);
////        rightIcon.setCornerRadius(4);
//        if (mRightIcon != null) {
//            rightIcon.setImageDrawable(mRightIcon);
//        }
    }

    @Override
    public void inflateInfoButton(ViewStub viewStub) {

    }

    @Override
    public void onItemClick() {
        super.onItemClick();
    }

}

