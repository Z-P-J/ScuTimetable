package com.scu.timetable.model;

import android.support.annotation.NonNull;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:23
 */
public class SemesterInfo {

    private String semesterCode;
    private String semesterName;

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

    @NonNull
    @Override
    public String toString() {
        return semesterName;
    }
}
