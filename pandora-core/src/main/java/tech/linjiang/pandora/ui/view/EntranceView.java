package tech.linjiang.pandora.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 29/05/2018.
 */

public class EntranceView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "EntranceView";

    private float lastY;
    private OnClickListener clickListener;

    public EntranceView(Context context) {
        super(context);
        initSelf();
        inflate();
    }

    private void initSelf() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(ViewKnife.getDrawable(R.drawable.pd_shadow_131124));
        } else {
            setBackgroundDrawable(ViewKnife.getDrawable(R.drawable.pd_shadow_131124));
        }
        final ImageView dragView = new ImageView(getContext());
        dragView.setImageResource(R.drawable.pd_drag);
        dragView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        dragView.setOnTouchListener(touchListener);
        addView(dragView, new LayoutParams(ViewKnife.dip2px(24), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void inflate() {
        final View entranceWrapper = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_entrance, null);
        final View inspectWrapper = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_ui_inspect, null);
        entranceWrapper.findViewById(R.id.entrance_network).setOnClickListener(this);
        entranceWrapper.findViewById(R.id.entrance_sandbox).setOnClickListener(this);
        entranceWrapper.findViewById(R.id.ui_select).setOnClickListener(this);
        entranceWrapper.findViewById(R.id.entrance_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                entranceWrapper.setVisibility(GONE);
                inspectWrapper.setVisibility(VISIBLE);
            }
        });
        entranceWrapper.findViewById(R.id.entrance_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        inspectWrapper.findViewById(R.id.ui_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                entranceWrapper.setVisibility(VISIBLE);
                inspectWrapper.setVisibility(GONE);
            }
        });
        inspectWrapper.findViewById(R.id.entrance_config).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_hierarchy).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_grid).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_window).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_baseline).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_route).setOnClickListener(this);
        addView(entranceWrapper);
        addView(inspectWrapper);
    }

    public void enableNetwork(boolean use) {
        findViewById(R.id.entrance_network).setVisibility(use ? VISIBLE : GONE);
    }

    public void enableSandbox(boolean use) {
        findViewById(R.id.entrance_sandbox).setVisibility(use ? VISIBLE : GONE);
    }

    public void enableUiInspect(boolean use) {
        findViewById(R.id.ui_select).setVisibility(use ? VISIBLE : GONE);
    }

    private boolean isOpen;


    public static void open() {
        if (instance.isOpen) {
            return;
        }
        instance.getChildAt(1).setVisibility(VISIBLE);
        instance.getChildAt(2).setVisibility(GONE);
        try {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.format = PixelFormat.TRANSLUCENT;
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = 0;
            params.y = 0;
            instance.getWindowManager().addView(instance, params);
            instance.isOpen = true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void close() {
        if (!instance.isOpen) {
            return;
        }
        try {
            instance.getWindowManager().removeView(instance);
            instance.isOpen = false;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void hide() {
        instance.setVisibility(GONE);
    }

    public static void show() {
        boolean netEnable = Config.getCOMMON_NETWORK_SWITCH();
        boolean sbEnable = Config.getCOMMON_SANDBOX_SWITCH();
        boolean uiEnable = Config.getCOMMON_UI_SWITCH();
        instance.enableNetwork(netEnable);
        instance.enableSandbox(sbEnable);
        instance.enableUiInspect(uiEnable);
        instance.setVisibility(VISIBLE);
    }

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                    params.y += event.getRawY() - lastY;
                    params.y = Math.max(0, params.y);
                    getWindowManager().updateViewLayout(EntranceView.this, params);
                    lastY = event.getRawY();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    public static void setListener(@Nullable OnClickListener l) {
        instance.clickListener = l;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(v);
        }
    }

    private WindowManager getWindowManager() {
        return ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
    }

    private static final EntranceView instance = new EntranceView(Utils.getContext());
}
