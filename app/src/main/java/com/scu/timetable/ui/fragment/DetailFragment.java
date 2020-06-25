package com.scu.timetable.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.events.StartFragmentEvent;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.ui.widget.DetailLayout;
import com.zpj.fragmentation.BaseFragment;

public class DetailFragment extends BaseFragment {

    private ScuSubject subject;

    public static DetailFragment newInstance(ScuSubject subject) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        fragment.setSubject(subject);
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(ScuSubject subject) {
        StartFragmentEvent.start(newInstance(subject));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        DetailLayout courseName = view.findViewById(R.id.course_name);
        DetailLayout teacherName = view.findViewById(R.id.teacher_name);
        DetailLayout classTime = view.findViewById(R.id.class_time);
        DetailLayout classRoom = view.findViewById(R.id.class_room);
        DetailLayout courseNum = view.findViewById(R.id.course_num);
        DetailLayout courseSequenceNum = view.findViewById(R.id.course_sequence_num);
        DetailLayout studyUnit = view.findViewById(R.id.study_unit);
        DetailLayout courseProperties = view.findViewById(R.id.course_properties);
        DetailLayout courseType = view.findViewById(R.id.course_type);
        DetailLayout examType = view.findViewById(R.id.exam_type);
        DetailLayout studyMode = view.findViewById(R.id.study_mode);
        DetailLayout restrictedCondition = view.findViewById(R.id.restricted_condition);

        setToolbarTitle(subject.getCourseName());
        courseName.setContent(subject.getCourseName());
        teacherName.setContent(subject.getTeacher());
        classTime.setContent(subject.getClassTime());
        classRoom.setContent(subject.getRoom());
        courseNum.setContent(subject.getCoureNumber());
        courseSequenceNum.setContent(subject.getCoureSequenceNumber());
        studyUnit.setContent(subject.getUnit());
        courseProperties.setContent(subject.getCourseProperties());
        courseType.setContent(subject.getCourseCategory());
        examType.setContent(subject.getExamType());
        studyMode.setContent(subject.getStudyMode());
        restrictedCondition.setContent(subject.getRestrictedCondition());
    }

    public void setSubject(ScuSubject subject) {
        this.subject = subject;
    }

}
