package tech.linjiang.pandora.ui.item;

import android.view.View;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 03/06/2018.
 */

public class NameItem extends BaseItem<String> {


    public NameItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool
                .setVisibility(R.id.common_item_info, View.GONE)
                .setVisibility(R.id.common_item_arrow, View.GONE)
                .setText(R.id.common_item_title, data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
