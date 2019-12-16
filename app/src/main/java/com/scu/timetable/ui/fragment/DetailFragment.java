package com.scu.timetable.ui.fragment;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.leon.lib.settingview.LSettingItem;
import com.scu.timetable.R;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.ui.fragment.base.BaseFragment;
import com.scu.timetable.ui.fragment.base.FullscreenDialogFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;
import com.zpj.popupmenuview.CustomPopupMenuView;

public class DetailFragment extends BaseFragment implements View.OnClickListener, LSettingItem.OnLSettingItemClick {

    private ScuSubject subject;

    public static DetailFragment newInstance(ScuSubject subject) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        fragment.setSubject(subject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        TextView headerTitle = view.findViewById(R.id.header_title);
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

        headerTitle.setText(subject.getCourseName());
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            pop();
        }
    }

    @Override
    public void click(View view, boolean isChecked) {
        int id = view.getId();
        if (id == R.id.item_smart_show_weekends) {
            TimetableHelper.toggleSmartShowWeekends();
        } else if (id == R.id.item_monday_is_first_day) {
            TimetableHelper.toggleSundayIsFirstDay();
        } else if (id == R.id.item_show_non_this_week) {
            TimetableHelper.toggleShowNotCurWeek();
        } else if (id == R.id.item_show_weekends) {
            if (TimetableHelper.isSmartShowWeekends()) {
                AToast.normal("关闭智能显示周末后启用");
            } else {
                TimetableHelper.toggleShowWeekends();
            }
        } else if (id == R.id.item_show_time) {
            TimetableHelper.toggleShowTime();
        } else if (id == R.id.item_change_current_week) {
            TimetableHelper.openChangeCurrentWeekDialog(getContext(), null);
        } else if (id == R.id.item_widget_smart_show_weekends) {
            TimetableWidgtHelper.toggleSmartShowWeekends(getContext());
        }
    }

}
