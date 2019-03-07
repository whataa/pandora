package tech.linjiang.pandora.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.cache.Crash;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.ExceptionItem;
import tech.linjiang.pandora.ui.item.KeyValueItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.util.FileUtil;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/4.
 */

public class CrashStackFragment extends BaseListFragment {

    private StringBuilder formatText = new StringBuilder();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Crash crash = (Crash) getArguments().getSerializable(PARAM1);

        final String time = Utils.millis2String(crash.createTime, Utils.NO_MILLIS);
        getToolbar().setTitle(time);
        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_copy_value);
        getToolbar().getMenu().add(-1, 0, 1, R.string.pd_name_share);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(formatText.toString());
                } else if (item.getOrder() == 1) {
                    saveAsFileAndShare(formatText.toString());
                }
                return true;
            }
        });
        formatText.append("time: ").append(time).append("\n");

        List<BaseItem> items = new ArrayList<>();
        formatText.append(crash.stack).append("\n");
        items.add(new ExceptionItem(crash.stack));

        formatText.append("duration: ").append(Utils.formatDuration(crash.createTime - crash.startTime)).append("\n");
        items.add(new KeyValueItem(new String[]{"duration", Utils.formatDuration(crash.createTime - crash.startTime)}, false));

        formatText.append("versionCode: ").append(String.valueOf(crash.versionCode)).append("\n");
        items.add(new KeyValueItem(new String[]{"versionCode", String.valueOf(crash.versionCode)}, false));

        formatText.append("versionName: ").append(String.valueOf(crash.versionName)).append("\n");
        items.add(new KeyValueItem(new String[]{"versionName", String.valueOf(crash.versionName)}, false));

        formatText.append("androidSDK: ").append(String.valueOf(crash.systemSDK)).append("\n");
        items.add(new KeyValueItem(new String[]{"androidSDK", String.valueOf(crash.systemSDK)}, false));

        formatText.append("androidVersion: ").append(String.valueOf(crash.systemVersion)).append("\n");
        items.add(new KeyValueItem(new String[]{"androidVersion", String.valueOf(crash.systemVersion)}, false));

        formatText.append("rom: ").append(String.valueOf(crash.rom)).append("\n");
        items.add(new KeyValueItem(new String[]{"rom", String.valueOf(crash.rom)}, false));

        formatText.append("cpuABI: ").append(String.valueOf(crash.cpuABI)).append("\n");
        items.add(new KeyValueItem(new String[]{"cpuABI", String.valueOf(crash.cpuABI)}, false));

        formatText.append("phoneName: ").append(String.valueOf(crash.phoneName)).append("\n");
        items.add(new KeyValueItem(new String[]{"phoneName", String.valueOf(crash.phoneName)}, false));

        formatText.append("locale: ").append(String.valueOf(crash.locale)).append("\n");
        items.add(new KeyValueItem(new String[]{"locale", String.valueOf(crash.locale)}, false));
        getAdapter().setItems(items);
    }

    private void saveAsFileAndShare(String msg) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<String, Intent>() {
            @Override
            public Intent doInBackground(String[] params) {
                String path = FileUtil.saveFile(params[0].getBytes(), "crash", "txt");
                String newPath = FileUtil.fileCopy2Tmp(new File(path));
                if (!TextUtils.isEmpty(newPath)) {
                    return FileUtil.getFileIntent(newPath);
                }
                return null;
            }

            @Override
            public void onPostExecute(Intent result) {
                hideLoading();
                if (result != null) {
                    try {
                        startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Utils.toast(t.getMessage());
                    }
                } else {
                    Utils.toast(R.string.pd_failed);
                }

            }
        }).execute(msg);
    }
}
