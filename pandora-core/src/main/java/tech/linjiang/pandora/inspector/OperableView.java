package tech.linjiang.pandora.inspector;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tech.linjiang.pandora.inspector.canvas.ClickInfoCanvas;
import tech.linjiang.pandora.inspector.canvas.GridCanvas;
import tech.linjiang.pandora.inspector.canvas.RelativeCanvas;
import tech.linjiang.pandora.inspector.canvas.SelectCanvas;
import tech.linjiang.pandora.inspector.model.Element;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 11/06/2018.
 */
public class OperableView extends ElementHoldView {

    private static final String TAG = "OperableView";

    public OperableView(Context context) {
        super(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
        longPressTimeout = ViewConfiguration.getLongPressTimeout();
        tapTimeout = ViewConfiguration.getTapTimeout();
        selectCanvas = new SelectCanvas(this);
        relativeCanvas = new RelativeCanvas(this);
        gridCanvas = new GridCanvas(this);
        clickInfoCanvas = new ClickInfoCanvas(this);
    }

    private int searchCount = 0;
    // max selectable count
    private final int elementsNum = 2;
    private Element[] relativeElements = new Element[elementsNum];
    // the latest selected Element when tap.
    private Element targetElement;
    private SelectCanvas selectCanvas;
    private RelativeCanvas relativeCanvas;
    private GridCanvas gridCanvas;
    private ClickInfoCanvas clickInfoCanvas;
    private int touchSlop;
    private long longPressTimeout, tapTimeout;
    private float lastX, lastY;
    // (x, y) when DOWN
    private float downX, downY;
    @State
    private int state;
    private float alpha;
    // anim for indicating longPress action
    private ValueAnimator gridAnimator;

    private final Paint defPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {{
        setColor(Color.YELLOW);
        setStrokeWidth(ViewKnife.dip2px(2));
        setStyle(Style.STROKE);
    }};


    @IntDef({
            State.NONE,
            State.TOUCHING,
            State.PRESSING,
            State.DRAGGING,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NONE = 0x00;
        int PRESSING = 0x01;    // after tapTimeout and before longPressTimeout
        int TOUCHING = 0x02;    // trigger move before dragging
        int DRAGGING = 0x03;    // since long press
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastX = event.getX();
                downY = lastY = event.getY();
                tryStartCheckTask();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (state == State.DRAGGING) {
                    if (targetElement != null) {
                        float dx = event.getX() - lastX;
                        float dy = event.getY() - lastY;
                        targetElement.offset(dx, dy);
                        for (Element e : relativeElements) {
                            if (e != null) {
                                e.reset();
                            }
                        }
                        invalidate();
                    }
                } else if (state == State.TOUCHING) {
                    // do nothing
                } else {
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    if (dx * dx + dy * dy > touchSlop * touchSlop) {
                        if (state == State.PRESSING) {
                            Toast.makeText(getContext(), "CANCEL", Toast.LENGTH_SHORT).show();
                        }
                        state = State.TOUCHING;
                        cancelCheckTask();
                        invalidate();
                        Log.w(TAG, "onTouchEvent: change to State.TOUCHING");
                    }
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                cancelCheckTask();
                if (state == State.NONE) {
                    handleClick(event.getX(), event.getY());
                } else if (state == State.DRAGGING) {
                    resetAll();
                }
                state = State.NONE;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), defPaint);
        if (state == State.DRAGGING) {
            gridCanvas.draw(canvas, 1);
        } else if (state == State.PRESSING) {
            gridCanvas.draw(canvas, alpha);
        }
        selectCanvas.draw(canvas, relativeElements);
        relativeCanvas.draw(canvas, relativeElements[searchCount % elementsNum],
                relativeElements[Math.abs(searchCount - 1) % elementsNum]);

        clickInfoCanvas.draw(canvas);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelCheckTask();
        relativeElements = null;
    }

    private void cancelCheckTask() {
        removeCallbacks(longPressCheck);
        removeCallbacks(tapTimeoutCheck);
        if (gridAnimator != null) {
            gridAnimator.cancel();
            gridAnimator = null;
        }
    }

    private void tryStartCheckTask() {
        cancelCheckTask();
        if (targetElement != null) {
            postDelayed(longPressCheck, longPressTimeout);
            postDelayed(tapTimeoutCheck, tapTimeout);
        }
    }

    private Runnable longPressCheck = new Runnable() {
        @Override
        public void run() {
            state = State.DRAGGING;
            alpha = 1;
        }
    };
    private Runnable tapTimeoutCheck = new Runnable() {
        @Override
        public void run() {
            state = State.PRESSING;
            gridAnimator = ObjectAnimator.ofFloat(0, 1)
                    .setDuration(longPressTimeout - tapTimeout);
            gridAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    alpha = value;
                    invalidate();
                }
            });
            gridAnimator.start();
        }
    };


    private void handleClick(float x, float y) {
        final Element element = getTargetElement(x, y);
        handleElementSelected(element, true);
    }

    public boolean handleClick(View v) {
        final Element element = getTargetElement(v);
        handleElementSelected(element, false);
        invalidate();
        return element != null;
    }

    private void handleElementSelected(Element element, boolean cancelIfSelected) {
        targetElement = element;
        if (element != null) {
            boolean bothNull = true;
            for (int i = 0; i < relativeElements.length; i++) {
                if (relativeElements[i] != null) {
                    if (relativeElements[i] == element) {
                        if (cancelIfSelected) {
                            // cancel selected
                            relativeElements[i] = null;
                            searchCount = i;
                        }
                        if (clickListener != null) {
                            clickListener.onClick(element.getView());
                        }
                        return;
                    }
                    bothNull = false;
                }
            }
            if (bothNull) {
                // If only one is selected, show info
                clickInfoCanvas.setInfoElement(element);
            }
            relativeElements[searchCount % elementsNum] = element;
            searchCount++;
            if (clickListener != null) {
                clickListener.onClick(element.getView());
            }
        }
    }

    public boolean isSelectedEmpty() {
        boolean empty = true;
        for (int i = 0; i < elementsNum; i++) {
            if (relativeElements[i] != null) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    private OnClickListener clickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
    }

}
