package tech.linjiang.pandora.inspector.attribute.parser;

import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.inspector.model.Attribute;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/19.
 */

public class TextViewParser implements IParser<TextView> {

    @Override
    public List<Attribute> getAttrs(TextView view) {
        List<Attribute> attributes = new ArrayList<>();
        Attribute textAttribute = new Attribute("text", view.getText().toString(), Attribute.Edit.TEXT);
        attributes.add(textAttribute);
        Attribute textColorAttribute = new Attribute("textColor", "#" + intToHex(view.getCurrentTextColor()), Attribute.Edit.TEXT_COLOR);
        attributes.add(textColorAttribute);
        Attribute textHintColorAttribute = new Attribute("textHintColor", "#" + intToHex(view.getCurrentHintTextColor()));
        attributes.add(textHintColorAttribute);
        Attribute textSizeAttribute = new Attribute("textSize", ViewKnife.px2dipStr(view.getTextSize()), Attribute.Edit.TEXT_SIZE);
        attributes.add(textSizeAttribute);
        Attribute gravityAttribute = new Attribute("gravity", gravityToStr(view.getGravity()));
        attributes.add(gravityAttribute);

        Attribute lineCountAttribute = new Attribute("lineCount", String.valueOf(view.getLineCount()));
        attributes.add(lineCountAttribute);
        Attribute lineHeightAttribute = new Attribute("lineHeight", ViewKnife.px2dipStr(view.getLineHeight()));
        attributes.add(lineHeightAttribute);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Attribute lineSpacingExtraAttribute = new Attribute("lineSpacingExtra", String.valueOf(view.getLineSpacingExtra()));
            attributes.add(lineSpacingExtraAttribute);
            Attribute lineSpacingMultiplierAttribute = new Attribute("lineSpacingMultiplier", String.valueOf(view.getLineSpacingMultiplier()));
            attributes.add(lineSpacingMultiplierAttribute);

            if (view instanceof EditText) {
                Attribute maxLinesAttribute = new Attribute("maxLines", String.valueOf(view.getMaxLines()));
                attributes.add(maxLinesAttribute);
                Attribute minLinesAttribute = new Attribute("minLines", String.valueOf(view.getMinLines()));
                attributes.add(minLinesAttribute);
                Attribute maxEmsAttribute = new Attribute("maxEms", String.valueOf(view.getMaxEms()));
                attributes.add(maxEmsAttribute);
                Attribute minEmsAttribute = new Attribute("minEms", String.valueOf(view.getMinEms()));
                attributes.add(minEmsAttribute);
                Attribute maxWidthAttribute = new Attribute("maxWidth", ViewKnife.px2dipStr(view.getMaxWidth()));
                attributes.add(maxWidthAttribute);
                Attribute minWidthAttribute = new Attribute("minWidth", ViewKnife.px2dipStr(view.getMinWidth()));
                attributes.add(minWidthAttribute);
                Attribute maxHeightAttribute = new Attribute("maxHeight", ViewKnife.px2dipStr(view.getMaxHeight()));
                attributes.add(maxHeightAttribute);
                Attribute minHeightAttribute = new Attribute("minHeight", ViewKnife.px2dipStr(view.getMinHeight()));
                attributes.add(minHeightAttribute);
            }
        }

        return attributes;
    }

    private static String intToHex(int value) {
        return Integer.toHexString(value).toUpperCase();
    }

    private static String gravityToStr(int gravity) {
        switch (gravity) {
            case Gravity.NO_GRAVITY:
                return "NO_GRAVITY";
            case Gravity.LEFT:
                return "LEFT";
            case Gravity.TOP:
                return "TOP";
            case Gravity.RIGHT:
                return "RIGHT";
            case Gravity.BOTTOM:
                return "BOTTOM";
            case Gravity.CENTER:
                return "CENTER";
            case Gravity.CENTER_HORIZONTAL:
                return "CENTER_HORIZONTAL";
            case Gravity.CENTER_VERTICAL:
                return "CENTER_VERTICAL";
            case Gravity.START:
                return "START";
            case Gravity.END:
                return "END";
            case Gravity.CLIP_HORIZONTAL:
                return "CLIP_HORIZONTAL";
            case Gravity.CLIP_VERTICAL:
                return "CLIP_VERTICAL";
            case Gravity.FILL:
                return "FILL";
            case Gravity.FILL_HORIZONTAL:
                return "FILL_HORIZONTAL";
            case Gravity.FILL_VERTICAL:
                return "FILL_VERTICAL";
            default:
                return "OTHER";
        }
    }
}
