package tech.linjiang.pandora.inspector.attribute.parser;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.inspector.model.Attribute;

/**
 * Created by linjiang on 2018/6/19.
 */

public class ImageViewParser implements IParser<ImageView> {

    @Override
    public List<Attribute> getAttrs(ImageView view) {
        List<Attribute> attributes = new ArrayList<>();
        Attribute scaleTypeAttribute = new Attribute("scaleType", scaleTypeToStr(view.getScaleType()), Attribute.Edit.SCALE_TYPE);
        attributes.add(scaleTypeAttribute);
        return attributes;
    }
    private static String scaleTypeToStr(ImageView.ScaleType scaleType) {
        switch (scaleType) {
            case CENTER:
                return "CENTER";
            case FIT_XY:
                return "FIT_XY";
            case MATRIX:
                return "MATRIX";
            case FIT_END:
                return "FIT_END";
            case FIT_START:
                return "FIT_START";
            case FIT_CENTER:
                return "FIT_CENTER";
            case CENTER_CROP:
                return "CENTER_CROP";
            case CENTER_INSIDE:
                return "CENTER_INSIDE";
                default:
                    return "OTHER";
        }
    }
}
