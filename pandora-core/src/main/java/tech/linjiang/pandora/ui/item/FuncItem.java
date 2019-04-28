package tech.linjiang.pandora.ui.item;

import android.graphics.Color;
import android.widget.ImageView;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2019/3/4.
 */

public class FuncItem extends BaseItem<String> {

    private int icon;
    private boolean isSelected;
    public FuncItem(int icon, String data) {
        super(data);
        this.icon = icon;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        pool.setImageResource(R.id.icon, icon).setText(R.id.title, data);
        ImageView imageView = pool.getView(R.id.icon);
        imageView.setColorFilter(isSelected ? 0xffEB346E : Color.TRANSPARENT);
        pool.setTextColor(R.id.title, isSelected ? 0xffEB346E : Color.BLACK);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_func;
    }
}
