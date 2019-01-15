package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.ui.item.RouteItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Navigate to...");
        List<String> activities = Utils.getActivities();
        List<BaseItem> data = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            data.add(new RouteItem(activities.get(i), callback));
        }
        getAdapter().setItems(data);
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

}
