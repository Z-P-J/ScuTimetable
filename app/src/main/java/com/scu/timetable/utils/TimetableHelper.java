package com.scu.timetable.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.utils.content.SPHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Z-P-J
 */
public final class TimetableHelper {

    public static final String[] TIMES_1 = {
            "8:15", "9:10", "10:15", "11:10",
            "13:50", "14:45", "15:40", "16:50",
            "17:45", "19:20", "20:15", "21:10"
    };

    public static final String[] TIMES_2 = new String[]{
            "8:00", "9:00", "10:10", "11:00",
            "15:00", "16:00", "17:00", "18:00",
            "19:30", "20:30", "21:30", "22:30"
    };

    static final String UA = "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36";

    private static final String FILE_NAME = "timetable.json";
    private static final String VISITOR_FILE_NAME = "timetable_visitor.json";

    private TimetableHelper() {

    }

//    private static boolean hasJsonFile(Context context) {
////        String fileName = isVisitorMode() ? VISITOR_FILE_NAME : FILE_NAME;
//        File filesDir = context.getFilesDir();
//        if (filesDir.exists()) {
//            for (File file : filesDir.listFiles()) {
//                if (file.getName().equals(FILE_NAME)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private static boolean hasJsonFile(Context context, String fileName) {
//        String fileName = isVisitorMode() ? VISITOR_FILE_NAME : FILE_NAME;
        File filesDir = context.getFilesDir();
        if (filesDir.exists()) {
            for (File file : filesDir.listFiles()) {
                if (file.getName().equals(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String readFromJson(Context context) throws FileNotFoundException {
        String fileName = isVisitorMode() ? VISITOR_FILE_NAME : FILE_NAME;
        FileInputStream inStream = null;
        inStream = context.openFileInput(fileName);
        //输出到内存
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] contentByte = outStream.toByteArray();
            return new String(contentByte);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ScuSubject> getSubjects(Context context) {
        try {
            String json = readFromJson(context);
            return getSubjects(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    public static List<ScuSubject> getSubjects(String json) throws Exception {
        List<ScuSubject> scuSubjectList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json).getJSONArray("dateList").getJSONObject(0);
        JSONArray jsonArray = jsonObject.getJSONArray("selectCourseList");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray array = jsonArray.getJSONObject(i).getJSONArray("timeAndPlaceList");
            String attendClassTeacher = jsonArray.getJSONObject(i).getString("attendClassTeacher");
            String name = jsonArray.getJSONObject(i).getString("courseName");
            String examTypeName = jsonArray.getJSONObject(i).getString("examTypeName");
            String coursePropertiesName = jsonArray.getJSONObject(i).getString("coursePropertiesName");
            String courseCategoryName = jsonArray.getJSONObject(i).getString("courseCategoryName");
            String restrictedCondition = jsonArray.getJSONObject(i).getString("restrictedCondition");
            String programPlanName = jsonArray.getJSONObject(i).getString("programPlanName");
            String studyModeName = jsonArray.getJSONObject(i).getString("studyModeName");
            int unit = jsonArray.getJSONObject(i).getInt("unit");
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

//                    ScuSubject scuSubject = new ScuSubject("2018-2019学年春", name, room, attendClassTeacher, getWeekList(object.getString("weekDescription")), start, step, day, -1, null);
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
        return scuSubjectList;
    }

//    public static void writeToJson(Context context, String fileName, String content) throws Exception {
//        FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//        outStream.write(content.getBytes());
//        outStream.close();
//    }

    public static void writeToJson(Context context, String content) throws Exception {
        String fileName = isVisitorMode() ? VISITOR_FILE_NAME : FILE_NAME;
        FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        outStream.write(content.getBytes());
        outStream.close();
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
        if (SPHelper.getBoolean("logined", false) && hasJsonFile(context, FILE_NAME)) {
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
            String json = readFromJson(context);
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
                            writeToJson(context, jsonObject.toString());
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
        SPHelper.putInt("current_weak", week);
    }

    public static int getCurrentWeek() {
        return SPHelper.getInt("current_weak", 1);
    }

    public static void setCurrentDate(String date) {
        SPHelper.putString("current_date", date);
    }

    private static String getCurrentDate() {
        return SPHelper.getString("current_date", "");
    }

    public static boolean isShowWeekends() {
        boolean showWeekends = SPHelper.getBoolean("show_weekends", false);
        if (isSmartShowWeekends()) {
            int day = DateUtil.dayOfWeek();
            if (day == 1 || day == 7) {
                return true;
            }
        }
        return showWeekends;
    }

    public static boolean isShowWeekendsOrin() {
        return SPHelper.getBoolean("show_weekends", false);
    }

    public static void toggleShowWeekends() {
        SPHelper.putBoolean("show_weekends", !isShowWeekendsOrin());
    }

    public static boolean isShowTime() {
        return SPHelper.getBoolean("show_time", true);
    }

    public static void toggleShowTime() {
        SPHelper.putBoolean("show_time", !isShowTime());
    }

    public static boolean isShowNotCurWeek() {
        return SPHelper.getBoolean("show_not_cur_week", true);
    }

    public static void toggleShowNotCurWeek() {
        SPHelper.putBoolean("show_not_cur_week", !isShowNotCurWeek());
    }

    public static boolean isSmartShowWeekends() {
        return SPHelper.getBoolean("smart_show_weekends", true);
    }

    public static void toggleSmartShowWeekends() {
        SPHelper.putBoolean("smart_show_weekends", !isSmartShowWeekends());
    }

    public static boolean sundayIsFirstDay() {
        return SPHelper.getBoolean("sunday_is_first_day", true);
    }

    public static void toggleSundayIsFirstDay() {
        SPHelper.putBoolean("sunday_is_first_day", !sundayIsFirstDay());
    }

    public static boolean isVisitorMode() {
        return SPHelper.getBoolean("visitor_mode", false);
    }

    public static boolean startVisitorMode(Context context) {
        SPHelper.putBoolean("visitor_mode", true);
        if (!hasJsonFile(context, VISITOR_FILE_NAME)) {
            try {
                TimetableHelper.writeToJson(context, FileUtil.readAssetFile(context, "timetable.json"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "启用游客模式失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public static void closeVisitorMode() {
        SPHelper.putBoolean("visitor_mode", false);
    }


}
