package tech.linjiang.pandora.inspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/23.
 */
public class GridLineView extends View {

    public GridLineView(Context context) {
        super(context);
    }
    private boolean isOpen;
    private static final GridLineView gridLineView = new GridLineView(Utils.getContext());

    private static GridLineView open() {
        gridLineView.LINE_INTERVAL = ViewKnife.dip2px(Config.getUI_GRID_INTERVAL());
        try {
            WindowManager windowManager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.format = PixelFormat.TRANSLUCENT;
            windowManager.addView(gridLineView, params);
            gridLineView.isOpen = true;
        } catch (Throwable ignore) {
        }
        return gridLineView;
    }
    private static void close() {
        Utils.removeViewFromWindow(gridLineView);
        gridLineView.isOpen = false;
    }

    public static void toggle() {
        if (gridLineView.isOpen) {
            close();
        } else {
            open();
        }
    }

    public static void show() {
        gridLineView.setVisibility(VISIBLE);
    }

    public static void hide() {
        gridLineView.setVisibility(GONE);
    }

    private int LINE_INTERVAL;

    private Paint paint = new Paint() {
        {
            setAntiAlias(true);
            setColor(0x30000000);
            setStrokeWidth(1);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startX = 0;
        while (startX < getMeasuredWidth()) {
            canvas.drawLine(startX, 0, startX, getMeasuredHeight(), paint);
            startX = startX + LINE_INTERVAL;
        }

        int startY = 0;
        while (startY < getMeasuredHeight()) {
            canvas.drawLine(0, startY, getMeasuredWidth(), startY, paint);
            startY = startY + LINE_INTERVAL;
        }
    }
}
