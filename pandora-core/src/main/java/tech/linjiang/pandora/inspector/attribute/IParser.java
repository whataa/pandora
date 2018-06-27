package tech.linjiang.pandora.inspector.attribute;

import android.view.View;

import java.util.List;

import tech.linjiang.pandora.inspector.model.Attribute;

/**
 * Created by linjiang on 2018/6/19.
 * <p>
 * T is the supported View type and its subclasses are also parsed
 */

public interface IParser<T extends View> {
    List<Attribute> getAttrs(T view);

}
