package tech.linjiang.pandora.crash;

import android.os.Handler;
import android.os.Looper;

import tech.linjiang.pandora.cache.Crash;

/**
 * Created by linjiang on 2019/3/1.
 */

public class CrashHandler {

    private final long launchTime;

    public CrashHandler() {
        launchTime = System.currentTimeMillis();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.loop();
                } catch (Throwable t) {
                    Crash.insert(t, launchTime);
                    throw t;
                }
            }
        });
    }

}