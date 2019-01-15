package tech.linjiang.pandora.ui.item;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

/**
 * Created by linjiang on 2019/1/14.
 */

public class RouteParamItem extends BaseItem<Integer> {
    public RouteParamItem(@Type Integer type) {
        super(type);
    }

    public RouteParamItem(@Type Integer type, EditListener listener) {
        super(type);
        this.listener = listener;
    }

    private String input1, input2;
    private int flagType;
    private int whichIdInput;
    private boolean isEditRequesting;
    private EditListener listener;

    public int getFlagType() {
        return flagType;
    }

    public String getInput1() {
        return input1;
    }

    public String getInput2() {
        return input2;
    }

    public boolean HasInput() {
        return !TextUtils.isEmpty(input1) && !TextUtils.isEmpty(input2);
    }

    public void setFlagType(int flagType, String name) {
        this.flagType = flagType;
        input2 = name;
    }

    public boolean isEditRequesting() {
        return isEditRequesting;
    }

    public void setTheEditResult(String value) {
        isEditRequesting = false;
        if (whichIdInput == R.id.input1) {
            input1 = value;
        } else {
            input2 = value;
        }
    }

    @Override
    public void onBinding(final int position, UniversalAdapter.ViewPool pool, Integer data) {
        pool.getView(R.id.input1).setVisibility(Type.NONE == data ? View.GONE : View.VISIBLE);
        pool.getView(R.id.input2).setEnabled(Type.NONE != data);
        pool.setText(R.id.type, getTypeName(data))
                .setText(R.id.input1, input1)
                .setText(R.id.input2, input2);
        pool.getView(R.id.input1).setTag(0);
        pool.getView(R.id.input1).setOnClickListener(clickListener);
        pool.getView(R.id.input2).setTag(1);
        pool.getView(R.id.input2).setOnClickListener(clickListener);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_route_param;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            whichIdInput = v.getId();
            TextView tv = (TextView) v;
            isEditRequesting = true;
            if (listener != null) {
                listener.onEditReq(tv.getText().toString(), whichIdInput == R.id.input1 ? Type.STRING : data);
            }
        }
    };

    private static String getTypeName(@Type int type) {
        switch (type) {
            case Type.BOOLEAN:
                return "boolean";
            case Type.DOUBLE:
                return "double";
            case Type.NONE:
                return "flag";
            case Type.FLOAT:
                return "float";
            case Type.INT:
                return "int";
            case Type.LONG:
                return "long";
            case Type.STRING:
                return "String";
        }
        return null;
    }

    public interface EditListener {
        void onEditReq(String def, @Type int type);
    }

    @IntDef({
            Type.NONE,
            Type.STRING,
            Type.BOOLEAN,
            Type.INT,
            Type.LONG,
            Type.FLOAT,
            Type.DOUBLE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int NONE = -1;
        int STRING = 0x00;
        int BOOLEAN = 0x01;
        int INT = 0x02;
        int LONG = 0x03;
        int FLOAT = 0x04;
        int DOUBLE = 0x05;
    }
}
