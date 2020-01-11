package com.scu.timetable.events;

import com.scu.timetable.model.EvaluationBean;
import com.zpj.http.core.Connection;

public class EvaluationEvent {

    private EvaluationBean bean;
    private Connection connection;

    private EvaluationEvent() {

    }

    public static EvaluationEvent create() {
        return new EvaluationEvent();
    }

    public EvaluationEvent setEvaluationBean(EvaluationBean bean) {
        this.bean = bean;
        return this;
    }

    public EvaluationEvent setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public EvaluationBean getEvaluationBean() {
        return bean;
    }

    public Connection getConnection() {
        return connection;
    }
}
