package tech.linjiang.pandora.inspector.attribute;

import android.view.View;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.inspector.attribute.parser.ImageViewParser;
import tech.linjiang.pandora.inspector.attribute.parser.ViewGroupParser;
import tech.linjiang.pandora.inspector.attribute.parser.TextViewParser;
import tech.linjiang.pandora.inspector.attribute.parser.ViewParser;
import tech.linjiang.pandora.inspector.model.Attribute;

/**
 * Created by linjiang on 15/06/2018.
 */

public final class AttrFactory {


    private List<IParser> parsers = new ArrayList<IParser>() {
        {
            add(new ImageViewParser());
            add(new TextViewParser());
            add(new ViewGroupParser());
            add(new ViewParser());
        }
    };

    public void addParser(IParser parser) {
        parsers.add(0, parser);
    }

    public List<Attribute> parse(View v) {
        List<Attribute> attributes = new ArrayList<>();
        for (IParser parser : parsers) {
            if (parser != null) {
                try {
                    ParameterizedType parameterizedType =
                            (ParameterizedType) parser.getClass().getGenericInterfaces()[0];
                    Type actualTypeArguments = parameterizedType.getActualTypeArguments()[0];
                    if (findUpUntilEqual(v.getClass(), actualTypeArguments)) {
                        List<Attribute> result = parser.getAttrs(v);
                        if (result != null && !result.isEmpty()) {
                            for (int i = 0; i < result.size(); i++) {
                                result.get(i).category = actualTypeArguments.toString();
                            }
                            attributes.addAll(result);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return attributes;
    }
    private boolean findUpUntilEqual(Class clazz, Type type) {
        do {
            if (type == clazz) {
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
        return false;
    }
}
