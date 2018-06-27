package tech.linjiang.pandora.ui.item;

import android.view.View;

import java.io.File;
import java.util.Locale;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.FileUtil;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 04/06/2018.
 */

public class FileItem extends BaseItem<File> {
    private String info;

    public FileItem(File data) {
        super(data);
        if (!data.isDirectory()) {
            info = String.format(Locale.getDefault(), "%s    %s",
                    FileUtil.fileSize(data), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));
        } else {
            info = String.format(Locale.getDefault(), "%d items    %s",
                   Utils.getCount(data.list()), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));
        }
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, File data) {
        pool
                .setVisibility(R.id.common_item_arrow, data.isDirectory() ? View.VISIBLE : View.GONE)
                .setText(R.id.common_item_title, data.getName())
                .setText(R.id.common_item_info, info);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_common;
    }
}
