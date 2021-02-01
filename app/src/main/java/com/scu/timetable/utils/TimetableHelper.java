package com.scu.timetable.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.bean.SemesterInfo;
import com.zpj.utils.FileUtils;
import com.zpj.utils.PrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Z-P-J
 * 课程表工具类
 */
public final class TimetableHelper {

    /**
     * 江安校区作息时间
     */
    public static final String[] TIMES_1 = {
            "8:15", "9:10", "10:15", "11:10",
            "13:50", "14:45", "15:40", "16:45",
            "17:40", "19:20", "20:15", "21:10"
    };

    public static final String[] TIMES_END_1 = {
            "9:00", "9:55", "11:00", "11:55",
            "14:35", "15:30", "16:25", "17:30",
            "18:25", "20:25", "21:00", "21:55"
    };

    /**
     * 望江及华西校区作息时间
     */
    public static final String[] TIMES_2 = new String[]{
            "8:00", "8:55", "10:00", "10:55",
            "14:00", "14:55", "15:50", "16:55",
            "17:50", "19:30", "20:25", "21:20"
    };

    static final String UA = "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36";

    private static final String FILE_NAME = "timetable_%s.json";
    private static final String VISITOR_FILE_NAME = "timetable_visitor.json";
    private static final String SEMESTER_FILE_NAME = "timetable_semester.json";

    private TimetableHelper() {

    }

    private static String getFileName() {
        return getFileName(getCurrentSemesterCode());
//        String currentSemesterCode = getCurrentSemesterCode();
//        return isVisitorMode() ? VISITOR_FILE_NAME : String.format(FILE_NAME, currentSemesterCode);
    }

    private static String getFileName(String semesterCode) {
        return isVisitorMode() ? VISITOR_FILE_NAME : String.format(FILE_NAME, semesterCode);
    }

    public static List<ScuSubject> getSubjects(Context context) {
        try {
            String json = JsonUtil.readFromJson(context, getFileName());
            return getSubjects(json);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("getSubjects", "e.getMessage=" + e.getMessage());
        }
        return new ArrayList<>(0);
    }

    public static List<ScuSubject> getSubjects(Context context, SemesterInfo semesterInfo) {
        try {
            String json = JsonUtil.readFromJson(context, getFileName(semesterInfo.getSemesterCode()));
            return getSubjects(json);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("getSubjects", "e.getMessage=" + e.getMessage());
        }
        return new ArrayList<>(0);
    }

