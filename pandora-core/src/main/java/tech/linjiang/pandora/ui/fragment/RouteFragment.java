package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.connector.SimpleOnActionExpandListener;
import tech.linjiang.pandora.ui.connector.SimpleOnQueryTextListener;
import tech.linjiang.pandora.ui.item.RouteItem;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    private final ArrayList<RouteItem> routeItemArrayList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_navigate);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_2, 0, R.string.pd_name_search)
                .setActionView(new SearchView(requireContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        initSearchView();
        List<String> activities = Utils.getActivities();
        for (int i = 0; i < activities.size(); i++) {
            routeItemArrayList.add(new RouteItem(activities.get(i), callback));
        }
        getAdapter().setItems(routeItemArrayList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            go(data);
        }
    }

    private final RouteItem.Callback callback = new RouteItem.Callback() {
        @Override
        public void onClick(String simpleName, String clazz, boolean needParam) {
            if (needParam) {
                Bundle bundle = new Bundle();
                bundle.putString(PARAM1, simpleName);
                bundle.putString(PARAM2, clazz);
                launch(RouteParamFragment.class, bundle, CODE1);
                return;
            }
            try {
                Intent intent = new Intent(getContext(), Class.forName(clazz));
                go(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    private void go(Intent intent) {
        startActivity(intent);
        getActivity().finish();
    }

    private void initSearchView() {
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.pd_menu_id_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                filter(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getAdapter().setItems(routeItemArrayList);
                return true;
            }
        });
    }

    private void filter(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            getAdapter().setItems(routeItemArrayList);
            return;
        }
        ArrayList<RouteItem> newList = new ArrayList<>();
        for (int i = 0; i < routeItemArrayList.size(); i++) {
            RouteItem routeItem = routeItemArrayList.get(i);
            if (routeItem.data.toLowerCase().contains(keyWord.toLowerCase())) {
                newList.add(routeItem);
            }
        }
        getAdapter().setItems(newList);
    }
}
