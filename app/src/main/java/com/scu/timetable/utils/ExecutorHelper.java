package com.scu.timetable.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Z-P-J
 * @date 2019/6/1 15:16
 */
public final class ExecutorHelper {

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private ExecutorHelper() {

    }

    public static void submit(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

}
