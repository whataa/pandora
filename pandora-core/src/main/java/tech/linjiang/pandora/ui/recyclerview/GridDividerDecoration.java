package tech.linjiang.pandora.ui.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by linjiang on 03/06/2018.
 */
public class GridDividerDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int mThickness;
    private boolean needHorizontal;
    private boolean needVertical;
    private VisibilityProvider visibilityProvider;

    protected GridDividerDecoration(int thickness, @ColorInt int color) {
        mThickness = thickness;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public static class Builder {
        private int mThickness;
        private int color = Color.TRANSPARENT;
        private boolean needHorizontal = true;
        private boolean needVertical = true;
        private VisibilityProvider visibilityProvider;

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setThickness(int mThickness) {
            this.mThickness = mThickness;
            return this;
        }

        public Builder needHorizontal(boolean needHorizontal) {
            this.needHorizontal = needHorizontal;
            return this;
        }

        public Builder needVertical(boolean needVertical) {
            this.needVertical = needVertical;
            return this;
        }

        public Builder visibilityProvider(VisibilityProvider visibilityProvider) {
            this.visibilityProvider = visibilityProvider;
            return this;
        }

        public GridDividerDecoration build() {
            GridDividerDecoration decoration = new GridDividerDecoration(mThickness, color);
            decoration.needHorizontal = needHorizontal;
            decoration.needVertical = needVertical;
            decoration.visibilityProvider = visibilityProvider;
            return decoration;
        }
    }

    public interface VisibilityProvider {

        boolean shouldHideDivider(int childPosition, int groupIndex);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        boolean isLastRow = isLastRow(parent, itemPosition, spanCount, childCount);
        boolean isLastColumn = isLastColumn(parent, itemPosition, spanCount, childCount);

        int left;
        int right;
        int bottom;
        int eachWidth = (spanCount - 1) * mThickness / spanCount;
        int dl = mThickness - eachWidth;

        left = itemPosition % spanCount * dl;
        right = eachWidth - left;
        bottom = mThickness;
        if (isLastRow) {
            bottom = 0;
        }
        if (!needVertical) {
            left = right = 0;
        } else {
            if (visibilityProvider != null) {
                int childPosition = parent.getChildAdapterPosition(view);
                int groupIndex = getGroupIndex(childPosition, parent);
                if (visibilityProvider.shouldHideDivider(childPosition, groupIndex)) {
                    left = right = 0;
                }
            }
        }
        bottom = needHorizontal ? bottom : 0;
        outRect.set(left, 0, right, bottom);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        draw(c, parent);
    }

    //绘制横向 item 分割线
    private void draw(Canvas canvas, RecyclerView parent) {
        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            //画水平分隔线
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mThickness;
            if (mPaint != null) {
                if (needHorizontal) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
            //画垂直分割线
            top = child.getTop();
            bottom = child.getBottom() + mThickness;
            left = child.getRight() + layoutParams.rightMargin;
            right = left + mThickness;
            if (mPaint != null) {
                if (needVertical) {
                    if (visibilityProvider != null) {
                        int childPosition = parent.getChildAdapterPosition(child);
                        int groupIndex = getGroupIndex(childPosition, parent);
                        if (visibilityProvider.shouldHideDivider(childPosition, groupIndex)) {
                            continue;
                        }
                    }
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            } else {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    private int getGroupIndex(int position, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            return spanSizeLookup.getSpanGroupIndex(position, spanCount);
        }

        return position;
    }
}