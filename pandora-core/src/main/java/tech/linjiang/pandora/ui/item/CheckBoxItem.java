package tech.linjiang.pandora.ui.item;

import androidx.appcompat.widget.AppCompatCheckBox;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2018/7/24.
 */

public class CheckBoxItem extends BaseItem<Boolean> {

    private String title;

    public CheckBoxItem(String title, Boolean data) {
        super(data);
        this.title = title;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, Boolean data) {
        ((AppCompatCheckBox)pool.itemView).setChecked(data);
        ((AppCompatCheckBox)pool.itemView).setText(title);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_checkbox;
    }
}
