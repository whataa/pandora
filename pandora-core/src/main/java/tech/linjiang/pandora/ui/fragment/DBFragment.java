package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.ui.item.NameItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 03/06/2018.
 */

public class DBFragment extends BaseListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int key = getArguments().getInt(PARAM1);

        List<String> tables = Pandora.get().getDatabases().getTableNames(key);
        Collections.sort(tables);
        List<BaseItem> data = new ArrayList<>(tables.size());
        data.add(new TitleItem(String.format(Locale.getDefault(), "%d TABLES", tables.size())));
        for (int i = 0; i < tables.size(); i++) {
            data.add(new NameItem(tables.get(i)));
        }
        getAdapter().setItems(data);

        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NameItem) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, ((NameItem) item).data);
                    launch(TableFragment.class, ((NameItem) item).data, bundle);
                }
            }
        });
    }
}
