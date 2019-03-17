package tech.linjiang.pandora.inspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 08/06/2018.
 */

public class BaseLineView extends View {

    public BaseLineView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
    }

    private int touchSlop;
    // downEvent
    private float downX, downY;
    // touchEvent
    private float lastX, lastY;
    // clickEvent
    private float initX, initY;
    // before changed
    private float oldX, oldY;
    // moveStartEvent
    private float moveStartX, moveStartY;

    private int heightDP, widthDP;
    private int SCALE_LENGTH = ViewKnife.dip2px(4);
    private int SCALE_GAP = 5;//(dp)
    // scroll direction
    private @Direction
    int direction;

    @IntDef({
            Direction.NONE,
            Direction.HORIZONTAL,
            Direction.VERTICAL,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
        int NONE = 0x00;
        int HORIZONTAL = 0x01;
        int VERTICAL = 0x02;
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(ViewKnife.getColor(R.color.pd_red));
            setStyle(Style.FILL);
            setStrokeWidth(ViewKnife.dip2px(1));
        }
    };
    private Paint oldPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(ViewKnife.getColor(R.color.pd_red));
            setStyle(Style.STROKE);
            setStrokeWidth(ViewKnife.dip2px(0.5f));
            setPathEffect(new DashPathEffect(new float[]{ViewKnife.dip2px(3), ViewKnife.dip2px(3)}, 0));
        }
    };
    private Paint mutablePaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(ViewKnife.getColor(R.color.pd_blue));
            setStyle(Style.FILL);
            setStrokeWidth(ViewKnife.dip2px(2));
            setTextSize(ViewKnife.dip2px(12));
            setFlags(FAKE_BOLD_TEXT_FLAG);
        }
    };

    private final Paint defPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {{
        setColor(Color.YELLOW);
        setStrokeWidth(ViewKnife.dip2px(2));
        setStyle(Style.STROKE);
    }};

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        heightDP = ViewKnife.px2dip(getMeasuredHeight());
        widthDP = ViewKnife.px2dip(getMeasuredWidth());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastX = event.getX();
                downY = lastY = event.getY();
                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                lastX = event.getX();
                lastY = event.getY();
                float dx = lastX - downX;
                float dy = lastY - downY;
                if (direction == Direction.NONE) {
                    if (Math.abs(dx) > touchSlop) {
                        direction = Direction.HORIZONTAL;
                        moveStartX = lastX;
                        oldX = initX;
                        if (initY <= 0) {
                            initY = lastY;
                        }
                    } else if (Math.abs(dy) > touchSlop) {
                        direction = Direction.VERTICAL;
                        moveStartY = lastY;
                        oldY = initY;
                        if (initX <= 0) {
                            initX = lastX;
                        }
                    }
                }
                if (direction != Direction.NONE) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (direction == Direction.NONE) {
                    oldX = oldY = 0;
                    initX = event.getX();
                    initY = event.getY();
                } else {
                    if (direction == Direction.HORIZONTAL) {
                        oldX = initX;
                        initX += event.getX() - moveStartX;
                    } else if (direction == Direction.VERTICAL) {
                        oldY = initY;
                        initY += event.getY() - moveStartY;
                    }
                    direction = Direction.NONE;
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), defPaint);

        // init
        if (initY > 0) {
            canvas.drawLine(0, initY, getMeasuredWidth(), initY, paint);
        }
        if (initX > 0) {
            canvas.drawLine(initX, 0, initX, getMeasuredHeight(), paint);
        }
        // scale
        for (int i = 0; i < heightDP; i += SCALE_GAP) {
            canvas.drawLine(initX, ViewKnife.dip2px(i), initX + SCALE_LENGTH, ViewKnife.dip2px(i), paint);
        }
        for (int i = 0; i < widthDP; i += SCALE_GAP) {
            canvas.drawLine(ViewKnife.dip2px(i), initY, ViewKnife.dip2px(i), initY + SCALE_LENGTH, paint);
        }
        // scroll
        if (direction == Direction.HORIZONTAL) {
            canvas.drawLine(initX + lastX - moveStartX, 0, initX + lastX - moveStartX, getMeasuredHeight(), paint);
            float dis = lastX - moveStartX;
            canvas.drawLine(initX, initY, initX + dis, initY, mutablePaint);
            mutablePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(ViewKnife.px2dip(dis) + "dp", initX + dis / 2, initY - ViewKnife.dip2px(12), mutablePaint);
        } else if (direction == Direction.VERTICAL) {
            canvas.drawLine(0, initY + lastY - moveStartY, getMeasuredWidth(), initY + lastY - moveStartY, paint);
            float dis = lastY - moveStartY;
            canvas.drawLine(initX, initY, initX, initY + dis, mutablePaint);
            mutablePaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(ViewKnife.px2dip(dis) + "dp", initX + ViewKnife.dip2px(12), initY + dis / 2, mutablePaint);
        }
        // old
        if (oldX > 0) {
            canvas.drawLine(oldX, 0, oldX, getMeasuredHeight(), oldPaint);
        }
        if (oldY > 0) {
            canvas.drawLine(0, oldY, getMeasuredWidth(), oldY, oldPaint);
        }


    }

}
