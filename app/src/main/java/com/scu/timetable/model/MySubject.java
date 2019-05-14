package com.scu.timetable.model;

import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.List;

/**
 * 自定义实体类需要实现ScheduleEnable接口并实现getSchedule()
 *
 * @see ScheduleEnable#getSchedule()
 */
public class MySubject implements ScheduleEnable {

    public static final String EXTRAS_ID = "extras_id";
    public static final String EXTRAS_AD_URL = "extras_ad_url";

    private int id = 0;

    /**
     * 课程名
     */
    private String courseName;

    //无用数据
    private String time;

    /**
     * 教室
     */
    private String room;

    /**
     * 教师
     */
    private String teacher;

    /**
     * 第几周至第几周上
     */
    private List<Integer> weekList;

    /**
     * 开始上课的节次
     */
    private int start;

    /**
     * 上课节数
     */
    private int step;

    /**
     * 周几上
     */
    private int day;

    private String term;

    /**
     * 一个随机数，用于对应课程的颜色
     */
    private int colorRandom = 0;

    private String url;

    private String courseProperties;

    private String teachingBuilding;

    private String classroom;

    private String coureNumber;

    private String coureSequenceNumber;

    private String campusName;

    private String weekDescription;

    private String examType;

    private String courseCategory;

    private String restrictedCondition;

    private String programPlan;

    private String studyMode;

    private String unit;

    private String note = "";

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public MySubject() {
        // TODO Auto-generated constructor stub
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public MySubject(int start, int day) {
        this.start = start;
        this.step = 1;
        this.day = day;
        this.courseName = "";
    }

//    public MySubject(String term, String courseName, String room, String teacher, List<Integer> weekList, int start, int step, int day, int colorRandom, String time) {
//        super();
//        this.term = term;
//        this.courseName = courseName;
//        this.room = room;
//        this.teacher = teacher;
//        this.weekList = weekList;
//        this.start = start;
//        this.step = step;
//        this.day = day;
//        this.colorRandom = colorRandom;
//        this.time = time;
//    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setWeekList(List<Integer> weekList) {
        this.weekList = weekList;
    }

    public List<Integer> getWeekList() {
        return weekList;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return start + step -1;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColorRandom() {
        return colorRandom;
    }

    public void setColorRandom(int colorRandom) {
        this.colorRandom = colorRandom;
    }

    public String getCourseProperties() {
        return courseProperties;
    }

    public void setCourseProperties(String courseProperties) {
        this.courseProperties = courseProperties;
    }

    public String getTeachingBuilding() {
        return teachingBuilding;
    }

    public void setTeachingBuilding(String teachingBuilding) {
        this.teachingBuilding = teachingBuilding;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getCoureNumber() {
        return coureNumber;
    }

    public void setCoureNumber(String coureNumber) {
        this.coureNumber = coureNumber;
    }

    public String getCoureSequenceNumber() {
        return coureSequenceNumber;
    }

    public void setCoureSequenceNumber(String coureSequenceNumber) {
        this.coureSequenceNumber = coureSequenceNumber;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getWeekDescription() {
        return weekDescription;
    }

    public void setWeekDescription(String weekDescription) {
        this.weekDescription = weekDescription;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getRestrictedCondition() {
        return restrictedCondition;
    }

    public void setRestrictedCondition(String restrictedCondition) {
        this.restrictedCondition = restrictedCondition;
    }

    public String getProgramPlan() {
        return programPlan;
    }

    public void setProgramPlan(String programPlan) {
        this.programPlan = programPlan;
    }

    public String getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(String studyMode) {
        this.studyMode = studyMode;
    }

    @Override
    public Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setDay(getDay());
        schedule.setName(getCourseName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(getWeekList());
        schedule.setColorRandom(2);
        schedule.putExtras(EXTRAS_ID, getId());
        schedule.putExtras(EXTRAS_AD_URL, getUrl());
        schedule.setScheduleEnable(this);
        return schedule;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MySubject{" +
                "\n\rid=" + id +
                "\n\rcourseName='" + courseName + '\'' +
                "\n\rtime='" + time + '\'' +
                "\n\rroom='" + room + '\'' +
                "\n\rteacher='" + teacher + '\'' +
                "\n\rstart=" + start +
                "\n\rstep=" + step +
                "\n\rday=" + day +
                "\n\rterm='" + term + '\'' +
                "\n\rcourseProperties='" + courseProperties + '\'' +
                "\n\rteachingBuilding='" + teachingBuilding + '\'' +
                "\n\rclassroom='" + classroom + '\'' +
                "\n\rcoureNumber='" + coureNumber + '\'' +
                "\n\rcoureSequenceNumber='" + coureSequenceNumber + '\'' +
                "\n\rcampusName='" + campusName + '\'' +
                "\n\rweekDescription='" + weekDescription + '\'' +
                "\n\rexamType='" + examType + '\'' +
                "\n\rcourseCategory='" + courseCategory + '\'' +
                "\n\rrestrictedCondition='" + restrictedCondition + '\'' +
                "\n\rprogramPlan='" + programPlan + '\'' +
                "\n\rstudyMode='" + studyMode + '\'' +
                "\n}";
    }
}
