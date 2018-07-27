package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.network.CacheDbHelper;
import tech.linjiang.pandora.network.model.Content;
import tech.linjiang.pandora.ui.connector.SimpleOnActionExpandListener;
import tech.linjiang.pandora.ui.connector.SimpleOnQueryTextListener;
import tech.linjiang.pandora.ui.item.ContentItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.JsonUtil;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/24.
 */

public class NetContentFragment extends BaseListFragment {

    private boolean showResponse;
    private long id;
    private String originContent;
    private String filter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showResponse = getArguments().getBoolean(PARAM1, true);
        id = getArguments().getLong(PARAM2);
    }

    @Override
    protected View getLayoutView() {
        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setFillViewport(true);
        scrollView.addView(super.getLayoutView(), params);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Content");
        getRecyclerView().removeItemDecoration(getRecyclerView().getItemDecorationAt(0));
        setSearchView();
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof ContentItem) {
                    Utils.copy2ClipBoard((String) item.data);
                }
            }
        });
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.cancelTask(filterTask);
        closeSoftInput();
    }

    private void setSearchView() {
        getToolbar().getMenu().findItem(R.id.menu_copy).setVisible(true);
        getToolbar().getMenu().findItem(R.id.menu_search).setVisible(true);
        getToolbar().getMenu().findItem(R.id.menu_share).setVisible(true);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_copy) {
                    Utils.copy2ClipBoard(originContent);
                } else if (item.getItemId() == R.id.menu_share) {
                    Utils.shareText(originContent);
                }
                return true;
            }
        });
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchView.setQueryHint(ViewKnife.getString(R.string.pd_net_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filter = newText;
                readyFilter();
                return true;
            }

        });
        menuItem.setOnActionExpandListener(new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filter = null;
                readyFilter();
                return true;
            }
        });
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
            @Override
            public String doInBackground(Void[] params) {
                Content content = CacheDbHelper.getContent(id);
                if (showResponse) {
                    return content.responseBody;
                } else {
                    return content.requestBody;
                }
            }

            @Override
            public void onPostExecute(String result) {
                hideLoading();
                originContent = result;
                tryFormatJson(result);
            }
        }).execute();
    }

    private void tryFormatJson(final String content) {
        new SimpleTask<>(new SimpleTask.Callback<Void, List<String>>() {
            @Override
            public List<String> doInBackground(Void[] params) {
                return JsonUtil.print(content);
            }

            @Override
            public void onPostExecute(List<String> result) {
                if (Utils.isNotEmpty(result)) {
                    List<BaseItem> data = new ArrayList<>(result.size());
                    for (int i = 0; i < result.size(); i++) {
                        data.add(new ContentItem(result.get(i)));
                    }
                    getAdapter().setItems(data);
                } else {
                    getToolbar().getMenu().clear();
                    showError(null);
                }
            }
        }).execute();
    }

    private void readyFilter() {
        Utils.cancelTask(filterTask);
        Utils.postDelayed(filterTask, 400);
    }

    private Runnable filterTask = new Runnable() {

        @Override
        public void run() {
            if (Utils.isNotEmpty(getAdapter().getItems())) {
                int targetPos = -1;
                for (int i = 0; i < getAdapter().getItems().size(); i++) {
                    BaseItem item  = getAdapter().getItems().get(i);
                    if (item instanceof ContentItem) {
                        ((ContentItem) item).setFocus(false);
                        if (!TextUtils.isEmpty(filter)) {
                            if (((String) item.data).toLowerCase().contains(filter.toLowerCase())) {
                                ((ContentItem) item).setFocus(true);
                                if (targetPos == -1) {
                                    targetPos = i;
                                }
                            }
                        }
                    }
                }
                if (targetPos == -1) {
                    targetPos = 0;
                }
                getAdapter().notifyDataSetChanged();
                getRecyclerView().scrollToPosition(targetPos);
            }
        }
    };
}
