package tech.linjiang.pandora.ui.item;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 05/06/2018.
 */

public class KeyValueItem extends BaseItem<String[]> {
    public boolean isTitle;
    public boolean clickable;
    private String prefix;


    public KeyValueItem(String[] data) {
        super(data);
    }

    public KeyValueItem(String[] data, boolean isTitle) {
        super(data);
        this.isTitle = isTitle;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable, String prefix) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
        this.prefix = prefix;
    }


    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String[] data) {
        pool.setVisibility(R.id.item_prefix, TextUtils.isEmpty(prefix) ? View.GONE : View.VISIBLE);
        pool.setText(R.id.item_prefix, prefix);
        pool
                .setTextGravity(R.id.item_key, isTitle ? Gravity.CENTER : Gravity.CENTER_VERTICAL)
                .setText(R.id.item_key, data[0])
                .setTextGravity(R.id.item_value,
        isTitle ? Gravity.CENTER : Gravity.CENTER_VERTICAL)
                .setBackgroundColor(R.id.item_value,
        isTitle ? ViewKnife.getColor(R.color.pd_item_key) : Color.WHITE)
                .setText(R.id.item_value, data[1]);

        pool.getView(R.id.item_value).setVisibility(View.VISIBLE);
        pool.getView(R.id.item_edit).setVisibility(View.GONE);
        pool.setVisibility(R.id.item_arrow, clickable ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_key_value;
    }

}
