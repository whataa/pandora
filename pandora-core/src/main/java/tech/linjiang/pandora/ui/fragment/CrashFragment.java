package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.cache.Crash;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.GeneralDialog;
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
    private final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_crash);
        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_delete_key).setIcon(R.drawable.pd_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                GeneralDialog.build(CODE1)
                        .title(R.string.pd_help_title)
                        .message(R.string.pd_make_sure, true)
                        .positiveButton(R.string.pd_ok)
                        .negativeButton(R.string.pd_cancel)
                        .show(CrashFragment.this);
                return true;
            }
        });
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof CrashItem) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PARAM1, ((CrashItem)item).data);
                    launch(CrashStackFragment.class, bundle);
                }
            }
        });
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            Crash.clear();
            getAdapter().clearItems();
            Utils.toast(R.string.pd_success);
        }
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
                    String title = null;
                    for (Crash crash : result) {
                        String tmp = Utils.millis2String(crash.createTime, FORMAT);
                        if (!TextUtils.equals(title, tmp)) {
                            data.add(new TitleItem(tmp));
                            title = tmp;
                        }
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