    public static List<ScuSubject> getSubjects(String json) throws Exception {
        Log.d("getSubjects", "json=" + json);
        List<ScuSubject> scuSubjectList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json).getJSONArray("dateList").getJSONObject(0);
        JSONArray jsonArray = jsonObject.getJSONArray("selectCourseList");
        for (int i = 0; i < jsonArray.length(); i++) {
            String attendClassTeacher = jsonArray.getJSONObject(i).getString("attendClassTeacher");
            String name = jsonArray.getJSONObject(i).getString("courseName");
            String examTypeName = jsonArray.getJSONObject(i).getString("examTypeName");
            String coursePropertiesName = jsonArray.getJSONObject(i).getString("coursePropertiesName");
            String courseCategoryName = jsonArray.getJSONObject(i).getString("courseCategoryName");
            String restrictedCondition = jsonArray.getJSONObject(i).getString("restrictedCondition");
            String programPlanName = jsonArray.getJSONObject(i).getString("programPlanName");
            String studyModeName = jsonArray.getJSONObject(i).getString("studyModeName");
            int unit = jsonArray.getJSONObject(i).getInt("unit");
            JSONArray array = jsonArray.getJSONObject(i).optJSONArray("timeAndPlaceList");
            if (array != null) {
                for (int j = 0; j < array.length(); j++) {
                    JSONObject object = array.getJSONObject(j);
                    String classroomName = object.getString("classroomName");
                    String teachingBuildingName = object.getString("teachingBuildingName");
                    String room = teachingBuildingName + classroomName;
                    String weekDescription = object.getString("weekDescription");
                    int start = object.getInt("classSessions");
                    int step = object.getInt("continuingSession");
                    int day = object.getInt("classDay");
                    day = day + 1;
                    if (day == 8) {
                        day = 1;
                    }
                    String campusName = object.getString("campusName");
                    String courseNumber = object.getString("coureNumber");
                    String courseSequenceNumber = object.getString("coureSequenceNumber");
                    String executiveEducationPlanNumber = object.getString("executiveEducationPlanNumber");
                    String note = "";
                    if (object.has("note")) {
                        note = object.getString("note");
                    }
                    ScuSubject scuSubject = new ScuSubject();
                    scuSubject.setTerm("2018-2019学年春");
                    scuSubject.setCourseName(name);
                    scuSubject.setClassroom(classroomName);
                    scuSubject.setTeachingBuilding(teachingBuildingName);
                    scuSubject.setTeacher(attendClassTeacher);
                    scuSubject.setRoom(room);
                    scuSubject.setWeekDescription(weekDescription);
                    scuSubject.setWeekList(getWeekList(weekDescription));
                    scuSubject.setStart(start);
                    scuSubject.setStep(step);
                    scuSubject.setDay(day);
                    scuSubject.setColorRandom(-1);
                    scuSubject.setTime(null);
                    scuSubject.setCourseProperties(coursePropertiesName);
                    scuSubject.setCampusName(campusName);
                    scuSubject.setCoureNumber(courseNumber);
                    scuSubject.setCoureSequenceNumber(courseSequenceNumber);
                    scuSubject.setExamType(examTypeName);
                    scuSubject.setCourseCategory(courseCategoryName);
                    scuSubject.setRestrictedCondition(restrictedCondition);
                    scuSubject.setProgramPlan(programPlanName);
                    scuSubject.setStudyMode(studyModeName);
                    scuSubject.setUnit("" + unit);
                    scuSubject.setNote(note);
                    scuSubjectList.add(scuSubject);
                }
            }
        }
        return scuSubjectList;
    }

    public static void writeToJson(Context context, JSONObject jsonObject) throws Exception {
        JsonUtil.writeToJson(context, String.format(FILE_NAME, jsonObject.getString("semester_code")), jsonObject.toString());
    }

    private static List<Integer> getWeekList(String weeksString) {
        Log.d("getWeekList", "weeksString=" + weeksString);
        List<Integer> weekList = new ArrayList<>();
        if (weeksString == null || weeksString.length() == 0) {
            return weekList;
        }

        weeksString = weeksString.replaceAll("[^\\d\\-\\,]", "");
        if (weeksString.contains(",")) {
            String[] arr = weeksString.split(",");
            for (String s : arr) {
                weekList.addAll(getWeekList(s));
            }
        } else {
            int first = -1, end = -1, index = -1;
            if ((index = weeksString.indexOf("-")) != -1) {
                first = Integer.parseInt(weeksString.substring(0, index));
                end = Integer.parseInt(weeksString.substring(index + 1));
            } else {
                first = Integer.parseInt(weeksString);
                end = first;
            }
            for (int i = first; i <= end; i++) {
                weekList.add(i);
            }
        }
        Log.d("getWeekList", "weekList=" + weekList.toString());
        return weekList;
    }

    public static boolean isLogined(Context context) {
        if (PrefsHelper.with().getBoolean("logined", false) && JsonUtil.hasJsonFile(context, String.format(FILE_NAME, getCurrentSemesterCode()))) {
            String date = getCurrentDate();
            if (!date.isEmpty()) {
                Date oldDate = DateUtil.parse(date);
                int weeks = DateUtil.computeWeek(oldDate, new Date());
                Log.d("weeks", "weeks=" + weeks);
                int currentWeek = getCurrentWeek();
                Log.d("currentWeek", "currentWeek=" + currentWeek);
                setCurrentWeek(currentWeek + weeks);
            }
            setCurrentDate(DateUtil.currentDate());
            return true;
        }
        return false;
    }

    public static boolean saveNote(Context context, ScuSubject subject, String note) {
        try {
            String json = JsonUtil.readFromJson(context, getFileName());
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("dateList")
                    .getJSONObject(0)
                    .getJSONArray("selectCourseList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String attendClassTeacher = jsonArray.getJSONObject(i).getString("attendClassTeacher");
                String name = jsonArray.getJSONObject(i).getString("courseName");
                if (subject.getTeacher().equals(attendClassTeacher) && subject.getCourseName().equals(name)) {
                    JSONArray array = jsonArray.getJSONObject(i).getJSONArray("timeAndPlaceList");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        int start = object.getInt("classSessions");
                        int step = object.getInt("continuingSession");
                        int day = object.getInt("classDay");
                        day = day + 1;
                        if (day == 8) {
                            day = 1;
                        }
                        if (subject.getDay() == day && subject.getStart() == start && subject.getStep() == step) {
                            object.put("note", note);
                            JsonUtil.writeToJson(context, getFileName(), jsonObject.toString());
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openChangeCurrentWeekDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        final String[] items = new String[20];
        for (int i = 0; i < 20; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        final int[] target = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, TimetableHelper.getCurrentWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target[0] = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target[0] != -1) {
                    TimetableHelper.setCurrentWeek(target[0] + 1);
                    if (onClickListener != null) {
                        onClickListener.onClick(dialog, target[0] + 1);
                    }
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public static void setCurrentWeek(int week) {
        PrefsHelper.with().putInt("current_weak", week);
    }

    public static int getCurrentWeek() {
        return PrefsHelper.with().getInt("current_weak", 1);
    }

    public static void setCurrentDate(String date) {
        PrefsHelper.with().putString("current_date", date);
    }

    private static String getCurrentDate() {
        return PrefsHelper.with().getString("current_date", "");
    }

    public static boolean isShowWeekends() {
        boolean showWeekends = PrefsHelper.with().getBoolean("show_weekends", false);
        if (isSmartShowWeekends()) {
            int day = DateUtil.dayOfWeek();
            if (day == 1 || day == 7) {
                return true;
            }
        }
        return showWeekends;
    }

    public static boolean isShowWeekendsOrin() {
        return PrefsHelper.with().getBoolean("show_weekends", false);
    }

    public static void toggleShowWeekends() {
        PrefsHelper.with().putBoolean("show_weekends", !isShowWeekendsOrin());
    }

    public static boolean isShowTime() {
        return PrefsHelper.with().getBoolean("show_time", true);
    }

    public static void toggleShowTime() {
        PrefsHelper.with().putBoolean("show_time", !isShowTime());
    }

    public static void toggleSpeech() {
        PrefsHelper.with().putBoolean("text_to_speech", !isSpeech());
    }

    public static boolean isSpeech() {
        return PrefsHelper.with().getBoolean("text_to_speech", true);
    }

    public static boolean isShowNotCurWeek() {
        return PrefsHelper.with().getBoolean("show_not_cur_week", true);
    }

    public static void toggleShowNotCurWeek() {
        PrefsHelper.with().putBoolean("show_not_cur_week", !isShowNotCurWeek());
    }

    public static boolean isSmartShowWeekends() {
        return PrefsHelper.with().getBoolean("smart_show_weekends", true);
    }

    public static void toggleSmartShowWeekends() {
        PrefsHelper.with().putBoolean("smart_show_weekends", !isSmartShowWeekends());
    }

    public static boolean sundayIsFirstDay() {
        return PrefsHelper.with().getBoolean("sunday_is_first_day", true);
    }

    public static void toggleSundayIsFirstDay() {
        PrefsHelper.with().putBoolean("sunday_is_first_day", !sundayIsFirstDay());
    }

    public static boolean isVisitorMode() {
        return PrefsHelper.with().getBoolean("visitor_mode", false);
    }

    public static boolean startVisitorMode(Context context) {
        PrefsHelper.with().putBoolean("visitor_mode", true);
        if (!JsonUtil.hasJsonFile(context, VISITOR_FILE_NAME)) {
            try {
                JsonUtil.writeToJson(context, VISITOR_FILE_NAME, FileUtils.readAssetFile(context, "timetable.json"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                AToast.normal("启用游客模式失败！");
                return false;
            }
        }
        return true;
    }

    public static void closeVisitorMode() {
        PrefsHelper.with().putBoolean("visitor_mode", false);
    }

    public static void setCurrentSemester(final String currentSemesterCode, final String currentSenesterName) {
        PrefsHelper.with().putString("current_semester_code", currentSemesterCode);
        PrefsHelper.with().putString("current_semester_name", currentSenesterName);
    }

    public static List<SemesterInfo> getSemesterList(Context context) {
        List<SemesterInfo> semesterInfoList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(JsonUtil.readFromJson(context, SEMESTER_FILE_NAME));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SemesterInfo semester = new SemesterInfo();
                semester.setSemesterCode(jsonObject.getString("code"));
                semester.setSemesterName(jsonObject.getString("name"));
                semesterInfoList.add(semester);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return semesterInfoList;
    }

    public static void writeSemesterFile(Context context, String json) throws Exception {
        JsonUtil.writeToJson(context, SEMESTER_FILE_NAME, json);
    }

    public static String getCurrentSemesterCode() {
        return PrefsHelper.with().getString("current_semester_code", "2018-2019-2-1");
    }

    public static String getCurrentSemesterName() {
        return PrefsHelper.with().getString("current_semester_name", "2018-2019学年春(当前)");
    }

}
