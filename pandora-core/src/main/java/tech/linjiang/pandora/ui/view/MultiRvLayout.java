package tech.linjiang.pandora.ui.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by linjiang on 2018/7/23.
 * <p>
 * because behavior only support one scroll-view, we wrap multi scroll views just look like that.
 */

public class MultiRvLayout extends LinearLayout implements NestedScrollingChild {

    public MultiRvLayout(Context context) {
        super(context);
    }

    public MultiRvLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiRvLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return true;
    }
}
