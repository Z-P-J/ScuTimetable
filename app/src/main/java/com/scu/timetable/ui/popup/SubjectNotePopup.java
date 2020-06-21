package com.scu.timetable.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.popup.core.CenterPopup;
import com.scu.timetable.R;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.utils.TimetableHelper;

public class SubjectNotePopup extends CenterPopup<SubjectNotePopup> {
    
    private final ScuSubject subject;
    
    public SubjectNotePopup(@NonNull Context context, ScuSubject subject) {
        super(context);
        this.subject = subject;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_subject_note;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
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
