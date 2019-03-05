package tech.linjiang.pandora.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.inspector.BaseLineView;
import tech.linjiang.pandora.ui.GeneralDialog;

/**
 * Created by linjiang on 2019/3/5.
 */

public class MeasureFragment extends BaseFragment {

    @Override
    protected Toolbar onCreateToolbar() {
        return null;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        return new BaseLineView(getContext());
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        GeneralDialog.build(1)
                .title(R.string.pd_help_title)
                .message(R.string.pd_help_baseline)
                .positiveButton(R.string.pd_ok)
                .show(this);
    }


}
