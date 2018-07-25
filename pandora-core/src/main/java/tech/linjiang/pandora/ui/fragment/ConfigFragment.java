package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.connector.EditCallback;
import tech.linjiang.pandora.ui.item.CheckBoxItem;
import tech.linjiang.pandora.ui.item.NameArrowItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2018/7/24.
 */

public class ConfigFragment extends BaseListFragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Setting");
        getToolbar().inflateMenu(R.menu.pd_menu_config);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Config.reset();
                refreshData();
                Utils.toast(R.string.pd_success);
                return false;
            }
        });
        refreshData();


        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (!(item instanceof TitleItem)) {
                    @Config.Type int type = (int) item.getTag();
                    Log.d(TAG, "onItemClick: " + type);
                    switch (type) {
                        case Config.Type.COMMON_ACTIVITY:
                            Config.setCOMMON_ACTIVITY(!Config.getCOMMON_ACTIVITY());
                            break;
                        case Config.Type.COMMON_NETWORK_SWITCH:
                            Config.setCOMMON_NETWORK_SWITCH(!Config.getCOMMON_NETWORK_SWITCH());
                            break;
                        case Config.Type.COMMON_SANDBOX_SWITCH:
                            Config.setCOMMON_SANDBOX_SWITCH(!Config.getCOMMON_SANDBOX_SWITCH());
                            break;
                        case Config.Type.COMMON_UI_SWITCH:
                            Config.setCOMMON_UI_SWITCH(!Config.getCOMMON_UI_SWITCH());
                            break;
                        case Config.Type.NETWORK_DELAY_REQ:
                        case Config.Type.NETWORK_DELAY_RES:
                        case Config.Type.NETWORK_PAGE_SIZE:
                        case Config.Type.SHAKE_THRESHOLD:
                            editingType = type;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PARAM2, callback);
                            if (type == Config.Type.SHAKE_THRESHOLD) {
                                bundle.putStringArray(PARAM3, Utils.newArray("800", "1000", "1200", "1400", "1600"));
                            }
                            launch(EditFragment.class, bundle);
                            break;
                        case Config.Type.SANBOX_DPM:
                            Config.setSANBOX_DPM(!Config.getSANBOX_DPM());
                            break;
                        case Config.Type.SHAKE_SWITCH:
                            Config.setSHAKE_SWITCH(!Config.getSHAKE_SWITCH());
                            break;
                    }
                }
            }
        });
    }


    private void refreshData() {
        List<BaseItem> data = new ArrayList<>();

        data.add(new TitleItem("COMMON"));
        data.add(new CheckBoxItem("show current activity name", Config.getCOMMON_ACTIVITY()).setTag(Config.Type.COMMON_ACTIVITY));
        data.add(new CheckBoxItem("enable network module", Config.getCOMMON_NETWORK_SWITCH()).setTag(Config.Type.COMMON_NETWORK_SWITCH));
        data.add(new CheckBoxItem("enable sandbox module", Config.getCOMMON_SANDBOX_SWITCH()).setTag(Config.Type.COMMON_SANDBOX_SWITCH));
        data.add(new CheckBoxItem("enable UI module", Config.getCOMMON_UI_SWITCH()).setTag(Config.Type.COMMON_UI_SWITCH));

        data.add(new TitleItem("NETWORK"));
        data.add(new NameArrowItem("delay for each request (ms)", "" + Config.getNETWORK_DELAY_REQ()).setTag(Config.Type.NETWORK_DELAY_REQ));
        data.add(new NameArrowItem("delay for each response (ms)", "" + Config.getNETWORK_DELAY_RES()).setTag(Config.Type.NETWORK_DELAY_RES));
        data.add(new NameArrowItem("the maximum number of first loads", "" + Config.getNETWORK_PAGE_SIZE()).setTag(Config.Type.NETWORK_PAGE_SIZE));

        data.add(new TitleItem("SANDBOX"));
        data.add(new CheckBoxItem("show device-protect-mode file\n(only for api>=24)", Config.getSANBOX_DPM()).setTag(Config.Type.SANBOX_DPM));

        data.add(new TitleItem("SHAKE"));
        data.add(new CheckBoxItem("turn on", Config.getSHAKE_SWITCH()).setTag(Config.Type.SHAKE_SWITCH));
        data.add(new NameArrowItem("threshold", "" + Config.getSHAKE_THRESHOLD()).setTag(Config.Type.SHAKE_THRESHOLD));

        getAdapter().setItems(data);
    }

    private int editingType;
    private EditCallback callback = new EditCallback() {
        @Override
        public void onValueChanged(String value) {
            try {
                switch (editingType) {
                    case Config.Type.NETWORK_DELAY_REQ:
                        long req = Long.parseLong(value);
                        Config.setNETWORK_DELAY_REQ(req);
                        break;
                    case Config.Type.NETWORK_DELAY_RES:
                        long res = Long.parseLong(value);
                        Config.setNETWORK_DELAY_RES(res);
                        break;
                    case Config.Type.NETWORK_PAGE_SIZE:
                        int size = Integer.parseInt(value);
                        if (size < 1) {
                            Utils.toast("invalid. At least 1");
                            return;
                        }
                        Config.setNETWORK_PAGE_SIZE(size);
                        break;
                    case Config.Type.SHAKE_THRESHOLD:
                        int threshold = Integer.parseInt(value);
                        if (threshold < 600) {
                            Utils.toast("invalid. At least 600");
                            return;
                        }
                        Config.setSHAKE_THRESHOLD(threshold);
                        break;
                }
                refreshData();
                Utils.toast(R.string.pd_success);
            } catch (Throwable t) {
                t.printStackTrace();
                Utils.toast(t.getMessage());
            }
        }
    };
}
