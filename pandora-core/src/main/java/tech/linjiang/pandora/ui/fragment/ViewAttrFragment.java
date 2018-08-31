package tech.linjiang.pandora.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.inspector.model.Attribute;
import tech.linjiang.pandora.ui.connector.EditCallback;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.item.ViewAttrItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 15/06/2018.
 */

public class ViewAttrFragment extends BaseListFragment {


    private View targetView;
    private @Attribute.Edit
    int editType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Pandora.get().getBottomActivity() != null) {
            View decor = ViewKnife.tryGetTheFrontView(Pandora.get().getBottomActivity());
            if (decor != null) {
                targetView = findViewByDefaultTag(decor);
            }
            if (targetView != null) {
                // clear flag
                targetView.setTag(R.id.pd_view_tag_for_unique, null);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (targetView == null) {
            return;
        }
        getToolbar().setTitle(targetView.getClass().getSimpleName());
        getToolbar().setSubtitle("@" + ViewKnife.getIdString(targetView));

        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof ViewAttrItem) {
                    editType = ((ViewAttrItem) item).data.attrType;
                    if (editType != Attribute.Edit.NORMAL) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PARAM2, callback);
                        bundle.putStringArray(PARAM3, assembleOption(editType));
                        launch(EditFragment.class, bundle);
                    } else {
                        Utils.toast(R.string.pd_can_not_edit);
                    }
                }
            }
        });

        loadData();
    }

    private View findViewByDefaultTag(View root) {
        if (root.getTag(R.id.pd_view_tag_for_unique) != null) {
            return root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = findViewByDefaultTag(parent.getChildAt(i));
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(Void[] params) {
                List<BaseItem> data = new ArrayList<>();
                final Context context = targetView.getContext();
                if (context instanceof Activity) {
                    data.add(new TitleItem(context.getClass().getSimpleName()));
                    data.add(new ViewAttrItem(new Attribute("package", context.getClass().getPackage().getName())));
                    data.add(new ViewAttrItem(new Attribute("id", getToolbar().getSubtitle().toString())));
                }
                List<Attribute> attributes = Pandora.get().getAttrFactory().parse(targetView);
                if (Utils.isNotEmpty(attributes)) {
                    String category = null;
                    for (Attribute attr : attributes) {
                        if (!TextUtils.equals(category, attr.category)) {
                            category = attr.category;
                            data.add(new TitleItem(category));
                        }
                        data.add(new ViewAttrItem(attr));
                    }
                }
                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> data) {
                getAdapter().setItems(data);
                hideLoading();
            }
        }).execute();
    }

    private String[] assembleOption(int type) {
        switch (type) {
            case Attribute.Edit.LAYOUT_WIDTH:
            case Attribute.Edit.LAYOUT_HEIGHT:
                return Utils.newArray("MATCH_PARENT", "WRAP_CONTENT");
            case Attribute.Edit.SCALE_TYPE:
                return Utils.newArray(
                        "CENTER_INSIDE",
                        "CENTER_CROP",
                        "CENTER",
                        "FIT_CENTER",
                        "FIT_END",
                        "FIT_START",
                        "FIT_XY",
                        "MATRIX"
                );
            case Attribute.Edit.VISIBILITY:
                return Utils.newArray("VISIBLE", "INVISIBLE", "GONE");
            default:
                return null;
        }
    }

    private EditCallback callback = new EditCallback() {
        @Override
        public void onValueChanged(String value) {
            try {
                switch (editType) {
                    case Attribute.Edit.ALPHA:
                        float alpha = Float.valueOf(value);
                        targetView.setAlpha(alpha);
                        break;
                    case Attribute.Edit.LAYOUT_HEIGHT:
                        switch (value) {
                            case "WRAP_CONTENT":
                                targetView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                break;
                            case "MATCH_PARENT":
                                targetView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                                break;
                            default:
                                int height = Integer.valueOf(value);
                                targetView.getLayoutParams().height = ViewKnife.dip2px(height);
                                break;
                        }
                        break;
                    case Attribute.Edit.LAYOUT_WIDTH:
                        switch (value) {
                            case "WRAP_CONTENT":
                                targetView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                break;
                            case "MATCH_PARENT":
                                targetView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                break;
                            default:
                                int width = Integer.valueOf(value);
                                targetView.getLayoutParams().width = ViewKnife.dip2px(width);
                                break;
                        }
                        break;
                    case Attribute.Edit.NORMAL:
                        break;
                    case Attribute.Edit.PADDING_BOTTOM:
                        int pdBottom = Integer.valueOf(value);
                        targetView.setPadding(
                                targetView.getPaddingLeft(),
                                targetView.getPaddingTop(),
                                targetView.getPaddingRight(),
                                ViewKnife.dip2px(pdBottom));
                        break;
                    case Attribute.Edit.PADDING_LEFT:
                        int pdLeft = Integer.valueOf(value);
                        targetView.setPadding(
                                ViewKnife.dip2px(pdLeft),
                                targetView.getPaddingTop(),
                                targetView.getPaddingRight(),
                                targetView.getPaddingBottom());
                        break;
                    case Attribute.Edit.PADDING_RIGHT:
                        int pdRight = Integer.valueOf(value);
                        targetView.setPadding(
                                targetView.getPaddingLeft(),
                                targetView.getPaddingTop(),
                                ViewKnife.dip2px(pdRight),
                                targetView.getPaddingBottom());
                        break;
                    case Attribute.Edit.PADDING_TOP:
                        int pdTop = Integer.valueOf(value);
                        targetView.setPadding(
                                targetView.getPaddingLeft(),
                                ViewKnife.dip2px(pdTop),
                                targetView.getPaddingRight(),
                                targetView.getPaddingBottom());
                        break;
                    case Attribute.Edit.SCALE_TYPE:
                        ImageView.ScaleType scaleType = null;
                        switch (value) {
                            case "CENTER_INSIDE":
                                scaleType = ImageView.ScaleType.CENTER_INSIDE;
                                break;
                            case "CENTER_CROP":
                                scaleType = ImageView.ScaleType.CENTER_CROP;
                                break;
                            case "CENTER":
                                scaleType = ImageView.ScaleType.CENTER;
                                break;
                            case "FIT_CENTER":
                                scaleType = ImageView.ScaleType.FIT_CENTER;
                                break;
                            case "FIT_END":
                                scaleType = ImageView.ScaleType.FIT_END;
                                break;
                            case "FIT_START":
                                scaleType = ImageView.ScaleType.FIT_START;
                                break;
                            case "FIT_XY":
                                scaleType = ImageView.ScaleType.FIT_XY;
                                break;
                            case "MATRIX":
                                scaleType = ImageView.ScaleType.MATRIX;
                                break;
                        }
                        if (scaleType != null) {
                            ((ImageView) targetView).setScaleType(scaleType);
                        }
                        break;
                    case Attribute.Edit.TEXT_COLOR:
                        int color = Color.parseColor(value);
                        ((TextView) targetView).setTextColor(color);
                        break;
                    case Attribute.Edit.TEXT_SIZE:
                        float size = Float.valueOf(value);
                        ((TextView) targetView).setTextSize(size);
                        break;
                    case Attribute.Edit.TEXT:
                        ((TextView) targetView).setText(value);
                        break;
                    case Attribute.Edit.VISIBILITY:
                        int visibility = -1;
                        switch (value) {
                            case "VISIBLE":
                                visibility = View.VISIBLE;
                                break;
                            case "GONE":
                                visibility = View.GONE;
                                break;
                            case "INVISIBLE":
                                visibility = View.INVISIBLE;
                                break;
                        }
                        targetView.setVisibility(visibility);
                        break;
                    case Attribute.Edit.MARGIN_BOTTOM:
                        int leftMargin = Integer.valueOf(value);
                        ((ViewGroup.MarginLayoutParams) targetView.getLayoutParams()).leftMargin = ViewKnife.dip2px(leftMargin);
                        break;
                    case Attribute.Edit.MARGIN_LEFT:
                        int rightMargin = Integer.valueOf(value);
                        ((ViewGroup.MarginLayoutParams) targetView.getLayoutParams()).rightMargin = ViewKnife.dip2px(rightMargin);
                        break;
                    case Attribute.Edit.MARGIN_RIGHT:
                        int topMargin = Integer.valueOf(value);
                        ((ViewGroup.MarginLayoutParams) targetView.getLayoutParams()).topMargin = ViewKnife.dip2px(topMargin);
                        break;
                    case Attribute.Edit.MARGIN_TOP:
                        int bottomMargin = Integer.valueOf(value);
                        ((ViewGroup.MarginLayoutParams) targetView.getLayoutParams()).bottomMargin = ViewKnife.dip2px(bottomMargin);
                        break;
                }
                targetView.requestLayout();
                targetView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            } catch (Throwable t) {
                Utils.toast(t.getMessage());
            }
        }
    };
}
