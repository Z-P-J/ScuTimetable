package com.scu.timetable.bean;

import android.support.annotation.NonNull;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:23
 */
public class SemesterInfo {

    private String semesterCode;
    private String semesterName;
    private boolean isCurrent;

    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @NonNull
    @Override
    public String toString() {
        return semesterName;
    }
}
