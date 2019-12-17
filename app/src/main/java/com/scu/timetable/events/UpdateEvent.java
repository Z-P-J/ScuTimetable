package com.scu.timetable.events;

import com.scu.timetable.model.UpdateBean;

public class UpdateEvent {

    private String errorMsg;
    private UpdateBean updateBean;
    private boolean isLatestVersion = false;

    private UpdateEvent() {

    }

    public static UpdateEvent create() {
        return new UpdateEvent();
    }

    public UpdateEvent setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public UpdateEvent setUpdateBean(UpdateBean updateBean) {
        this.updateBean = updateBean;
        return this;
    }

    public UpdateBean getUpdateBean() {
        return updateBean;
    }

    public UpdateEvent setLatestVersion(boolean latestVersion) {
        isLatestVersion = latestVersion;
        return this;
    }

    public boolean isLatestVersion() {
        return isLatestVersion;
    }
}
