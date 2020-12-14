package com.scu.timetable.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.timetable.R;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;

public class MoreInfoDialog extends CenterDialogFragment {

    private String title;
    private String content;

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_text;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        TextView titleView = findViewById(R.id.title);
        titleView.setText(title);
        TextView contentView = findViewById(R.id.content);
        contentView.setText(content);

        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());
    }

    public MoreInfoDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public MoreInfoDialog setContent(String content) {
        this.content = content;
        return this;
    }
}
