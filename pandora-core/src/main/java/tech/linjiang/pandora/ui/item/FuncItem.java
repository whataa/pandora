package tech.linjiang.pandora.ui.item;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2019/3/4.
 */

public class FuncItem extends BaseItem<String> {

    private int icon;
    public FuncItem(int icon, String data) {
        super(data);
        this.icon = icon;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.setImageResource(R.id.icon, icon).setText(R.id.title, data);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_func;
    }
}
