package tech.linjiang.pandora.crash;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import tech.linjiang.pandora.cache.Crash;

/**
 * Created by linjiang on 2019/3/1.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final long launchTime;
    private Thread.UncaughtExceptionHandler defHandler;

    public CrashHandler(final Application app) {
        launchTime = System.currentTimeMillis();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                /*
                 Registering here is to prevent other crash-listeners from not callback
                 in case CrashHandler Registered before Application's onCreate.
                 */
                app.unregisterActivityLifecycleCallbacks(this);
                defHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(CrashHandler.this);
            }

            @Override public void onActivityStarted(Activity activity) {}
            @Override public void onActivityResumed(Activity activity) {}
            @Override public void onActivityPaused(Activity activity) {}
            @Override public void onActivityStopped(Activity activity) {}
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override public void onActivityDestroyed(Activity activity) {}
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Crash.insert(e, launchTime);
        if (defHandler != null) {
            defHandler.uncaughtException(t, e);
        }
    }

}