package tech.linjiang.pandora.ui.item;

import android.widget.TextView;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2018/7/22.
 */

public class ViewNameItem extends BaseItem<String> {
    public ViewNameItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        ((TextView)pool.itemView).setText(data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_view_name;
    }
}
