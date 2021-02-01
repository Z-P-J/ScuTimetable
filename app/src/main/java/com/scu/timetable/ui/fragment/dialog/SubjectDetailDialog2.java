package com.scu.timetable.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.ui.fragment.DetailFragment;
import com.zpj.fragmentation.dialog.base.BottomDialogFragment;

public class SubjectDetailDialog2 extends BottomDialogFragment {

    private ScuSubject subject;

    public SubjectDetailDialog2 setSubject(ScuSubject subject) {
        this.subject = subject;
        return this;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_subject_detail_2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        if (subject == null) {
            dismiss();
            return;
        }

        TextView tvCourseName = findViewById(R.id.tv_course_name);
        TextView tvCourseRoom = findViewById(R.id.tv_course_room);
        TextView tvCourseTeacher = findViewById(R.id.tv_course_teacher);
        TextView tvCourseTime = findViewById(R.id.tv_course_time);

        tvCourseName.setText(subject.getCourseName());
        tvCourseTeacher.setText(subject.getTeacher());
        tvCourseRoom.setText(subject.getRoom());
        tvCourseTime.setText(subject.getWeekDescription() + " | " + subject.getStart() + " - " + subject.getEnd() + "节");

        ImageView note = findViewById(R.id.subject_note);
        note.setOnClickListener(v -> {
            new SubjectNoteDialog()
                    .setSubject(subject)
                    .show(context);
            dismiss();
        });
        ImageView more = findViewById(R.id.subject_more);
        more.setOnClickListener(v -> {
            DetailFragment.start(subject);
            dismiss();
        });

        ImageView alarm = findViewById(R.id.subject_alarm);
        alarm.setOnClickListener(v -> {
            //todo alarm
            AToast.normal("提醒功能未实现！");
        });
    }

}
