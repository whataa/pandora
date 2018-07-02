package tech.linjiang.pandora.inspector.treenode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 13/06/2018.
 */

public class TreeView extends RelativeLayout {
    private static final String TAG = "TreeNodeView";

    public TreeView(Context context) {
        super(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
        algorithm = new Algorithm(nodeHorizontalMargin, nodeVerticalMargin);
        setWillNotDraw(false);
        setBackgroundColor(0xffffffff);
        initChildButton();
        initBitmapCache();
        scaleDetector = new ScaleGestureDetector(context, listener);
        gestureDetector = new GestureDetector(context, gestureListener);
        scroller = new Scroller(context);
    }

    private void initChildButton() {
        int padding = ViewKnife.dip2px(8);

        ImageView toggle = new ImageView(getContext());
        toggle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        toggle.setImageResource(R.drawable.pd_rotate);
        toggle.setPadding(padding, padding, padding, padding);
        LayoutParams toggleParam = new LayoutParams(ViewKnife.dip2px(50), ViewKnife.dip2px(50));
        toggleParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        toggleParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(toggle, toggleParam);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDirection();
            }
        });

        ImageView reset = new ImageView(getContext());
        reset.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        reset.setImageResource(R.drawable.pd_refresh);
        toggle.setPadding(padding, padding, padding, padding);
        LayoutParams resetParam = new LayoutParams(ViewKnife.dip2px(50), ViewKnife.dip2px(50));
        resetParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        resetParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        resetParam.rightMargin = ViewKnife.dip2px(50);
        addView(reset, resetParam);
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInitPosition();
                invalidate();
            }
        });
    }

    private void initBitmapCache() {
        Rect rect = new Rect(0, 0, nodeWidth - nodeShadow, nodeHeight - nodeShadow);
        bitmapNodeCache = Bitmap.createBitmap(nodeWidth, nodeHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setMaskFilter(new BlurMaskFilter(nodeShadow, BlurMaskFilter.Blur.OUTER));
        Canvas bitmapCanvas = new Canvas(bitmapNodeCache);
        bitmapCanvas.drawRect(rect, paint);
        paint.setColor(Color.WHITE);
        paint.setMaskFilter(null);
        bitmapCanvas.drawRect(rect, paint);
        paint.setStrokeWidth(ViewKnife.dip2px(1));
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        bitmapCanvas.drawRect(rect, paint);
    }

    public void setRootView(View rootView) {
        rootNode = ViewNode.create(rootView, nodeWidth, nodeHeight);
        algorithm.calc(rootNode);
    }

    private Paint linePaint = new Paint() {
        {
            setColor(Color.BLUE);
            setStyle(Style.STROKE);
            setStrokeWidth(ViewKnife.dip2px(1));
        }
    };
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int touchSlop;
    private Algorithm algorithm;
    private ViewNode rootNode;

    private int nodeShadow = ViewKnife.dip2px(4);
    private int nodeHeight = ViewKnife.dip2px(40) + nodeShadow;
    private int nodeWidth = ViewKnife.dip2px(130) + nodeShadow;
    private int nodeHorizontalMargin = ViewKnife.dip2px(16);
    private int nodeVerticalMargin = ViewKnife.dip2px(32);

    private float tmpCanvasLeft, tmpCanvasTop, tmpCanvasRight, tmpCanvasBottom;
    private Path tmpPath = new Path();
    private float[] tmpXy = new float[2];
    private Bitmap bitmapNodeCache;
    private float translateDx, translateDy;
    private float downX, downY;
    private float lastX, lastY;
    /**
     * true is H, false is V
     * <p>
     * We use a rotation and panning canvas to simulate a LANDSCAPE effect
     */
    private boolean direction = false;
    // true: dragging
    private boolean isDragging;
    // not allow move when scale
    private boolean isScaling;
    private final float[] scaleData = new float[]{1, 1, 0, 0};

    private void resetInitPosition() {
        scaleData[0] = scaleData[1] = 1;
        scaleData[2] = scaleData[3] = 0;
        if (direction) {
            translateDy = getCurVisualWidth() / 2 - nodeWidth / 2;
            translateDx = -getCurVisualHeight() / 4;
        } else {
            translateDx = getCurVisualWidth() / 2 - nodeWidth / 2;
            translateDy = getCurVisualHeight() / 4;
        }
    }

    private int getCurVisualWidth() {
        return direction ? getMeasuredHeight() : getMeasuredWidth();
    }

    private int getCurVisualHeight() {
        return direction ? getMeasuredWidth() : getMeasuredHeight();
    }

    /**
     * New x-axis relative to original
     *
     * @return
     */
    private int getCurAxisX() {
        return (int) (direction ? translateDy : translateDx);
    }

    /**
     * New y-axis relative to original
     *
     * @return
     */
    private int getCurAxisY() {
        return (int) (direction ? -translateDx : translateDy);
    }

    private OnClickListener clickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                cancelAllAnim();
                downX = event.getX();
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                isScaling = true;
                lastX = event.getX(0);
                lastY = event.getY(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    if (!isScaling) {
                        float dx = event.getX() - lastX;
                        float dy = event.getY() - lastY;
                        translateDx += dx;
                        translateDy += dy;
                        invalidate();
                    }
                } else {
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    if (dx * dx + dy * dy > touchSlop * touchSlop) {
                        isDragging = true;
                    }
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isScaling = false;
                int indexOfUpPointer = event.getActionIndex();
                if (indexOfUpPointer == 0) {
                    lastX = event.getX(1);
                    lastY = event.getY(1);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isDragging) {
                    handleClickEvent(event.getX(), event.getY());
                }
                isDragging = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener listener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleData[0] = detector.getScaleFactor() * scaleData[0];
            scaleData[0] = scaleData[1] = Math.max(0.3f, Math.min(scaleData[0], 2));

            // TODO: 15/06/2018
//            tmpXy[0] = detector.getFocusX();
//            tmpXy[1] = detector.getFocusY();
//            mapAxis(tmpXy);
//            scaleData[2] = tmpXy[0];
//            scaleData[3] = tmpXy[1];
            invalidate();
            return true;
        }
    };

    private GestureDetector.SimpleOnGestureListener gestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lastX = e2.getX();
            lastY = e2.getY();
            scroller.fling((int) e2.getX(), (int) e2.getY(),
                    (int) velocityX, (int) velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
            invalidate();
            return true;
        }
    };

    private void cancelAllAnim() {
        scroller.abortAnimation();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int curX = scroller.getCurrX();
            int curY = scroller.getCurrY();
            translateDx += curX - lastX;
            translateDy += curY - lastY;
            lastX = curX;
            lastY = curY;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resetInitPosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // viewSelf
        super.onDraw(canvas);
        canvas.save();
        // ① direction
        if (direction) {
            canvas.rotate(90);
            canvas.translate(0, -getCurVisualHeight());
        }
        canvas.save();
        // ② drag
        canvas.translate(getCurAxisX(), getCurAxisY());
        canvas.save();
        // ③ scale
        canvas.scale(scaleData[0], scaleData[1], scaleData[2], scaleData[3]);
        drawInternal(canvas);
        canvas.restore();
        canvas.restore();
        canvas.restore();
    }

    private Paint debugPaint = new Paint() {
        {
            setStrokeWidth(ViewKnife.dip2px(4));
            setStyle(Style.STROKE);
        }
    };
    private RectF tmpRectF = new RectF();

    private void drawInternal(Canvas canvas) {
        // Make the visual area unaffected by movements and zooming operations
        tmpCanvasLeft = -getCurAxisX() / scaleData[0];
        tmpCanvasTop = -getCurAxisY() / scaleData[0];
        tmpCanvasRight = (-getCurAxisX() + getCurVisualWidth()) / scaleData[0];
        tmpCanvasBottom = (-getCurAxisY() + getCurVisualHeight()) / scaleData[0];

//        tmpRectF.set(tmpCanvasLeft, tmpCanvasTop, tmpCanvasRight, tmpCanvasBottom);
//        debugPaint.setColor(Color.RED);
//        canvas.drawRect(tmpRectF, debugPaint);

        drawNode(canvas, rootNode);
        drawLines(canvas, rootNode);
    }

    private void drawNode(Canvas canvas, ViewNode viewNode) {
        if (viewNode.getRect().right >= tmpCanvasLeft
                && viewNode.getRect().bottom >= tmpCanvasTop
                && viewNode.getRect().left <= tmpCanvasRight
                && viewNode.getRect().top <= tmpCanvasBottom) {
            if (bitmapNodeCache != null && !bitmapNodeCache.isRecycled()) {
                canvas.drawBitmap(bitmapNodeCache, null, viewNode.getRect(), null);
            }
            canvas.save();
            canvas.translate(viewNode.getRect().left,
                    viewNode.getRect().centerY() - viewNode.getLayout().getHeight() / 2);
            viewNode.getLayout().draw(canvas);
            canvas.restore();
        }
        if (viewNode.hasChildren()) {
            List<ViewNode> children = viewNode.getChildren();
            for (int i = 0; i < children.size(); i++) {
                drawNode(canvas, children.get(i));
            }
        }
    }

    private void drawLines(Canvas canvas, ViewNode treeNode) {
        if (treeNode.hasChildren()) {
            for (ViewNode child : treeNode.getChildren()) {
                drawLines(canvas, child);
            }
        }
        if (treeNode.hasParent()) {
            tmpPath.reset();
            int halfVMargin = nodeVerticalMargin >> 1;
            ViewNode parent = treeNode.getParent();
            tmpPath.moveTo(treeNode.getRect().centerX(), treeNode.getRect().top);
            tmpPath.lineTo(treeNode.getRect().centerX(), treeNode.getRect().top - halfVMargin);
            tmpPath.lineTo(parent.getRect().centerX(), treeNode.getRect().top - halfVMargin);
            canvas.drawPath(tmpPath, linePaint);

            if (parent.getRect().right >= tmpCanvasLeft
                    && parent.getRect().bottom >= tmpCanvasTop
                    && parent.getRect().left <= tmpCanvasRight
                    && parent.getRect().top <= tmpCanvasBottom) {
                tmpPath.reset();
                tmpPath.moveTo(parent.getRect().centerX(), treeNode.getRect().top - halfVMargin);
                tmpPath.lineTo(parent.getRect().centerX(), parent.getRect().bottom);
                canvas.drawPath(tmpPath, linePaint);
            }
        }
    }

    private void handleClickEvent(float x, float y) {
        tmpXy[0] = x;
        tmpXy[1] = y;
        mapAxis(tmpXy);
        ViewNode node = findViewNode(rootNode, (int) tmpXy[0], (int) tmpXy[1]);
        if (node != null) {
            if (clickListener != null) {
                clickListener.onClick(node.getView());
            }
        }
    }

    private ViewNode findViewNode(ViewNode node, int x, int y) {
        if (node.getRect().contains(x, y)) {
            return node;
        }
        if (node.hasChildren()) {
            for (ViewNode viewNode : node.getChildren()) {
                ViewNode forNode = findViewNode(viewNode, x, y);
                if (forNode != null) {
                    return forNode;
                }
            }
        }
        return null;
    }


    private void mapAxis(float originXy[]) {
        float touchX = originXy[0];
        float touchY = originXy[1];
        if (direction) {
            float tmp = touchX;
            touchX = touchY;
            touchY = -tmp + getCurVisualHeight();
        }
        touchX = (touchX - getCurAxisX()) / scaleData[0];
        touchY = (touchY - getCurAxisY()) / scaleData[0];
        originXy[0] = touchX;
        originXy[1] = touchY;
    }

    public void changeDirection() {
        direction = !direction;
        resetInitPosition();
        postInvalidate();
    }
}
