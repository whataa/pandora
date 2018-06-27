package tech.linjiang.pandora.ui.item;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.database.Column;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 03/06/2018.
 */

public class GridItem extends BaseItem<String> {

    public boolean isColumnName;
    private boolean isPrimaryKey;
    public String primaryKeyValue;
    public String columnName;

    public void setIsPrimaryKey() {
        isPrimaryKey = true;
    }

    public boolean isEnable() {
        return !isColumnName && !isPrimaryKey && !Column.ROW_ID.equals(columnName);
    }


    public GridItem(String columnValue, String primaryKeyValue, String columnName) {
        super(columnValue);
        this.primaryKeyValue = primaryKeyValue;
        this.columnName = columnName;
    }

    public GridItem(String data, boolean isColumnName) {
        super(data);
        this.isColumnName = isColumnName;
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        ((TextView) pool.getView(R.id.gird_text)).setTypeface(null,
                TextUtils.isEmpty(data) ? Typeface.ITALIC : Typeface.NORMAL);
        ((TextView) pool.getView(R.id.gird_text)).setTextColor(
                TextUtils.isEmpty(data) ? ViewKnife.getColor(R.color.pd_label) : Color.BLACK);
        pool.setText(R.id.gird_text, TextUtils.isEmpty(data) ? "NULL" : data);
        pool.setBackgroundColor(R.id.gird_text,
                !isEnable() ? ViewKnife.getColor(R.color.pd_item_key) : Color.WHITE);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_table_cell;
    }
}
