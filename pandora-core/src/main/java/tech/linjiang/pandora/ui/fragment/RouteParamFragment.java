package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.item.RouteParamItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.MenuRecyclerView;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2019/1/13.
 */

public class RouteParamFragment extends BaseListFragment {

    @Override
    protected View getLayoutView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(super.getLayoutView(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));
        Button button = new Button(getContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                            Activity.RESULT_OK, assembleTargetIntent());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        button.setTextSize(14);
        button.setText("launch Activity");
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(R.drawable.pd_shape_btn_bg);
        LinearLayout.LayoutParams buttonParam;
        layout.addView(button, buttonParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewKnife.dip2px(45)
        ));
        buttonParam.leftMargin
                = buttonParam.topMargin
                = buttonParam.rightMargin
                = buttonParam.bottomMargin
                = ViewKnife.dip2px(16);

        return layout;
    }

    @Override
    protected Toolbar onCreateToolbar() {
        return new Toolbar(new ContextThemeWrapper(getContext(), R.style.pd_toolbar));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        registerForContextMenu(getRecyclerView());

        List<BaseItem> data = new ArrayList<>();
        data.add(new RouteParamItem(RouteParamItem.Type.STRING));
        data.add(new RouteParamItem(RouteParamItem.Type.BOOLEAN));
        data.add(new RouteParamItem(RouteParamItem.Type.INT));
        data.add(new RouteParamItem(RouteParamItem.Type.LONG));
        data.add(new RouteParamItem(RouteParamItem.Type.FLOAT));
        data.add(new RouteParamItem(RouteParamItem.Type.DOUBLE));
        getAdapter().setItems(data);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                getRecyclerView().getChildAt(position).showContextMenu();

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (menuInfo instanceof MenuRecyclerView.RvContextMenuInfo) {
            menu.add(-1, R.id.pd_menu_id_1, 0, "add");
            menu.add(-1, R.id.pd_menu_id_2, 0, "delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getMenuInfo() instanceof MenuRecyclerView.RvContextMenuInfo) {
            MenuRecyclerView.RvContextMenuInfo info = (MenuRecyclerView.RvContextMenuInfo) item.getMenuInfo();
            RouteParamItem paramItem = getAdapter().getItem(info.position);
            if (item.getItemId() == R.id.pd_menu_id_2) {
                getAdapter().removeItem(info.position);
            } else if (item.getItemId() == R.id.pd_menu_id_1) {
                getAdapter().insertItem(new RouteParamItem(paramItem.data), info.position);
            }
        }
        return super.onContextItemSelected(item);
    }

    private void initToolbar() {
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.STRING, "add String extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.BOOLEAN, "add boolean extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.INT, "add int extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.LONG, "add long extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.FLOAT, "add float extra");
        getToolbar().getMenu().add(0, 0, RouteParamItem.Type.DOUBLE, "add double extra");
        SubMenu subMenu = getToolbar().getMenu().addSubMenu(0, 0, 7, "add flag");
        subMenu.add(R.id.pd_menu_id_1, 0, 0, "FLAG_ACTIVITY_NEW_TASK");
        subMenu.add(R.id.pd_menu_id_1, 0, 1, "FLAG_ACTIVITY_SINGLE_TOP");
        subMenu.add(R.id.pd_menu_id_1, 0, 2, "FLAG_ACTIVITY_CLEAR_TASK");
        subMenu.add(R.id.pd_menu_id_1, 0, 3, "FLAG_ACTIVITY_CLEAR_TOP");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.hasSubMenu()) {
                    return true;
                } else {
                    if (item.getGroupId() == R.id.pd_menu_id_1) {
                        RouteParamItem paramItem = new RouteParamItem(RouteParamItem.Type.FLAG);
                        switch (item.getOrder()) {
                            case 0:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_NEW_TASK, item.getTitle().toString());
                                break;
                            case 1:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_SINGLE_TOP, item.getTitle().toString());
                                break;
                            case 2:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_CLEAR_TASK, item.getTitle().toString());
                                break;
                            case 3:
                                paramItem.setFlagType(Intent.FLAG_ACTIVITY_CLEAR_TOP, item.getTitle().toString());
                                break;
                        }
                        getAdapter().insertItem(paramItem);
                    } else {
                        getAdapter().insertItem(new RouteParamItem(item.getOrder()));
                    }
                    return true;
                }
            }
        });
    }

    private Intent assembleTargetIntent() throws ClassNotFoundException {
        String clazz = getArguments().getString("clazz");
        Intent intent = new Intent(getContext(), Class.forName(clazz));
        List<BaseItem> items = getAdapter().getItems();
        if (Utils.isNotEmpty(items)) {
            for (int i = 0; i < items.size(); i++) {
                RouteParamItem item = (RouteParamItem) items.get(i);
                switch (item.data) {
                    case RouteParamItem.Type.BOOLEAN:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Boolean.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.DOUBLE:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Double.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.FLAG:
                        intent.addFlags(item.getFlagType());
                        break;
                    case RouteParamItem.Type.FLOAT:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Float.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.INT:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Integer.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.LONG:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), Long.valueOf(item.getInput2()));
                        }
                        break;
                    case RouteParamItem.Type.STRING:
                        if (item.HasInput()) {
                            intent.putExtra(item.getInput1(), item.getInput2());
                        }
                        break;
                }
            }
        }
        return intent;
    }
}
