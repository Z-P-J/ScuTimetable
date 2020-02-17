package com.scu.timetable.events;

import com.scu.timetable.model.UpdateInfo;

public class UpdateEvent {

    private UpdateInfo updateInfo;
    private boolean isLatestVersion = false;

    private UpdateEvent() {

    }

    public static UpdateEvent create() {
        return new UpdateEvent();
    }

    public UpdateEvent setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
        return this;
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    public UpdateEvent setLatestVersion(boolean latestVersion) {
        isLatestVersion = latestVersion;
        return this;
    }

    public boolean isLatestVersion() {
        return isLatestVersion;
    }
}
