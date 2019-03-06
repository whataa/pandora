package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.cache.Crash;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.CrashItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/4.
 */

public class CrashFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("CRASH");
        getToolbar().getMenu().add(-1, 0, 0, "delete").setIcon(R.drawable.pd_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Crash.clear();
                getAdapter().clearItems();
                Utils.toast(R.string.pd_success);
                return true;
            }
        });
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof CrashItem) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(PARAM1, ((CrashItem)item).data.createTime);
                    bundle.putString(PARAM2, ((CrashItem)item).data.stack);
                    launch(CrashStackFragment.class, bundle);
                }
            }
        });
        loadData();
    }

    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<Crash>>() {
            @Override
            public List<Crash> doInBackground(Void[] params) {
                return Crash.query();
            }

            @Override
            public void onPostExecute(List<Crash> result) {
                hideLoading();
                List<BaseItem> data = new ArrayList<>(result.size());
                if (Utils.isNotEmpty(result)) {
                    data.add(new TitleItem(String.format(Locale.getDefault(), "%d LOGS", result.size())));
                    for (Crash crash : result) {
                        data.add(new CrashItem(crash));
                    }
                    getAdapter().setItems(data);
                } else {
                    showError(null);
                }
            }
        }).execute();
    }
}
