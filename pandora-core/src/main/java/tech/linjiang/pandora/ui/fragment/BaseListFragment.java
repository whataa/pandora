package tech.linjiang.pandora.ui.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.MenuRecyclerView;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/22.
 */

public class BaseListFragment extends BaseFragment {
    @Override
    protected final int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        adapter = new UniversalAdapter();
        recyclerView = new MenuRecyclerView(getContext());
        recyclerView.setBackgroundColor(ViewKnife.getColor(R.color.pd_main_bg));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ViewKnife.getDrawable(R.drawable.pd_divider_horizontal));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private MenuRecyclerView recyclerView;
    private UniversalAdapter adapter;

    protected final MenuRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public final UniversalAdapter getAdapter() {
        return adapter;
    }
}
