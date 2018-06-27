package tech.linjiang.pandora.ui.recyclerview;

import android.support.annotation.LayoutRes;

/**
 * Created by linjiang on 03/06/2018.
 */

public abstract class BaseItem<T> {

    public T data;

    public BaseItem(T data) {
        this.data = data;
    }

    public abstract void onBinding(int position, UniversalAdapter.ViewPool pool, T data);

    public abstract @LayoutRes int getLayout();

    private Object tag;

    public final void setTag(Object tag) {
        this.tag = tag;
    }

    public final Object getTag() {
        return tag;
    }
}
