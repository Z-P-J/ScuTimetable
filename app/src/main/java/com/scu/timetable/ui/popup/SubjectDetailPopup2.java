package com.scu.timetable.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.events.StartFragmentEvent;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.ui.fragment.DetailFragment;
import com.zpj.popup.core.BottomPopup;

public class SubjectDetailPopup2 extends BottomPopup<SubjectDetailPopup2> {

    private final ScuSubject subject;

    public SubjectDetailPopup2(@NonNull Context context, ScuSubject subject) {
        super(context);
        this.subject = subject;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_subject_detail_2;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

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
            dismiss();
            new SubjectNotePopup(context, subject).show();
        });
        ImageView more = findViewById(R.id.subject_more);
        more.setOnClickListener(v -> {
            dismiss();
            new StartFragmentEvent(DetailFragment.newInstance(subject)).post();
        });

        ImageView alarm = findViewById(R.id.subject_alarm);
        alarm.setOnClickListener(v -> {
            //todo alarm
            AToast.normal("提醒功能未实现！");
        });

    }
}
