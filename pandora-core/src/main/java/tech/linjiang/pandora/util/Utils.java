package tech.linjiang.pandora.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.core.R;

public class Utils {

    public static final DateFormat DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.ENGLISH);
    public static final DateFormat NO_MILLIS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final DateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss SSS", Locale.ENGLISH);

    private static Context CONTEXT;
    private static Handler mainHandler;


    private Utils() {
    }

    public static void init(Context context) {
        CONTEXT = context.getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("PrivateApi")
    public static @NonNull
    Context getContext() {
        return CONTEXT;
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
            CONTEXT.startActivity(Intent.createChooser(shareIntent, "share to").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Throwable t) {
            t.printStackTrace();
            toast(t.getMessage());
        }
    }

    public static void removeViewFromWindow(View v) {
        try {
            WindowManager windowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(v);
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    public static boolean addViewToWindow(View v, WindowManager.LayoutParams params) {
        try {
            WindowManager windowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(v, params);
            return true;
        } catch (Throwable t){
            t.printStackTrace();
            removeViewFromWindow(v);
            return false;
        }
    }

    public static void updateViewLayoutInWindow(View v, WindowManager.LayoutParams params) {
        try {
            WindowManager windowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.updateViewLayout(v, params);
        } catch (Throwable ignore){}
    }

    public static List<String> getActivities() {
        List<String> result = new ArrayList<>();
        try {
            PackageManager packageManager = CONTEXT.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    CONTEXT.getPackageName(), PackageManager.GET_ACTIVITIES);

            for (ActivityInfo activity : packageInfo.activities) {
                result.add(activity.name);
            }
        } catch (PackageManager.NameNotFoundException ignore) {

        }
        Collections.sort(result);
        return result;
    }

    public static String collectThrow(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return writer.toString();
    }
}
