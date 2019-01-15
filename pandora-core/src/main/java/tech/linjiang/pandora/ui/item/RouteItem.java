package tech.linjiang.pandora.ui.item;

import android.view.View;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteItem extends BaseItem<String> {
    private String simpleName;
    private Callback callback;

    public RouteItem(String data, Callback callback) {
        super(data);
        this.callback = callback;
        simpleName = data.substring(data.lastIndexOf(".") + 1);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, final String data) {
        pool.setText(R.id.common_item_title, simpleName)
                .setText(R.id.common_item_info, data);
        pool.getView(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClick(simpleName, data, false);
                }
            }
        });
        pool.getView(R.id.param).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClick(simpleName, data, true);
                }
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_route;
    }

    public interface Callback {
        void onClick(String simpleName, String clazz, boolean needParam);
    }
}
