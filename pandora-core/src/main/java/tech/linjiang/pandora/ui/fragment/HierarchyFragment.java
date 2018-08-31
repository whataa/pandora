package tech.linjiang.pandora.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.HierarchyItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/26.
 */

public class HierarchyFragment extends BaseListFragment
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private boolean isExpand = true;
    private View targetView;
    private int sysLayerCount;
    private View rootView;

    @Override
    protected boolean needDefaultDivider() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        rootView = ViewKnife.tryGetTheFrontView(Pandora.get().getBottomActivity());
        if (!Config.getUI_IGNORE_SYS_LAYER()) {
            sysLayerCount = countSysLayers();
        } else {
            if (rootView != null) {
                rootView = rootView.findViewById(android.R.id.content);
            }
            sysLayerCount = 0;
        }
        targetView = findViewByDefaultTag();
        if (targetView != null) {
            // clear flag
            targetView.setTag(R.id.pd_view_tag_for_unique, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rootView = null;
        targetView = null;
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(Utils.getContext()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // let scroll smooth more
                        return 120f / displayMetrics.densityDpi;
                    }

                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        getToolbar().setTitle("Hierarchy");
        getToolbar().getMenu().findItem(R.id.menu_switch).setVisible(true);
        SwitchCompat switchCompat = ((SwitchCompat) getToolbar()
                .getMenu().findItem(R.id.menu_switch).getActionView());
        switchCompat.setChecked(isExpand);
        switchCompat.setOnCheckedChangeListener(this);

        getRecyclerView().setBackgroundColor(Color.WHITE);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                HierarchyItem hierarchyItem = (HierarchyItem) item;
                if (hierarchyItem.isGroup() && hierarchyItem.getChildCount() > 0) {
                    if (!hierarchyItem.isExpand) {
                        List<HierarchyItem> expands = hierarchyItem.assembleChildren();
                        insertItems(expands, position + 1);
                    } else {
                        List<HierarchyItem> expands = getAllExpandItems(hierarchyItem, position + 1);
                        removeItems(expands);
                    }
                    hierarchyItem.toggleIcon();
                }
            }
        });

        expandAllViews();
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        super.onViewEnterAnimEnd(container);
        if (targetView != null) {
            int scrollTargetIndex = -1;
            for (int i = 0; i < getAdapter().getItems().size(); i++) {
                HierarchyItem hierarchyItem = getAdapter().getItem(i);
                if (hierarchyItem.data == targetView) {
                    scrollTargetIndex = i;
                    break;
                }
            }
            if (scrollTargetIndex != -1) {
                getRecyclerView().smoothScrollToPosition(scrollTargetIndex);
            }
        }
    }

    private List<HierarchyItem> getAllExpandItems(HierarchyItem hierarchyItem, int pos) {
        List<HierarchyItem> result = new ArrayList<>();
        if (hierarchyItem.isExpand && hierarchyItem.getChildCount() > 0) {
            for (int i = pos; i < getAdapter().getItemCount(); i++) {
                HierarchyItem curItem = getAdapter().getItem(i);
                if (hierarchyItem.layerCount >= curItem.layerCount) {
                    break;
                }
                result.add(curItem);
                if (curItem.isGroup()) {
                    List<HierarchyItem> subChildren = getAllExpandItems(curItem, i + 1);
                    result.addAll(subChildren);
                    i += subChildren.size();
                }
            }
        }
        return result;
    }


    private void removeItems(List<HierarchyItem> data) {
        final List<BaseItem> tmpData = new ArrayList<>();
        for (int i = 0; i < getAdapter().getItemCount(); i++) {
            tmpData.add(getAdapter().getItem(i));
        }
        getAdapter().getItems().removeAll(data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getNewListSize() {
                return getAdapter().getItemCount();
            }

            @Override
            public int getOldListSize() {
                return tmpData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                HierarchyItem newHierarchyItem = getAdapter().getItem(newItemPosition);
                HierarchyItem oldHierarchyItem = (HierarchyItem) tmpData.get(oldItemPosition);
                return oldHierarchyItem.data == newHierarchyItem.data;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // NOTICE: position also needs be compared
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(getAdapter());
    }

    private void insertItems(List<HierarchyItem> data, int pos) {
        final List<BaseItem> tmpData = new ArrayList<>();
        for (int i = 0; i < getAdapter().getItemCount(); i++) {
            tmpData.add(getAdapter().getItem(i));
        }
        getAdapter().getItems().addAll(pos, data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getNewListSize() {
                return getAdapter().getItemCount();
            }

            @Override
            public int getOldListSize() {
                return tmpData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                HierarchyItem newHierarchyItem = getAdapter().getItem(newItemPosition);
                HierarchyItem oldHierarchyItem = (HierarchyItem) tmpData.get(oldItemPosition);
                return oldHierarchyItem.data == newHierarchyItem.data;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // NOTICE: position also needs be compared
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(getAdapter());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isExpand) {
            getAdapter().performClick(0);
        } else {
            expandAllViews();
        }
        isExpand = !isExpand;
    }

    private void expandAllViews() {
        List<BaseItem> data = new ArrayList<>();
        HierarchyItem rootItem = HierarchyItem.createRoot(rootView, this);
        rootItem.sysLayerCount = sysLayerCount;
        data.add(rootItem);
        assembleItems(data, rootItem);
        getAdapter().setItems(data);
    }

    private void assembleItems(List<BaseItem> container, HierarchyItem hierarchyItem) {
        if (hierarchyItem.data == targetView) {
            hierarchyItem.isTarget = true;
        }
        if (hierarchyItem.isGroup() && hierarchyItem.getChildCount() > 0) {
            hierarchyItem.isExpand = true;
            List<HierarchyItem> expands = hierarchyItem.assembleChildren();
            for (int i = 0; i < expands.size(); i++) {
                HierarchyItem childItem = expands.get(i);
                container.add(childItem);
                assembleItems(container, childItem);
            }
        }
    }

    private View findViewByDefaultTag() {
        return findViewByDefaultTag(rootView);
    }
    private View findViewByDefaultTag(View root) {
        if (root.getTag(R.id.pd_view_tag_for_unique) != null) {
            return root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = findViewByDefaultTag(parent.getChildAt(i));
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    private int countSysLayers() {
        View content = rootView.findViewById(android.R.id.content);
        int layer = 0;
        if (content != null) {
            View current = content;
            while (current.getParent() != null) {
                layer++;
                if (current.getParent() instanceof View) {
                    current = (View) current.getParent();
                } else {
                    break;
                }
            }
        }
        return layer;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        ((HierarchyItem)getAdapter().getItem(position)).data
                .setTag(R.id.pd_view_tag_for_unique, new Object());
        launch(ViewAttrFragment.class, null);
    }
}
