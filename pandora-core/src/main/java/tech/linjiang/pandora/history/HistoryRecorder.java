package tech.linjiang.pandora.history;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import tech.linjiang.pandora.cache.History;
import tech.linjiang.pandora.ui.Dispatcher;

/**
 * Created by linjiang on 2019/3/4.
 */

public class HistoryRecorder implements Application.ActivityLifecycleCallbacks {

    private static final int CODE = 0x02;
    private Handler handler;
    private Activity topActivity;

    public static HistoryRecorder register(Application application) {
        HistoryRecorder recorder = new HistoryRecorder();
        application.registerActivityLifecycleCallbacks(recorder);
        return recorder;
    }

    private HistoryRecorder() {
        WorkThread thread = new WorkThread();
        thread.start();
        handler = new Handler(thread.getLooper(), thread);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        record(activity, "onCreate");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        record(activity, "onStart");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        record(activity, "onResume");
        if (!(activity instanceof Dispatcher)) {
            topActivity = activity;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        record(activity, "onPause");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        record(activity, "onStop");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        record(activity, "onSaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        record(activity, "onDestroy");
        if (topActivity == activity) {
            topActivity = null;
        }
    }

    private void record(Activity activity, String event) {
        History history = new History();
        history.createTime = System.currentTimeMillis();
        history.activity = activity.getClass().getSimpleName();
        history.event = event;
        handler.sendMessage(Message.obtain(handler, CODE, history));
    }

    public Activity getTopActivity() {
        return topActivity;
    }

    static class WorkThread extends HandlerThread implements Handler.Callback {

        WorkThread() {
            super("HistoryRecorder");
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == CODE) {
                History.insert((History) msg.obj);
            }
            return true;
        }
    }
}
