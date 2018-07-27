package tech.linjiang.pandora.ui.item;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.TreeNodeLayout;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/26.
 */

public class HierarchyItem extends BaseItem<View> {


    public HierarchyItem(View data, int layerCount) {
        super(data);
        this.layerCount = layerCount;
    }

    public boolean isExpand;
    public int layerCount;


    public boolean isGroup() {
        return data instanceof ViewGroup;
    }

    public int getChildCount() {
        ViewGroup group = (ViewGroup) data;
        return group.getChildCount();
    }

    public List<HierarchyItem> assembleChildren() {
        ViewGroup group = (ViewGroup) data;
        List<HierarchyItem> result = new ArrayList<>();
        int newLayerCount = layerCount + 1;
        for (int i = 0; i < group.getChildCount(); i++) {
            result.add(new HierarchyItem(group.getChildAt(i), newLayerCount));
        }
        return result;
    }

    private boolean isVisible() {
        return data.getVisibility() == View.VISIBLE;
    }

    public void toggleIcon() {
        isExpand = !isExpand;
        if (itemView != null) {
            if (isGroup() && getChildCount() > 0) {
                ((TextView) itemView.findViewById(R.id.view_name_title)).setCompoundDrawablesWithIntrinsicBounds(
                        ViewKnife.getDrawable(isExpand ? R.drawable.pd_expand : R.drawable.pd_collapse), null, null, null);
            }
        }
    }

    private View itemView;

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, View data) {
        itemView = pool.itemView;
        pool.setText(R.id.view_name_title, viewToTitleString(data)).setTextColor(R.id.view_name_title, isVisible() ? 0xff000000 : 0xff959595)
                .setText(R.id.view_name_subtitle, viewToSummaryString(data)).setTextColor(R.id.view_name_subtitle, isVisible() ? 0xff000000 : 0xff959595);

        TreeNodeLayout layout = pool.getView(R.id.view_name_wrapper);
        layout.setLayerCount(layerCount, 5);
        if (isGroup() && getChildCount() > 0) {
            ((TextView) pool.getView(R.id.view_name_title)).setCompoundDrawablesWithIntrinsicBounds(
                    ViewKnife.getDrawable(isExpand ? R.drawable.pd_expand : R.drawable.pd_collapse), null, null, null);
        } else {
            ((TextView) pool.getView(R.id.view_name_title)).setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_hierachy;
    }


    private String viewToTitleString(View view) {
        if (isGroup()) {
            return view.getClass().getSimpleName() + " (" + getChildCount() + ")";
        } else {
            return view.getClass().getSimpleName();
        }
    }

    private String viewToSummaryString(View view) {
        return "{(" +
                view.getLeft() +
                ',' +
                view.getTop() +
                "), (" +
                view.getRight() +
                ',' +
                view.getBottom() +
                ")} " +
                ViewKnife.getIdString(view);
    }
}
