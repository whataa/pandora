package tech.linjiang.pandora.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.FuncItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2019/3/4.
 */

public class FuncView extends LinearLayout {

    private static final String TAG = "PanelView";

    private UniversalAdapter adapter;
    private float lastY;


    @SuppressLint("ClickableViewAccessibility")
    public FuncView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.pd_shadow_131124);
        ImageView moveView = new ImageView(context);
        RecyclerView recyclerView = new RecyclerView(context);
        ImageView closeView = new ImageView(context);

        moveView.setImageResource(R.drawable.pd_drag);
        moveView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        moveView.setOnTouchListener(touchListener);
        closeView.setImageResource(R.drawable.pd_close);
        closeView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter = new UniversalAdapter());

        addView(moveView, new LayoutParams(
                ViewKnife.dip2px(24), ViewGroup.LayoutParams.MATCH_PARENT
        ));
        addView(recyclerView, new LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1
        ));
        addView(closeView, new LayoutParams(
                ViewKnife.dip2px(40), ViewGroup.LayoutParams.MATCH_PARENT
        ));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // drag + close + 5*func + 0.5*func
            maxWidth = ViewKnife.dip2px(64) + ViewKnife.dip2px(50) * 5 + ViewKnife.dip2px(24);
        } else {
            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(
                Math.min(MeasureSpec.getSize(widthMeasureSpec), maxWidth),
                MeasureSpec.getMode(widthMeasureSpec)
        ), heightMeasureSpec);
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
                    Utils.updateViewLayoutInWindow(FuncView.this, params);
                    lastY = event.getRawY();
                    Utils.cancelTask(task);
                    Utils.postDelayed(task, 200);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Config.setDragY(lastY);
        }
    };

    public void addItem(@DrawableRes int icon, String name) {
        adapter.insertItem(new FuncItem(icon, name));
    }

    public void addItem(@DrawableRes int icon, String name, int position) {
        position = Math.min(Math.max(position, 0), adapter.getItemCount());
        adapter.insertItem(new FuncItem(icon, name), position);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        adapter.setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                boolean selected = listener.onItemClick(position);
                ((FuncItem) item).setSelected(selected);
                adapter.notifyItemChanged(position);
            }
        });
    }

    public boolean open() {
        if (ViewCompat.isAttachedToWindow(this)) {
            return true;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = ViewKnife.dip2px(62);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = (int) Config.getDragY();
        return Utils.addViewToWindow(this, params);
    }

    public void close() {
        if (ViewCompat.isAttachedToWindow(this)) {
            Utils.removeViewFromWindow(this);
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public interface OnItemClickListener {
        boolean onItemClick(int index);
    }
}
