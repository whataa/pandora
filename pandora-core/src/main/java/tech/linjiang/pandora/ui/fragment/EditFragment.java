package tech.linjiang.pandora.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.connector.EditCallback;
import tech.linjiang.pandora.ui.item.OptionItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 03/06/2018.
 *
 * PARAM1: Default content
 * PARAM2: EditCallback
 * PARAM3: Quick input options
 * PARAM4: if can edit
 */

public class EditFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        View wrapper;
        editText = new EditText(getContext());
        int padding = ViewKnife.dip2px(16);
        editText.setPadding(padding, padding, padding, padding);
        editText.setBackgroundColor(Color.WHITE);
        editText.setGravity(Gravity.START | Gravity.TOP);
        editText.setTextColor(ViewKnife.getColor(R.color.pd_label_dark));
        editText.setLineSpacing(0, 1.2f);

        String[] options = getArguments().getStringArray(PARAM3);
        if (options != null && options.length > 0) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            wrapper = layout;
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setBackgroundColor(Color.WHITE);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
            UniversalAdapter adapter = new UniversalAdapter();
            recyclerView.setAdapter(adapter);
            adapter.setListener(new UniversalAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, BaseItem item) {
                    if (callback != null) {
                        callback.onValueChanged(((OptionItem)item).data);
                    }
                    onBackPressed();
                }
            });
            List<BaseItem> items = new ArrayList<>(options.length);
            for (String option : options) {
                items.add(new OptionItem(option));
            }
            adapter.setItems(items);

            LinearLayout.LayoutParams recyclerParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewKnife.dip2px(50)
            );
            layout.addView(recyclerView, recyclerParam);
            LinearLayout.LayoutParams editParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            layout.addView(editText, editParam);
        } else {
            wrapper = editText;
        }
        return wrapper;
    }

    private EditText editText;
    private EditCallback callback;
    private boolean canNotEdit;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = ((EditCallback)getArguments().getSerializable(PARAM2));
        // bugFix: Anonymous inner class objects cannot be serialized on some 4.4 platform devices when saveInstanceState().
        // Maybe it holds outer class object ?
        getArguments().remove(PARAM2);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Edit");
        final String data = getArguments().getString(PARAM1);
        editText.setText(data);
        editText.setSelection(editText.getText().length());
        canNotEdit = getArguments().getBoolean(PARAM4);
        if (canNotEdit) {
            editText.setEnabled(false);
        } else {
            editText.requestFocus();
        }

        getToolbar().getMenu().findItem(R.id.menu_save).setVisible(true);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String curValue = editText.getText().toString();
                if (!TextUtils.equals(curValue, data)) {
                    if (callback != null) {
                        callback.onValueChanged(curValue);
                    }
                    onBackPressed();
                } else {
                    Utils.toast(R.string.pd_no_change);
                }
                return true;
            }
        });
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        if (!canNotEdit) {
            openSoftInput();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeSoftInput();
        callback = null;
    }
}
