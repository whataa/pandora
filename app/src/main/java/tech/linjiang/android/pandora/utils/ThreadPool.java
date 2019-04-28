package tech.linjiang.android.pandora.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static void post(Runnable task) {
        cachedThreadPool.submit(task);
    }
}
