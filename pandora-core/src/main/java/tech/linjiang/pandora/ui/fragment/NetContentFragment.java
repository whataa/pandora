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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.network.CacheDbHelper;
import tech.linjiang.pandora.network.model.Content;
import tech.linjiang.pandora.ui.connector.SimpleOnActionExpandListener;
import tech.linjiang.pandora.ui.connector.SimpleOnQueryTextListener;
import tech.linjiang.pandora.ui.item.ContentItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.FormatUtil;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/24.
 */

public class NetContentFragment extends BaseListFragment {

    private boolean showResponse;
    private long id;
    private String contentType;
    private String originContent;
    private String filter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showResponse = getArguments().getBoolean(PARAM1, true);
        id = getArguments().getLong(PARAM2);
        contentType = getArguments().getString(PARAM3);
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
    protected boolean needDefaultDivider() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Content");
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
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_1, 0, "copy");
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_2, 1, "search")
                .setActionView(new SearchView(getContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_3, 2, "share");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.pd_menu_id_1) {
                    Utils.copy2ClipBoard(originContent);
                } else if (item.getItemId() == R.id.pd_menu_id_3) {
                    Utils.shareText(originContent);
                }
                return true;
            }
        });
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.pd_menu_id_2);
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
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
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
                String result;
                if (showResponse) {
                    result = content.responseBody;
                } else {
                    result = content.requestBody;
                }
                try {
                    return URLDecoder.decode(result, "utf-8");
                } catch (Exception e) {
                    return result;
                }
            }

            @Override
            public void onPostExecute(String result) {
                hideLoading();
                originContent = result;
                handlePlainText(result);
            }
        }).execute();
    }

    private void handlePlainText(final String content) {
        new SimpleTask<>(new SimpleTask.Callback<String, List<String>>() {
            @Override
            public List<String> doInBackground(String[] params) {
                if (TextUtils.isEmpty(params[0])) {
                    return null;
                }
                if (TextUtils.isEmpty(contentType)) {
                    return Collections.singletonList(params[0]);
                }
                if (contentType.toLowerCase().contains("json")) {
                    if (Pandora.get().getInterceptor().getJsonFormatter() != null) {
                        try {
                            String formatResult = Pandora.get()
                                    .getInterceptor().getJsonFormatter().format(params[0]);
                            if (!TextUtils.isEmpty(formatResult)) {
                                params[0] = formatResult;
                            }
                        } catch (Exception e) {// may not be json
                            e.printStackTrace();
                            return Collections.singletonList(params[0]);
                        }
                    }
                    return FormatUtil.printJson(params[0]);
                } else if (contentType.toLowerCase().contains("xml")) {
                    return FormatUtil.printXml(params[0]);
                } else {
                    return Collections.singletonList(params[0]);
                }
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
        }).execute(content);
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
                    BaseItem item = getAdapter().getItems().get(i);
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
