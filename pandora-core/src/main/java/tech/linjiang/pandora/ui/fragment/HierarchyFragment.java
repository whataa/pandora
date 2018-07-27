package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.ui.item.HierarchyItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2018/7/26.
 */

public class HierarchyFragment extends BaseListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Hierarchy");
        getRecyclerView().removeItemDecoration(getRecyclerView().getItemDecorationAt(0));
        List<BaseItem> data = new ArrayList<>();
        data.add(new HierarchyItem(Pandora.get().getViewRoot(), 0));
        getAdapter().setItems(data);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                HierarchyItem hierarchyItem = (HierarchyItem) item;
                if (hierarchyItem.isGroup() && hierarchyItem.getChildCount() > 0) {
                    if (!hierarchyItem.isExpand) {
                        List<HierarchyItem> expands = hierarchyItem.assembleChildren();
                        insertItems(expands, position+1);
                    } else {
                        List<HierarchyItem> expands = getAllExpandItems(hierarchyItem, position + 1);
                        removeItems(expands);
                    }
                    hierarchyItem.toggleIcon();
                }
            }
        });
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
}
