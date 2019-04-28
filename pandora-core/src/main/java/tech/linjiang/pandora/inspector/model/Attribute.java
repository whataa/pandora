package tech.linjiang.pandora.inspector.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by linjiang on 15/06/2018.
 */

public class Attribute {

    // For classification
    public String category;
    public String attrName;
    public String attrValue;


    public Attribute(String attrName, String attrValue) {
        this.attrName = attrName;
        this.attrValue = attrValue;
    }

    public Attribute(String attrName, String attrValue, @Edit int attrType) {
        this.attrName = attrName;
        this.attrValue = attrValue;
        this.attrType = attrType;
    }

    // Some attributes that can be edited
    public @Edit int attrType = Edit.NORMAL;

    @IntDef({
            Edit.NORMAL,
            Edit.LAYOUT_WIDTH,
            Edit.LAYOUT_HEIGHT,
            Edit.VISIBILITY,
            Edit.PADDING_LEFT,
            Edit.PADDING_RIGHT,
            Edit.PADDING_TOP,
            Edit.PADDING_BOTTOM,
            Edit.ALPHA,
            Edit.TEXT,
            Edit.TEXT_COLOR,
            Edit.TEXT_SIZE,
            Edit.SCALE_TYPE,
            Edit.MARGIN_LEFT,
            Edit.MARGIN_RIGHT,
            Edit.MARGIN_TOP,
            Edit.MARGIN_BOTTOM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Edit {
        int NORMAL = 0x00;
        int LAYOUT_WIDTH = 0x01;
        int LAYOUT_HEIGHT = 0x02;
        int VISIBILITY = 0x03;
        int PADDING_LEFT = 0x04;
        int PADDING_RIGHT = 0x05;
        int PADDING_TOP = 0x06;
        int PADDING_BOTTOM = 0x07;
        int ALPHA = 0x08;

        int TEXT_SIZE = 0x10;
        int TEXT_COLOR = 0x11;
        int TEXT = 0x12;
        int SCALE_TYPE = 0x13;

        int MARGIN_LEFT = 0x14;
        int MARGIN_RIGHT = 0x15;
        int MARGIN_TOP = 0x16;
        int MARGIN_BOTTOM = 0x17;
    }
}
