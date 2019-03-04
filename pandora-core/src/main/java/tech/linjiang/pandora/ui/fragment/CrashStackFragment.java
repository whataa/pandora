package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import tech.linjiang.pandora.ui.item.ExceptionItem;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/4.
 */

public class CrashStackFragment extends BaseListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long time = getArguments().getLong(PARAM1);
        final String stack = getArguments().getString(PARAM2);

        getToolbar().setTitle(Utils.millis2String(time, Utils.NO_MILLIS));
        getToolbar().getMenu().add(-1, 0, 0, "copy");
        getToolbar().getMenu().add(-1, 0, 1, "share to");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(stack);
                } else if (item.getOrder() == 1) {
                    Utils.shareText(stack);
                }
                return true;
            }
        });
        getAdapter().insertItem(new ExceptionItem(stack));
    }
}
