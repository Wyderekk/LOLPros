package me.wyderekk.application.task;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskRunner {

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void runInBackground(int initialDelay, int interval, TimeUnit timeUnit, Runnable... runnable) {
        Arrays.stream(runnable).forEach(r -> scheduler.scheduleAtFixedRate(r, initialDelay, interval, timeUnit));
    }
}
