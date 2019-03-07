package tech.linjiang.pandora.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import tech.linjiang.pandora.cache.Crash;

/**
 * Created by linjiang on 2019/3/1.
 */

public class CrashHandler implements UncaughtExceptionHandler {

    private final long launchTime;

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    public CrashHandler() {
        launchTime = System.currentTimeMillis();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        Crash.insert(t, launchTime);
        mDefaultHandler.uncaughtException(thread, t);
    }

}