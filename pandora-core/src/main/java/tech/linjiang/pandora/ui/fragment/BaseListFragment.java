package tech.linjiang.pandora.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        recyclerView.setLayoutManager(onCreateLayoutManager());
        if (needDefaultDivider()) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            GradientDrawable horizontalDrawable = new GradientDrawable();
            horizontalDrawable.setColor(0xffE5E5E5);
            horizontalDrawable.setSize(0, 1);
            divider.setDrawable(horizontalDrawable);
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    protected boolean needDefaultDivider() {
        return true;
    }

    private MenuRecyclerView recyclerView;
    private UniversalAdapter adapter;

    protected final MenuRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public final UniversalAdapter getAdapter() {
        return adapter;
    }

    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }
}
