package tech.linjiang.pandora.ui.fragment;

import android.view.View;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.inspector.treenode.TreeView;

/**
 * Created by linjiang on 2018/7/26.
 */

public class HierarchyFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        TreeView treeNodeView = new TreeView(getContext());
        treeNodeView.setRootView(Pandora.get().getViewRoot());
        return treeNodeView;
    }
}
