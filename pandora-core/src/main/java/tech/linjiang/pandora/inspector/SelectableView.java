package tech.linjiang.pandora.inspector;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import tech.linjiang.pandora.inspector.canvas.ClickInfoCanvas;
import tech.linjiang.pandora.inspector.canvas.SelectCanvas;
import tech.linjiang.pandora.inspector.model.Element;

/**
 * Created by linjiang on 11/06/2018.
 */
public class SelectableView extends ElementHoldView {

    public SelectableView(Context context) {
        super(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
        selectCanvas = new SelectCanvas(this);
        clickInfoCanvas = new ClickInfoCanvas(this, true);
    }


    @Override
    protected String getViewHint() {
        return "① singleTap to select views." +
                "\n② tap the selected view again to see attributes.";
    }

    private Element targetElement;
    private SelectCanvas selectCanvas;
    private ClickInfoCanvas clickInfoCanvas;
    private int touchSlop;
    // (x, y) when DOWN
    private float downX, downY;
    private boolean isTouching;


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isTouching) {
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    if (dx * dx + dy * dy > touchSlop * touchSlop) {
                        isTouching = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isTouching) {
                    handleClick(event.getX(), event.getY());
                }
                isTouching = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        selectCanvas.draw(canvas, targetElement);
        clickInfoCanvas.draw(canvas);
    }




    private void handleClick(float x, float y) {
        // view's attribute maybe has changed
        resetAll();
        final Element element = getTargetElement(x, y);
        if (element != null) {
            if (targetElement == element) {
                if (clickListener != null) {
                    clickListener.onClick(element.getView());
                }
                invalidate();
                return;
            }
            targetElement = element;
            clickInfoCanvas.setInfoElement(element);
        }
        invalidate();
    }


    private OnClickListener clickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
    }
}
