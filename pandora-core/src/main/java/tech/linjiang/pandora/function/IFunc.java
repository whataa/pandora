package tech.linjiang.pandora.function;

import android.support.annotation.DrawableRes;

/**
 * Created by linjiang on 2019/3/4.
 */

public interface IFunc {

    @DrawableRes int getIcon();
    String getName();
    void onClick();
}
