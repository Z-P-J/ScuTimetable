//package com.scu.timetable.ui.fragment;
//
//import android.animation.ValueAnimator;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.felix.atoast.library.AToast;
//import com.leon.lib.settingview.LSettingItem;
//import com.scu.timetable.R;
//import com.scu.timetable.model.ScuSubject;
//import com.scu.timetable.ui.fragment.base.FullscreenDialogFragment;
//import com.scu.timetable.ui.widget.DetailLayout;
//import com.scu.timetable.utils.TimetableHelper;
//import com.scu.timetable.utils.TimetableWidgtHelper;
//import com.zpj.popupmenuview.CustomPopupMenuView;
//
//public class DetailDialogFragment extends FullscreenDialogFragment implements View.OnClickListener, LSettingItem.OnLSettingItemClick {
//
//    private FrameLayout background;
//
//    private float currentAlpha = 0.0f;
//
//    private Callback callback;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        FrameLayout frameLayout = new FrameLayout(getContext());
//        View view = inflater.inflate(R.layout.dialog_fragment_detail, null, false);
//        frameLayout.addView(view);
//        background = new FrameLayout(getContext());
//        background.setBackgroundColor(Color.BLACK);
//        background.setAlpha(currentAlpha);
//        frameLayout.addView(background);
//        if (callback != null) {
//            initView(view);
//        }
//        return frameLayout;
//    }
//
//    private void initView(View view) {
//        ScuSubject subject = callback.fechSubject();
//
////        Toolbar toolbar = view.findViewById(R.id.toolbar);
////        toolbar.setTitle(subject.getCourseName());
////        toolbar.setTitleTextColor(Color.WHITE);
//
//        TextView headerTitle = view.findViewById(R.id.header_title);
//        DetailLayout courseName = view.findViewById(R.id.course_name);
//        DetailLayout teacherName = view.findViewById(R.id.teacher_name);
//        DetailLayout classTime = view.findViewById(R.id.class_time);
//        DetailLayout classRoom = view.findViewById(R.id.class_room);
//        DetailLayout courseNum = view.findViewById(R.id.course_num);
//        DetailLayout courseSequenceNum = view.findViewById(R.id.course_sequence_num);
//        DetailLayout studyUnit = view.findViewById(R.id.study_unit);
//        DetailLayout courseProperties = view.findViewById(R.id.course_properties);
//        DetailLayout courseType = view.findViewById(R.id.course_type);
//        DetailLayout examType = view.findViewById(R.id.exam_type);
//        DetailLayout studyMode = view.findViewById(R.id.study_mode);
//        DetailLayout restrictedCondition = view.findViewById(R.id.restricted_condition);
//
//        headerTitle.setText(subject.getCourseName());
//        courseName.setContent(subject.getCourseName());
//        teacherName.setContent(subject.getTeacher());
//        classTime.setContent(subject.getClassTime());
//        classRoom.setContent(subject.getRoom());
//        courseNum.setContent(subject.getCoureNumber());
//        courseSequenceNum.setContent(subject.getCoureSequenceNumber());
//        studyUnit.setContent(subject.getUnit());
//        courseProperties.setContent(subject.getCourseProperties());
//        courseType.setContent(subject.getCourseCategory());
//        examType.setContent(subject.getExamType());
//        studyMode.setContent(subject.getStudyMode());
//        restrictedCondition.setContent(subject.getRestrictedCondition());
//    }
//
//    private void initBackground(View view) {
////        LinearLayout linearLayout = view.findViewById(R.id.container);
////        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_login);
////
////        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
////                mBitmap.getWidth() / 4,
////                mBitmap.getHeight() / 4,
////                false);
////        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 20, true);
////
////        linearLayout.setBackground(new BitmapDrawable(null, blurBitmap));
//    }
//
//    public void setBackgroudAlpha(float alpha) {
//        ValueAnimator animator = ValueAnimator.ofFloat(currentAlpha, alpha);
//        currentAlpha = alpha;
////        if (alpha > 0.0f) {
////            animator = ValueAnimator.ofFloat(0.0f, alpha);;
////        } else {
////            ValueAnimator.ofFloat(0.0f, alpha);
////        }
//        animator.setDuration(300);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (Float) animation.getAnimatedValue();
//                background.setAlpha(value);
//            }
//        });
//        animator.start();
//    }
//
//    private void showInfoPopupView(View view, final String title, final String content) {
//        CustomPopupMenuView.with(getContext(), R.layout.layout_text)
//                .setOrientation(LinearLayout.VERTICAL)
////                .setBackgroundAlpha(getActivity(), 0.9f)
//                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
////                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 350, 100, 0)
////                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 350, -100, 0)
//                .setAnimationAlphaShow(350, 0.0f, 1.0f)
//                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
//                .initViews(
//                        1,
//                        (popupMenuView, itemView, position) -> {
//                            TextView titleView = itemView.findViewById(R.id.title);
//                            titleView.setText(title);
//                            TextView contentView = itemView.findViewById(R.id.content);
//
//                            StringBuilder content2 = new StringBuilder(content);
//                            if (title.length() >= content2.length()) {
//                                for (int i = 0; i < title.length() * 4; i++) {
//                                    content2.append(" ");
//                                }
//                            }
//                            contentView.setText(content2.toString());
//                            ImageView btnClose = itemView.findViewById(R.id.btn_close);
//                            btnClose.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    popupMenuView.dismiss();
//
//                                }
//                            });
//                        })
//                .setOnPopupWindowDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        setBackgroudAlpha(0.0f);
//                    }
//                })
//                .show(view);
//        setBackgroudAlpha(0.1f);
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_back) {
//            dismiss();
//        }
//    }
//
//    @Override
//    public void click(View view, boolean isChecked) {
//        int id = view.getId();
//        if (id == R.id.item_smart_show_weekends) {
//            TimetableHelper.toggleSmartShowWeekends();
//        } else if (id == R.id.item_monday_is_first_day) {
//            TimetableHelper.toggleSundayIsFirstDay();
//        } else if (id == R.id.item_show_non_this_week) {
//            TimetableHelper.toggleShowNotCurWeek();
//        } else if (id == R.id.item_show_weekends) {
//            if (TimetableHelper.isSmartShowWeekends()) {
//                AToast.normal("关闭智能显示周末后启用");
//            } else {
//                TimetableHelper.toggleShowWeekends();
//            }
//        } else if (id == R.id.item_show_time) {
//            TimetableHelper.toggleShowTime();
//        } else if (id == R.id.item_change_current_week) {
//            TimetableHelper.openChangeCurrentWeekDialog(getContext(), null);
//        } else if (id == R.id.item_widget_smart_show_weekends) {
//            TimetableWidgtHelper.toggleSmartShowWeekends(getContext());
//        }
//
////        Toast.makeText(getContext(), "" + isChecked, Toast.LENGTH_SHORT).show();
//    }
//
//    public void setCallback(Callback callback) {
//        this.callback = callback;
//    }
//
//    public interface Callback {
//        ScuSubject fechSubject();
//    }
//
//}
