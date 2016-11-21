package com.seng480b.bumerang.interfaces;

public interface AsyncTaskHandler {

    void beforeAsyncTask();

    void afterAsyncTask(String result);

    boolean isAsyncTaskRunning();
}
