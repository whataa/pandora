package tech.linjiang.pandora.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/27.
 */

public class TreeNodeLayout extends LinearLayout {
    public TreeNodeLayout(Context context) {
        this(context, null);
    }

    public TreeNodeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreeNodeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    private int sysLayoutCount;
    private int layerCount;


    public void setLayerCount(int layerCount, int sysLayoutCount) {
        this.layerCount = layerCount;
        this.sysLayoutCount = sysLayoutCount;
        setPadding(interval * layerCount + ViewKnife.dip2px(2), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        invalidate();
    }

    private final int interval = ViewKnife.dip2px(8);
    private Paint paint = new Paint() {
        {
            setColor(Color.GRAY);
            setStyle(Style.FILL);
            setStrokeWidth(ViewKnife.dip2px(0.5f));
        }
    };

    private final int color0x = Color.GRAY;

    private final int color1x = 0xffC2D8D8;
    private final int color2x = 0xff7BC1C4;

    private final int color3x = 0xff70CACF;
    private final int color4x = 0xff90D1C1;
    private final int color5x = 0xff9CD251;
    private final int color6x = 0xffEDD269;
    private final int color7x = 0xffE1A167;
    private final int color8x = 0xffEC9178;

    private final int color9x = 0xffEA7855;
    private final int color10x = 0xff7A3923;

    private final int color11x = Color.BLACK;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1; i <= layerCount; i++) {
            if (i > sysLayoutCount) {
                if (i >= 11 + sysLayoutCount) {
                    paint.setColor(color11x);
                } else if (i >= 10 + sysLayoutCount) {
                    paint.setColor(color10x);
                } else if (i >= 9 + sysLayoutCount) {
                    paint.setColor(color9x);
                } else if (i >= 8 + sysLayoutCount) {
                    paint.setColor(color8x);
                } else if (i == 7 + sysLayoutCount) {
                    paint.setColor(color7x);
                } else if (i == 6 + sysLayoutCount) {
                    paint.setColor(color6x);
                } else if (i == 5 + sysLayoutCount) {
                    paint.setColor(color5x);
                } else if (i == 4 + sysLayoutCount) {
                    paint.setColor(color4x);
                } else if (i == 3 + sysLayoutCount) {
                    paint.setColor(color3x);
                } else if (i == 2 + sysLayoutCount) {
                    paint.setColor(color2x);
                } else if (i == 1 + sysLayoutCount) {
                    paint.setColor(color1x);
                }
                paint.setStrokeWidth(ViewKnife.dip2px(1));
            } else {
                paint.setStrokeWidth(ViewKnife.dip2px(0.5f));
                paint.setColor(color0x);
            }
            canvas.drawLine(i * interval, 0, i * interval, getMeasuredHeight(), paint);
        }
    }
}
