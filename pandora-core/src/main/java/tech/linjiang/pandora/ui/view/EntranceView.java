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
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 29/05/2018.
 */

public class EntranceView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "EntranceView";

    private WindowManager windowManager;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private float lastY;
    private View entranceWrapper, inspectWrapper;
    private OnClickListener clickListener;

    public EntranceView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
        entranceWrapper = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_entrance, null);
        inspectWrapper = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_ui_inspect, null);
        entranceWrapper.findViewById(R.id.entrance_network).setOnClickListener(this);
        entranceWrapper.findViewById(R.id.entrance_sandbox).setOnClickListener(this);
        entranceWrapper.findViewById(R.id.entrance_ui).setOnClickListener(new OnClickListener() {
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
        inspectWrapper.findViewById(R.id.ui_hierarchy).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_select).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_info).setOnClickListener(this);
        inspectWrapper.findViewById(R.id.ui_baseline).setOnClickListener(this);
    }

    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        close();
        entranceWrapper.setVisibility(VISIBLE);
        inspectWrapper.setVisibility(GONE);
        addView(entranceWrapper);
        addView(inspectWrapper);
        try {
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
            windowManager.addView(this, params);
            isOpen = true;
        } catch (Throwable ignore) {
        }
    }

    public void close() {
        removeView(entranceWrapper);
        removeView(inspectWrapper);
        try {
            windowManager.removeView(this);
            isOpen = false;
        } catch (Throwable ignore) {
        }
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.y += event.getRawY() - lastY;
                    params.y = Math.max(0, params.y);
                    windowManager.updateViewLayout(EntranceView.this, params);
                    lastY = event.getRawY();
                    break;
            }
            return true;
        }
    };

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
}
