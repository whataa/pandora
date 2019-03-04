package tech.linjiang.pandora.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import tech.linjiang.pandora.cache.Crash;

/**
 * Created by linjiang on 2019/3/1.
 */

public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    public CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        Crash.insert(t);
        mDefaultHandler.uncaughtException(thread, t);
    }

}