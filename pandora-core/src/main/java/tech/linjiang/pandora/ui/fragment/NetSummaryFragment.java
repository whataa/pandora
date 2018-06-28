package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.network.CacheDbHelper;
import tech.linjiang.pandora.network.model.Summary;
import tech.linjiang.pandora.ui.item.KeyValueItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2018/6/23.
 */

public class NetSummaryFragment extends BaseListFragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final long id = getArguments().getLong(PARAM1);
        loadData(id);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem) {
                    String tag = (String) item.getTag();
                    if (!TextUtils.isEmpty(tag)) {
                        Bundle bundle = new Bundle();
                        if (PARAM1.equals(tag)) {
                            bundle.putBoolean(PARAM1, false);
                        } else if (PARAM2.equals(tag)) {
                            bundle.putBoolean(PARAM1, true);
                        }
                        if (!bundle.isEmpty()) {
                            bundle.putLong(PARAM2, id);
                            launch(NetContentFragment.class, bundle);
                        }
                    } else {
                        String value = ((String[])item.data)[1];
                        if (!TextUtils.isEmpty(value)) {
                            Utils.copy2ClipBoard(value);
                        }
                    }
                }
            }
        });
    }

    private void loadData(final long id) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                return CacheDbHelper.getSummary(id);
            }

            @Override
            public void onPostExecute(Summary summary) {
                hideLoading();
                if (summary == null) {
                    showError(null);
                }
                getToolbar().setTitle(summary.url);
                getToolbar().setSubtitle(String.valueOf(summary.code));

                List<BaseItem> data = new ArrayList<>();
                data.add(new TitleItem("GENERAL"));
                data.add(new KeyValueItem(Utils.newArray("url", summary.url)));
                data.add(new KeyValueItem(Utils.newArray("host", summary.host)));
                data.add(new KeyValueItem(Utils.newArray("method", summary.method)));
                data.add(new KeyValueItem(Utils.newArray("protocol", summary.protocol)));
                data.add(new KeyValueItem(Utils.newArray("ssl", String.valueOf(summary.ssl))));
                data.add(new KeyValueItem(Utils.newArray("start_time", Utils.millis2String(summary.start_time))));
                data.add(new KeyValueItem(Utils.newArray("end_time", Utils.millis2String(summary.end_time))));
                data.add(new KeyValueItem(Utils.newArray("content-type", summary.response_content_type)));
                data.add(new KeyValueItem(Utils.newArray("request_size", Utils.formatSize(summary.request_size))));
                data.add(new KeyValueItem(Utils.newArray("response_size", Utils.formatSize(summary.response_size))));

                if (!TextUtils.isEmpty(summary.query)) {
                    data.add(new TitleItem(""));
                    data.add(new KeyValueItem(Utils.newArray("query", summary.query)));
                }

                data.add(new TitleItem("BODY"));
                KeyValueItem request = new KeyValueItem(Utils.newArray("request body", "tap to view"), false, true);
                request.setTag(PARAM1);
                data.add(request);
                if (summary.status == 2) {
                    KeyValueItem response = new KeyValueItem(Utils.newArray("response body", "tap to view"), false, true);
                    response.setTag(PARAM2);
                    data.add(response);
                }


                if (Utils.isNotEmpty(summary.request_header)) {
                    data.add(new TitleItem("REQUEST HEADER"));
                    for (Pair<String, String> pair : summary.request_header) {
                        data.add(new KeyValueItem(Utils.newArray(pair.first, pair.second)));
                    }
                }

                if (Utils.isNotEmpty(summary.response_header)) {
                    data.add(new TitleItem("RESPONSE HEADER"));
                    for (Pair<String, String> pair : summary.response_header) {
                        data.add(new KeyValueItem(Utils.newArray(pair.first, pair.second)));
                    }
                }

                getAdapter().setItems(data);
            }
        }).execute();
    }
}
