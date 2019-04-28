package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.CheckBoxItem;
import tech.linjiang.pandora.ui.item.NameArrowItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/7/24.
 */

public class ConfigFragment extends BaseListFragment {

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_config);
        getToolbar().getMenu().add(-1, -1, 0, R.string.pd_name_reset).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
                        case Config.Type.NETWORK_DELAY_REQ:
                        case Config.Type.NETWORK_DELAY_RES:
                        case Config.Type.NETWORK_PAGE_SIZE:
                        case Config.Type.UI_ACTIVITY_GRAVITY:
                        case Config.Type.UI_GRID_INTERVAL:
                        case Config.Type.SHAKE_THRESHOLD:
                            editingType = type;
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(PARAM2, true);
                            if (type == Config.Type.SHAKE_THRESHOLD) {
                                bundle.putStringArray(PARAM3, Utils.newArray("800", "1000", "1200", "1400", "1600"));
                            } else if (type == Config.Type.UI_ACTIVITY_GRAVITY) {
                                bundle.putStringArray(PARAM3, Utils.newArray("start|top", "end|top", "start|bottom", "end|bottom"));
                                bundle.putBoolean(PARAM4, true);
                            }
                            launch(EditFragment.class, bundle, CODE1);
                            break;
                        case Config.Type.SANDBOX_DPM:
                            Config.setSANDBOX_DPM(!Config.getSANDBOX_DPM());
                            break;
                        case Config.Type.SHAKE_SWITCH:
                            Config.setSHAKE_SWITCH(!Config.getSHAKE_SWITCH());
                            break;
                        case Config.Type.UI_IGNORE_SYS_LAYER:
                            Config.setUI_IGNORE_SYS_LAYER(!Config.getUI_IGNORE_SYS_LAYER());
                            break;
                    }
                }
            }
        });
    }


    private void refreshData() {
        List<BaseItem> data = new ArrayList<>();

        data.add(new TitleItem(ViewKnife.getString(R.string.pd_name_network)));
        data.add(new NameArrowItem("delay for each request(ms)", "" + Config.getNETWORK_DELAY_REQ()).setTag(Config.Type.NETWORK_DELAY_REQ));
        data.add(new NameArrowItem("delay for each response(ms)", "" + Config.getNETWORK_DELAY_RES()).setTag(Config.Type.NETWORK_DELAY_RES));
        data.add(new NameArrowItem("the maximum number of first loads", "" + Config.getNETWORK_PAGE_SIZE()).setTag(Config.Type.NETWORK_PAGE_SIZE));

        data.add(new TitleItem(ViewKnife.getString(R.string.pd_name_sandbox)));
        data.add(new CheckBoxItem("show device-protect-mode file\n(only for api>=24)", Config.getSANDBOX_DPM()).setTag(Config.Type.SANDBOX_DPM));

        data.add(new TitleItem("UI"));
        data.add(new NameArrowItem("the gravity of activity info", "" + ViewKnife.parseGravity(Config.getUI_ACTIVITY_GRAVITY())).setTag(Config.Type.UI_ACTIVITY_GRAVITY));
        data.add(new NameArrowItem("the interval of grid line(dp)", "" + Config.getUI_GRID_INTERVAL()).setTag(Config.Type.UI_GRID_INTERVAL));
        data.add(new CheckBoxItem("ignore system layers in hierarchy", Config.getUI_IGNORE_SYS_LAYER()).setTag(Config.Type.UI_IGNORE_SYS_LAYER));

        data.add(new TitleItem("SHAKE"));
        data.add(new CheckBoxItem(getString(R.string.pd_name_turn_on), Config.getSHAKE_SWITCH()).setTag(Config.Type.SHAKE_SWITCH));
        data.add(new NameArrowItem(getString(R.string.pd_name_threshold), "" + Config.getSHAKE_THRESHOLD()).setTag(Config.Type.SHAKE_THRESHOLD));

        getAdapter().setItems(data);
    }

    private int editingType;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            String value = data.getStringExtra("value");
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
                    case Config.Type.UI_ACTIVITY_GRAVITY:
                        String gravity = String.valueOf(value);
                        int result = ViewKnife.formatGravity(gravity);
                        if (result != 0) {
                            Config.setUI_ACTIVITY_GRAVITY(result);
                        } else {
                            Utils.toast("invalid");
                        }
                        break;
                    case Config.Type.UI_GRID_INTERVAL:
                        int interval = Integer.parseInt(value);
                        if (interval < 1) {
                            Utils.toast("invalid. At least 1");
                            return;
                        }
                        Config.setUI_GRID_INTERVAL(interval);
                        break;

                }
                refreshData();
                Utils.toast(R.string.pd_success);
            } catch (Throwable t) {
                t.printStackTrace();
                Utils.toast(t.getMessage());
            }
        }
    }

}
