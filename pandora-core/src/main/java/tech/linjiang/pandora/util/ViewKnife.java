package tech.linjiang.pandora.util;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

/**
 * @author linjiang
 *         2017/9/7
 */

public class ViewKnife {

    private static Resources getResouces() {
        return Utils.getContext().getResources();
    }

    public static int getColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResouces().getColor(color, Utils.getContext().getTheme());
        } else {
            return getResouces().getColor(color);
        }
    }

    public static float getDimen(@DimenRes int dimen) {
        return getResouces().getDimension(dimen);
    }

    public static String getString(@StringRes int res) {
        return getResouces().getString(res);
    }

    public static Drawable getDrawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(Utils.getContext(), res);
    }


    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static String px2dipStr(float pxValue) {
        return String.format(Locale.getDefault(), "%ddp", px2dip(pxValue));
    }

    public static void removeSelf(View view) {
        if (view != null && view.getParent() != null) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
    }

    public static float getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public static void setStatusBarColor(@NonNull Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    public static void transStatusBar(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = window.getDecorView();
            if (view != null) {
                view.setSystemUiVisibility(view.getSystemUiVisibility() | 1280);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static int getStatusHeight() {
        int height = 0;
        int resourceId = getResouces().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResouces().getDimensionPixelSize(resourceId);
            if (height > 0) {
                return height;
            }
        }
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int tmpHeight = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            height = getResouces().getDimensionPixelSize(tmpHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    public static String getIdString(View view) {
        StringBuilder out = new StringBuilder();
        int id = view.getId();
        if (id != View.NO_ID && !isViewIdGenerated(id)) {
            try {
                String pkgName;
                switch (id&0xff000000) {
                    case 0x7f000000:
                        pkgName="app";
                        break;
                    case 0x01000000:
                        pkgName="android";
                        break;
                    default:
                        pkgName = getResouces().getResourcePackageName(id);
                        break;
                }
                String typename = getResouces().getResourceTypeName(id);
                String entryName = getResouces().getResourceEntryName(id);
                out.append(pkgName);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryName);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                out.append(Integer.toHexString(id));
            }
        } else {
            out.append("NO_ID");
        }
        return out.toString();
    }

    // see View.java
    // generateViewId(), isViewIdGenerated()
    private static boolean isViewIdGenerated(int id) {
        return (id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0;
    }

    public static int formatGravity(String value) {
        int start = value.contains("start") ? Gravity.START : 0;
        int top = value.contains("top") ? Gravity.TOP : 0;
        int end = value.contains("end") ? Gravity.END : 0;
        int bottom = value.contains("bottom") ? Gravity.BOTTOM : 0;
        return start | top | end | bottom;
    }
    public static String parseGravity(int value) {
        String start = existGravity(value, Gravity.START) ? "start" : null;
        String top = existGravity(value, Gravity.TOP) ? "top" : null;
        String end = existGravity(value, Gravity.END) ? "end" : null;
        String bottom = existGravity(value, Gravity.BOTTOM) ? "bottom" : null;
        StringBuilder sb = new StringBuilder();
        sb.append(!TextUtils.isEmpty(start) ? start + "|" : "");
        sb.append(!TextUtils.isEmpty(top) ? top + "|" : "");
        sb.append(!TextUtils.isEmpty(end) ? end + "|" : "");
        sb.append(!TextUtils.isEmpty(bottom) ? bottom + "|" : "");
        String result = sb.toString();
        if (result.endsWith("|")) {
            result = result.substring(0, result.lastIndexOf("|"));
        }
        return result;
    }

    private static boolean existGravity(int value, int attr) {
        return (value & attr) == attr;
    }
}
