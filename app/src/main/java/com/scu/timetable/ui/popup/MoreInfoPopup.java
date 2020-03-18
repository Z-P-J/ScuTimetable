package com.scu.timetable.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.popup.core.CenterPopup;
import com.scu.timetable.R;

public class MoreInfoPopup extends CenterPopup {

    private String title;
    private String content;

    public MoreInfoPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_text;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView titleView = findViewById(R.id.title);
        titleView.setText(title);
        TextView contentView = findViewById(R.id.content);
        contentView.setText(content);

        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());
    }

    public MoreInfoPopup setTitle(String title) {
        this.title = title;
        return this;
    }

    public MoreInfoPopup setContent(String content) {
        this.content = content;
        return this;
    }
}
