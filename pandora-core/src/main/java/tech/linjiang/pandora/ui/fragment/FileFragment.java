package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.linjiang.pandora.sandbox.Sandbox;
import tech.linjiang.pandora.ui.item.FileItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 04/06/2018.
 */

public class FileFragment extends BaseListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = (File) getArguments().getSerializable(PARAM1);
        getToolbar().setTitle(file.getName());

        List<File> files = Sandbox.getFiles(file);
        if (Utils.isNotEmpty(files)) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d FILES", files.size())));
            for (int i = 0; i < files.size(); i++) {
                data.add(new FileItem(files.get(i)));
            }
            getAdapter().setItems(data);
            getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, BaseItem item) {
                    Bundle bundle = new Bundle();
                    if (item instanceof FileItem) {
                        bundle.putSerializable(PARAM1, (File) item.data);
                        if (((File) item.data).isDirectory()) {
                            launch(FileFragment.class, bundle);
                        } else {
                            launch(FileAttrFragment.class, bundle);
                        }
                    }
                }
            });
        } else {
            showError(null);
        }
    }
}
