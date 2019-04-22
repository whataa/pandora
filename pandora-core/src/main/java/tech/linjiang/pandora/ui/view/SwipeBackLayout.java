package tech.linjiang.pandora.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import tech.linjiang.pandora.core.R;

/**
 * Created by linjiang on 31/05/2018.
 */
public class SwipeBackLayout extends FrameLayout {

    public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;
    private static final int FULL_ALPHA = 255;
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.4f;
    private static final int OVER_SCROLL_DISTANCE = 10;


    private ViewDragHelper mHelper;

    private float mScrollPercent;
    private float mScrimOpacity;


    private Drawable mShadow;
    private Rect mTmpRect = new Rect();

    private int mEdgeFlag = EDGE_LEFT;
    private boolean mEnable = true;
    private int orientation;


    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mShadow = getResources().getDrawable(R.drawable.pd_shadow_left);
        mHelper.setEdgeTrackingEnabled(EDGE_LEFT);
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean isDrawView = child == getChildAt(0);
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (isDrawView && mScrimOpacity > 0 && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
        }
        return drawChild;
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if ((orientation & EDGE_LEFT) != 0) {
            mShadow.setBounds(childRect.left - mShadow.getIntrinsicWidth(),
                    childRect.top, childRect.left, childRect.bottom);
            mShadow.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadow.draw(canvas);
        }
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mScrimOpacity >= 0) {
            if (mHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }


    public void attach(DismissCallback callback, View view) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        addView(view);
        this.callback = callback;
    }


    public void enableGesture(boolean enable) {
        mEnable = enable;
    }

    class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean dragEnable = mHelper.isEdgeTouched(mEdgeFlag, pointerId);
            if (dragEnable) {
                if (mHelper.isEdgeTouched(EDGE_LEFT, pointerId)) {
                    orientation = EDGE_LEFT;
                }

            }
            return dragEnable;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if ((orientation & EDGE_LEFT) != 0) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            }
            return ret;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if ((orientation & EDGE_LEFT) != 0) {
                mScrollPercent = Math.abs((float) left / (getWidth() + mShadow.getIntrinsicWidth()));
            }
            invalidate();

            if (mScrollPercent > 1) {
                if (callback != null) {
                    callback.onDismiss();
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();

            int left = 0, top = 0;
            if ((orientation & EDGE_LEFT) != 0) {
                left = xvel > 0 || xvel == 0 && mScrollPercent > DEFAULT_SCROLL_THRESHOLD ? (childWidth
                        + mShadow.getIntrinsicWidth() + OVER_SCROLL_DISTANCE) : 0;
            }

            mHelper.settleCapturedViewAt(left, top);
            invalidate();
        }


        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            if ((mEdgeFlag & edgeFlags) != 0) {
                orientation = edgeFlags;
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnable) return super.onInterceptTouchEvent(ev);
        return mHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable) return super.onTouchEvent(event);
        mHelper.processTouchEvent(event);
        return true;
    }

    private DismissCallback callback;

    public interface DismissCallback {
        void onDismiss();
    }
}