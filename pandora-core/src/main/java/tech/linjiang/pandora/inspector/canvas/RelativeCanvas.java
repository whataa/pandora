package tech.linjiang.pandora.inspector.canvas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import tech.linjiang.pandora.inspector.model.Element;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 11/06/2018.
 */

public class RelativeCanvas {

    private View container;

    public RelativeCanvas(View container) {
        this.container = container;
    }

    private final int cornerRadius = ViewKnife.dip2px(1.5f);
    private final int endPointSpace = ViewKnife.dip2px(2);
    private final int textBgFillingSpace = ViewKnife.dip2px(3);
    private final int textLineDistance = ViewKnife.dip2px(6);
    private Paint areaPaint = new Paint() {
        {
            setAntiAlias(true);
            setColor(Color.RED);
            setStyle(Style.STROKE);
            setStrokeWidth(ViewKnife.dip2px(1));
        }
    };
    private Paint textPaint = new Paint() {
        {
            setAntiAlias(true);
            setTextSize(ViewKnife.dip2px(10));
            setColor(Color.RED);
            setStyle(Style.FILL);
            setStrokeWidth(ViewKnife.dip2px(1));
            setFlags(FAKE_BOLD_TEXT_FLAG);
        }
    };
    private Paint cornerPaint = new Paint() {
        {
            setAntiAlias(true);
            setStrokeWidth(ViewKnife.dip2px(1));
        }
    };
    private RectF tmpRectF = new RectF();

    private int getMeasuredWidth() {
        return container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return container.getMeasuredHeight();
    }

    public void draw(Canvas canvas, Element element1, Element element2) {
        if (element1 != null && element2 != null) {
            canvas.save();
            Rect firstRect = element1.getRect();
            Rect secondRect = element2.getRect();
            if (secondRect.top > firstRect.bottom) {
                int x = secondRect.left + secondRect.width() / 2;
                drawLineWithText(canvas, x, firstRect.bottom, x, secondRect.top);
            }

            if (firstRect.top > secondRect.bottom) {
                int x = secondRect.left + secondRect.width() / 2;
                drawLineWithText(canvas, x, secondRect.bottom, x, firstRect.top);
            }

            if (secondRect.left > firstRect.right) {
                int y = secondRect.top + secondRect.height() / 2;
                drawLineWithText(canvas, secondRect.left, y, firstRect.right, y);
            }

            if (firstRect.left > secondRect.right) {
                int y = secondRect.top + secondRect.height() / 2;
                drawLineWithText(canvas, secondRect.right, y, firstRect.left, y);
            }
            drawNestedAreaLine(canvas, firstRect, secondRect);
            drawNestedAreaLine(canvas, secondRect, firstRect);
            canvas.restore();
        }
    }

    private void drawLineWithText(Canvas canvas, int startX, int startY, int endX, int endY) {
        if (startX == endX && startY == endY) {
            return;
        }
        if (startX > endX) {
            int tempX = startX;
            startX = endX;
            endX = tempX;
        }
        if (startY > endY) {
            int tempY = startY;
            startY = endY;
            endY = tempY;
        }

        if (startX == endX) {
            drawLineWithEndPoint(canvas, startX, startY + endPointSpace, endX, endY - endPointSpace);
            String text = ViewKnife.px2dip(endY - startY) + "dp";
            drawText(canvas,
                    text,
                    startX + textLineDistance,
                    startY + (endY - startY) / 2 + ViewKnife.getTextHeight(textPaint, text) / 2);
        } else if (startY == endY) {
            drawLineWithEndPoint(canvas, startX + endPointSpace, startY, endX - endPointSpace, endY);
            String text = ViewKnife.px2dip(endX - startX) + "dp";
            drawText(canvas,
                    text,
                    startX + (endX - startX) / 2 - ViewKnife.getTextWidth(textPaint, text) / 2,
                    startY - textLineDistance);
        }
    }

    private void drawLineWithEndPoint(Canvas canvas, int startX, int startY, int endX, int endY) {
        canvas.drawLine(startX, startY, endX, endY, areaPaint);
        if (startX == endX) {
            canvas.drawLine(startX - endPointSpace, startY, endX + endPointSpace, startY, areaPaint);
            canvas.drawLine(startX - endPointSpace, endY, endX + endPointSpace, endY, areaPaint);
        } else if (startY == endY) {
            canvas.drawLine(startX, startY - endPointSpace, startX, endY + endPointSpace, areaPaint);
            canvas.drawLine(endX, startY - endPointSpace, endX, endY + endPointSpace, areaPaint);
        }
    }

    private void drawText(Canvas canvas, String text, float x, float y) {
        float left = x - textBgFillingSpace;
        float top = y - ViewKnife.getTextHeight(textPaint, text);
        float right = x + ViewKnife.getTextWidth(textPaint, text) + textBgFillingSpace;
        float bottom = y + textBgFillingSpace;
        // ensure text in screen bound
        if (left < 0) {
            right -= left;
            left = 0;
        }
        if (top < 0) {
            bottom -= top;
            top = 0;
        }
        if (bottom > getMeasuredHeight()) {
            float diff = top - bottom;
            bottom = getMeasuredHeight();
            top = bottom + diff;
        }
        if (right > getMeasuredWidth()) {
            float diff = left - right;
            right = getMeasuredWidth();
            left = right + diff;
        }
        cornerPaint.setColor(Color.WHITE);
        cornerPaint.setStyle(Paint.Style.FILL);
        tmpRectF.set(left, top, right, bottom);
        canvas.drawRoundRect(tmpRectF, cornerRadius, cornerRadius, cornerPaint);
        canvas.drawText(text, left + textBgFillingSpace, bottom - textBgFillingSpace, textPaint);
    }

    private void drawNestedAreaLine(Canvas canvas, Rect firstRect, Rect secondRect) {
        if (secondRect.left >= firstRect.left && secondRect.right <= firstRect.right
                && secondRect.top >= firstRect.top && secondRect.bottom <= firstRect.bottom) {

            drawLineWithText(canvas, secondRect.left, secondRect.top + secondRect.height() / 2,
                    firstRect.left, secondRect.top + secondRect.height() / 2);

            drawLineWithText(canvas, secondRect.right, secondRect.top + secondRect.height() / 2,
                    firstRect.right, secondRect.top + secondRect.height() / 2);

            drawLineWithText(canvas, secondRect.left + secondRect.width() / 2, secondRect.top,
                    secondRect.left + secondRect.width() / 2, firstRect.top);

            drawLineWithText(canvas, secondRect.left + secondRect.width() / 2, secondRect.bottom,
                    secondRect.left + secondRect.width() / 2, firstRect.bottom);
        }
    }
}
