package tech.linjiang.pandora.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.core.R;

public class Utils {

    public static final DateFormat DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.ENGLISH);
    public static final DateFormat NO_MILLIS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final DateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private static Context CONTEXT;
    private static Handler mainHandler;


    private Utils() {
    }

    public static void init(Application context) {
        CONTEXT = context;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("PrivateApi")
    public static @NonNull
    Context getContext() {
        if (CONTEXT != null) {
            return CONTEXT;
        } else {
            ///////// test //////////
            try {
                Class activityThreadClass = Class.forName("android.app.ActivityThread");
                Method method = activityThreadClass.getMethod("currentApplication");
                CONTEXT = (Context) method.invoke(null);
                return CONTEXT;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static <V> boolean isNotEmpty(List<V> sourceList) {
        return !(sourceList == null || sourceList.size() == 0);
    }

    public static <V> int getCount(V[] sourceList) {
        return !(sourceList == null || sourceList.length == 0) ? sourceList.length : 0;
    }

    public static <T> T[] newArray(T... value) {
        return value;
    }

    public static String millis2String(final long millis) {
        return millis2String(millis, DEFAULT);
    }

    public static String millis2String(final long millis, DateFormat format) {
        return format.format(new Date(millis));
    }

    public static void toast(@StringRes int resId) {
        Toast.makeText(CONTEXT, resId, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(CONTEXT, msg, Toast.LENGTH_SHORT).show();
    }

    public static void copy2ClipBoard(String msg) {
        ClipboardManager cm = (ClipboardManager)
                CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            ClipData mClipData = ClipData.newPlainText("text", msg);
            cm.setPrimaryClip(mClipData);
            Utils.toast(R.string.pd_copy_2_clipboard);
        } catch (Throwable t) {
            Utils.toast(t.getMessage());
        }
    }

    public static void post(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void cancelTask(Runnable runnable) {
        mainHandler.removeCallbacks(runnable);
    }

    public static String formatSize(long origin) {
        String value;
        BigDecimal size = new BigDecimal(Long.toString(origin));
        if (size.compareTo(new BigDecimal("1024")) < 0) {
            value = size + "B";
        } else {
            size = size.divide(new BigDecimal("1024"));
            if (size.compareTo(new BigDecimal("1024")) > 0) {
                value = size.divide(new BigDecimal("1024"), 2, BigDecimal.ROUND_DOWN) + "MB";
            } else {
                value = size.setScale(2, BigDecimal.ROUND_DOWN) + "KB";
            }
        }
        return value;
    }

    public static void shareText(String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");
        try {
            CONTEXT.startActivity(Intent.createChooser(shareIntent, "share to"));
        } catch (Throwable t) {
            t.printStackTrace();
            toast(t.getMessage());
        }
    }

    public static boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(CONTEXT)) {
                Utils.toast(R.string.pd_please_allow_permission);
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + CONTEXT.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CONTEXT.startActivity(intent);
                return false;
            }
        }
        // if failed, let user to allow manually
        return true;
    }

    public static void registerSensor(SensorEventListener listener) {
        try {
            SensorManager manager = (SensorManager) CONTEXT.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Throwable t) {
            t.printStackTrace();
            toast(t.getMessage());
        }
    }


    private static long lastCheckTime;
    private static float[] lastXyz = new float[3];

    public static boolean checkIfShake(float x, float y, float z) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastCheckTime;
        if (diffTime < 100) {
            return false;
        }
        lastCheckTime = currentTime;
        float deltaX = x - lastXyz[0];
        float deltaY = y - lastXyz[1];
        float deltaZ = z - lastXyz[2];
        lastXyz[0] = x;
        lastXyz[1] = y;
        lastXyz[2] = z;
        int delta = (int) (Math.sqrt(deltaX * deltaX
                + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
        if (delta > 450) {// a buddhist-style value
            return true;
        }
        return false;
    }
}
