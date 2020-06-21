package com.scu.timetable.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.events.StartFragmentEvent;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.ui.fragment.DetailFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.zpj.popup.core.BottomPopup;

public class SubjectDetailPopup extends BottomPopup<SubjectDetailPopup> {

    private final ScuSubject subject;
    
    public SubjectDetailPopup(@NonNull Context context, ScuSubject subject) {
        super(context);
        this.subject = subject;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_subject_detail;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        TextView courseName = findViewById(R.id.course_name);
        DetailLayout teacherName = findViewById(R.id.teacher_name);
        DetailLayout classRoom = findViewById(R.id.class_room);
        DetailLayout classTime = findViewById(R.id.class_time);
//                            TextView courseSequenceNum = findViewById(R.id.course_sequence_num);
//                            TextView courseNum = findViewById(R.id.course_num);

        courseName.setText(subject.getCourseName());
        teacherName.setContent(subject.getTeacher());
        classRoom.setContent(subject.getRoom());
        classTime.setContent(subject.getClassTime());

        if (!TextUtils.isEmpty(subject.getNote())) {
            DetailLayout noteLayout = findViewById(R.id.layout_note);
            noteLayout.setVisibility(View.VISIBLE);
            noteLayout.setContent(subject.getNote());
        }

        ImageView note = findViewById(R.id.subject_note);
        note.setOnClickListener(v -> {
            dismiss();
//            showSubjectNote(subject);
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
