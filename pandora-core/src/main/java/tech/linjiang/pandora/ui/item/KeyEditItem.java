package tech.linjiang.pandora.ui.item;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.ExtraEditTextView;

/**
 * Created by linjiang on 07/06/2018.
 */

public class KeyEditItem extends BaseItem<String[]> {

    public boolean editable = true;
    public String hint;

    /**
     *
     * @param disable   if can edit
     * @param data      [0]: key [1]: value
     * @param hint      hint of editText
     */
    public KeyEditItem(boolean disable, String[] data, String hint) {
        super(data);
        this.editable = !disable;
        this.hint = hint;
    }

    public KeyEditItem(boolean disable, String[] data) {
        this(disable, data, null);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String[] data) {
        // must first call the following two lines of code
        ((ExtraEditTextView)pool.getView(R.id.item_edit)).clearTextChangedListeners();
        ((EditText)pool.getView(R.id.item_edit)).addTextChangedListener(watcher);
        pool
                .setText(R.id.item_key, data[0])
                .setText(R.id.item_edit, data[1]);
        ((EditText)pool.getView(R.id.item_edit)).setHint(hint);


        pool.getView(R.id.item_value).setVisibility(View.GONE);
        pool.getView(R.id.item_edit).setVisibility(View.VISIBLE);
        pool.getView(R.id.item_edit).setEnabled(editable);
        if (editable) {
            ((EditText)pool.getView(R.id.item_edit)).setSingleLine(true);
        } else {
            ((EditText)pool.getView(R.id.item_edit)).setSingleLine(false);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_key_value;
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (data != null && data.length >= 2) {
                data[1] = s.toString();
            }
        }
    };
}
