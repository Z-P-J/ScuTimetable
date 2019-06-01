package com.scu.timetable.model;

/**
 * @author Z-P-J
 * @date 2019/6/1 22:43
 */
public class UpdateBean {

    private String versionName;
    private String fileSize;
    private String updateTime;
    private String updateContent;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    @Override
    public String toString() {
        return "UpdateBean{" +
                "versionName='" + versionName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", updateContent='" + updateContent + '\'' +
                '}';
    }
}
