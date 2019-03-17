package tech.linjiang.pandora.inspector.attribute.parser;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.inspector.model.Attribute;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/19.
 */

public class ViewParser implements IParser<View> {
    @Override
    public List<Attribute> getAttrs(View view) {
        List<Attribute> attributes = new ArrayList<>();
        Attribute classAttribute = new Attribute("class", String.valueOf(view.getClass().getName()));
        attributes.add(classAttribute);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        Attribute paramsAttribute = new Attribute("LayoutParams", params.getClass().getName());
        attributes.add(paramsAttribute);
        Attribute widthAttribute = new Attribute("layout_width", formatLayoutParam(params.width, view.getWidth()), Attribute.Edit.LAYOUT_WIDTH);
        attributes.add(widthAttribute);
        Attribute heightAttribute = new Attribute("layout_height", formatLayoutParam(params.height, view.getHeight()), Attribute.Edit.LAYOUT_HEIGHT);
        attributes.add(heightAttribute);

        Attribute visibilityAttribute = new Attribute("visibility", formatVisibility(view.getVisibility()), Attribute.Edit.VISIBILITY);
        attributes.add(visibilityAttribute);

        Attribute paddingLeftAttribute = new Attribute("paddingLeft", ViewKnife.px2dipStr(view.getPaddingLeft()), Attribute.Edit.PADDING_LEFT);
        attributes.add(paddingLeftAttribute);
        Attribute paddingTopAttribute = new Attribute("paddingTop", ViewKnife.px2dipStr(view.getPaddingTop()), Attribute.Edit.PADDING_TOP);
        attributes.add(paddingTopAttribute);
        Attribute paddingRightAttribute = new Attribute("paddingRight", ViewKnife.px2dipStr(view.getPaddingRight()), Attribute.Edit.PADDING_RIGHT);
        attributes.add(paddingRightAttribute);
        Attribute paddingBottomAttribute = new Attribute("paddingBottom", ViewKnife.px2dipStr(view.getPaddingBottom()), Attribute.Edit.PADDING_BOTTOM);
        attributes.add(paddingBottomAttribute);

        if (view.getLayoutParams() != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            Attribute marginLeftAttribute = new Attribute("marginLeft", ViewKnife.px2dipStr(marginLayoutParams.leftMargin), Attribute.Edit.MARGIN_LEFT);
            attributes.add(marginLeftAttribute);
            Attribute marginTopAttribute = new Attribute("marginTop", ViewKnife.px2dipStr(marginLayoutParams.topMargin), Attribute.Edit.MARGIN_TOP);
            attributes.add(marginTopAttribute);
            Attribute marginRightAttribute = new Attribute("marginRight", ViewKnife.px2dipStr(marginLayoutParams.rightMargin), Attribute.Edit.MARGIN_RIGHT);
            attributes.add(marginRightAttribute);
            Attribute marginBottomAttribute = new Attribute("marginBottom", ViewKnife.px2dipStr(marginLayoutParams.bottomMargin), Attribute.Edit.MARGIN_BOTTOM);
            attributes.add(marginBottomAttribute);
        }

        Attribute translationXAttribute = new Attribute("translationX", ViewKnife.px2dipStr(view.getTranslationX()));
        attributes.add(translationXAttribute);
        Attribute translationYAttribute = new Attribute("translationY", ViewKnife.px2dipStr(view.getTranslationY()));
        attributes.add(translationYAttribute);

        Attribute backgroundAttribute = new Attribute("background", formatDrawable(view.getBackground()));
        attributes.add(backgroundAttribute);
        Attribute alphaAttribute = new Attribute("alpha", String.valueOf(view.getAlpha()), Attribute.Edit.ALPHA);
        attributes.add(alphaAttribute);
        Attribute tagAttribute = new Attribute("tag", String.valueOf(view.getTag()));
        attributes.add(tagAttribute);

        Attribute enableAttribute = new Attribute("enable", String.valueOf(view.isEnabled()));
        attributes.add(enableAttribute);
        Attribute clickAttribute = new Attribute("clickable", String.valueOf(view.isClickable()));
        attributes.add(clickAttribute);
        Attribute longClickableAttribute = new Attribute("longClickable", String.valueOf(view.isLongClickable()));
        attributes.add(longClickableAttribute);
        Attribute focusAttribute = new Attribute("focusable", String.valueOf(view.isFocusable()));
        attributes.add(focusAttribute);

        Attribute contentDescriptionAttribute = new Attribute("contentDescription", String.valueOf(view.getContentDescription()));
        attributes.add(contentDescriptionAttribute);
        return attributes;
    }


    private static String formatLayoutParam(int layoutParam, int size) {
        String dp = ViewKnife.px2dipStr(size);
        if (layoutParam == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return String.format("wrap_content (%s)", dp);
        }
        if (layoutParam == ViewGroup.LayoutParams.MATCH_PARENT) {
            return String.format("match_parent (%s)", dp);
        }
        return dp;
    }

    private static String formatVisibility(int value) {
        if (value == View.VISIBLE) {
            return "VISIBLE";
        }
        if (value == View.INVISIBLE) {
            return "INVISIBLE";
        }
        if (value == View.GONE) {
            return "GONE";
        }
        return "OTHER";
    }

    private static String formatDrawable(Drawable drawable) {
        if (drawable == null) {
            return "null";
        }
        if (drawable instanceof ColorDrawable) {
            return String.format("#%06X", (((ColorDrawable) drawable).getColor()));
        }
        return drawable.toString();
    }
}
