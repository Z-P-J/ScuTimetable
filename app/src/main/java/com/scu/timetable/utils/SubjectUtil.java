package com.scu.timetable.utils;

import android.content.Context;
import android.util.Log;

import com.scu.timetable.model.MySubject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 25714
 */
public class SubjectUtil {

    private static final String FILE_NAME = "timetable.json";

    private SubjectUtil() {

    }

    public static boolean hasJsonFile(Context context) {
        File filesDir = context.getFilesDir();
        if (filesDir.exists()) {
            for (File file : filesDir.listFiles()) {
                if (file.getName().equals(FILE_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String readFromJson(Context context) throws FileNotFoundException {
        FileInputStream inStream = null;
        inStream = context.openFileInput(FILE_NAME);
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

    public static List<MySubject> getSubjects(Context context) {
        List<MySubject> mySubjectList = new ArrayList<>();
        try {
            String json = SubjectUtil.readFromJson(context);
            JSONObject jsonObject = new JSONObject(json).getJSONArray("dateList").getJSONObject(0);
            JSONArray jsonArray = jsonObject.getJSONArray("selectCourseList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONObject(i).getJSONArray("timeAndPlaceList");
                String attendClassTeacher = jsonArray.getJSONObject(i).getString("attendClassTeacher");
                String name = jsonArray.getJSONObject(i).getString("courseName");
                for (int j = 0; j < array.length(); j++) {
                    JSONObject object = array.getJSONObject(j);
                    String classroomName = object.getString("classroomName");
                    String teachingBuildingName = object.getString("teachingBuildingName");
                    String room = teachingBuildingName + classroomName;
                    int start = object.getInt("classSessions");
                    int step = object.getInt("continuingSession");
                    int day = object.getInt("classDay");
                    MySubject mySubject = new MySubject("2018-2019学年春", name, room, attendClassTeacher, getWeekList(object.getString("weekDescription")), start, step, day, -1, null);
                    mySubjectList.add(mySubject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mySubjectList;
    }

    public static void writeToJson(Context context, String content) throws Exception {
        FileOutputStream outStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
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

}
