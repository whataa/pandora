package tech.linjiang.pandora.inspector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/26.
 */

public class CurInfoView extends AppCompatTextView {
    public CurInfoView(Context context) {
        super(context);
        setBackgroundColor(0x6f000000);
        setTextSize(14);
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setPadding(ViewKnife.dip2px(4), 0, ViewKnife.dip2px(4), 0);
    }
    private boolean isOpen;

    private static final CurInfoView curInfoView = new CurInfoView(Utils.getContext());

    private static void open() {
        try {
            WindowManager windowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.format = PixelFormat.TRANSLUCENT;
            params.gravity = Config.getUI_ACTIVITY_GRAVITY();
            windowManager.addView(curInfoView, params);
            curInfoView.isOpen = true;
        } catch (Throwable ignore) {
        }
    }

    private static void close() {
        Utils.removeViewFromWindow(curInfoView);
        curInfoView.isOpen = false;
    }

    public static void toggle() {
        if (curInfoView.isOpen) {
            close();
        } else {
            open();
        }
    }

    public static void show() {
        curInfoView.setVisibility(VISIBLE);
    }

    public static void hide() {
        curInfoView.setVisibility(GONE);
    }

    private static CharSequence lastInfo;
    public static void updateText(CharSequence value) {
        if (!TextUtils.isEmpty(value)) {
            lastInfo = curInfoView.getText();
        } else {
            value = lastInfo;
        }
        curInfoView.setText(value);
    }
}
