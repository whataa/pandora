package tech.linjiang.pandora.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.inspector.SelectableView;
import tech.linjiang.pandora.inspector.treenode.TreeView;
import tech.linjiang.pandora.ui.connector.Type;

/**
 * Created by linjiang on 15/06/2018.
 */

public class ViewFragment extends BaseFragment implements View.OnClickListener {

    public static BaseFragment newInstance(@Type int type) {
        if (type != Type.ATTR && type != Type.HIERARCHY) {
            throw new IllegalArgumentException("");
        }
        ViewFragment fragment = new ViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM1, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private int type;

    @Override
    protected boolean enableToolbar() {
        return false;
    }

    @Override
    protected boolean enableSwipeBack() {
        return type == Type.HIERARCHY;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        if (type == Type.ATTR) {
            SelectableView selectView = new SelectableView(getContext());
            selectView.tryGetFrontView(Pandora.get().getBottomActivity());
            selectView.setOnClickListener(this);
            return selectView;
        } else if (type == Type.HIERARCHY) {
            TreeView treeNodeView = new TreeView(getContext());
            treeNodeView.setRootView(Pandora.get().getViewRoot());
            treeNodeView.setOnClickListener(this);
            return treeNodeView;
        }
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(PARAM1);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == View.NO_ID) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                id = View.generateViewId();
            } else {
                id = R.id.pd_view_default_id;
            }
            v.setId(id);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM1, id);
        launch(ViewAttrFragment.class, bundle);
    }
}
