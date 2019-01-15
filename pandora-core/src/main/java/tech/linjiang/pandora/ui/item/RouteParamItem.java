package tech.linjiang.pandora.ui.item;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;

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

    private String input1, input2;
    private int flagType;

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

    @Override
    public void onBinding(final int position, UniversalAdapter.ViewPool pool, Integer data) {
        pool.getView(R.id.input1).setVisibility(Type.FLAG == data ? View.GONE : View.VISIBLE);
        pool.getView(R.id.input2).setEnabled(Type.FLAG != data);
        pool.setText(R.id.type, getTypeName(data))
                .setText(R.id.input1, input1)
                .setText(R.id.input2, input2);
    }

    @Override
    public int getLayout() {
        return R.layout.pd_item_route_param;
    }

    private static String getTypeName(@Type int type) {
        switch (type) {
            case Type.BOOLEAN:
                return "boolean";
            case Type.DOUBLE:
                return "double";
            case Type.FLAG:
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

    @IntDef({
            Type.STRING,
            Type.BOOLEAN,
            Type.INT,
            Type.LONG,
            Type.FLOAT,
            Type.DOUBLE,
            Type.FLAG,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int STRING = 0x00;
        int BOOLEAN = 0x01;
        int INT = 0x02;
        int LONG = 0x03;
        int FLOAT = 0x04;
        int DOUBLE = 0x05;
        int FLAG = 0x10;
    }

}
