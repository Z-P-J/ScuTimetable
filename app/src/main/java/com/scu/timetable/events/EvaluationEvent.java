package com.scu.timetable.events;

import com.scu.timetable.model.EvaluationInfo;
import com.zpj.http.core.Connection;

public class EvaluationEvent extends BaseEvent {

    private EvaluationInfo bean;
    private Connection connection;

    private EvaluationEvent() {

    }

    public static EvaluationEvent create() {
        return new EvaluationEvent();
    }

    public EvaluationEvent setEvaluationBean(EvaluationInfo bean) {
        this.bean = bean;
        return this;
    }

    public EvaluationEvent setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public EvaluationInfo getEvaluationBean() {
        return bean;
    }

    public Connection getConnection() {
        return connection;
    }
}
