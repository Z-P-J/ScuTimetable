package com.scu.timetable.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;

public class SubjectNoteDialog extends CenterDialogFragment {
    
    private ScuSubject subject;

    public SubjectNoteDialog setSubject(ScuSubject subject) {
        this.subject = subject;
        return this;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_subject_note;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        if (subject == null) {
            dismiss();
            return;
        }

        TextView noteTitle = findViewById(R.id.note_title);
        ImageView btnClose = findViewById(R.id.btn_close);
        ImageView btnSave = findViewById(R.id.btn_save);
        EditText editText = findViewById(R.id.edit_text);

        noteTitle.setText(subject.getCourseName() + "的备注");
        editText.setText(subject.getNote());
        editText.setSelection(subject.getNote().length());
        btnClose.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> {
            String note = editText.getText().toString();
            if (subject.getNote().isEmpty() && editText.getText().toString().isEmpty()) {
                AToast.normal("请输入备注！");
                return;
            }
            if (TimetableHelper.saveNote(getContext(), subject, note)) {
                dismiss();
                AToast.normal("保存成功！");
                subject.setNote(note);
            } else {
                AToast.normal("保存失败，请重试！");
            }
        });
    }

}
