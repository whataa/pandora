package tech.linjiang.pandora.ui.item;

import android.text.TextUtils;
import android.view.View;

import tech.linjiang.pandora.cache.Crash;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 04/06/2018.
 */

public class CrashItem extends BaseItem<Crash> {

    public CrashItem(Crash data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, Crash data) {
        pool
                .setVisibility(R.id.common_item_arrow, View.VISIBLE)
                .setText(R.id.common_item_info, TextUtils.isEmpty(data.cause) ? data.type : data.cause)
                .setText(R.id.common_item_title, Utils.millis2String(data.createTime, Utils.NO_MILLIS));
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
