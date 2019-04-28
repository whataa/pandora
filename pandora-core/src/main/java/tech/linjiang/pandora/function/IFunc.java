package tech.linjiang.pandora.function;

import androidx.annotation.DrawableRes;

/**
 * Created by linjiang on 2019/3/4.
 * <p>
 * Please check @{@link tech.linjiang.pandora.Pandora#addFunction(IFunc)}
 */

public interface IFunc {

    /**
     * @return the icon of function.
     */
    @DrawableRes
    int getIcon();

    /**
     * @return the name of function.
     */
    String getName();

    /**
     * Click event.
     *
     * @return "Turn on" the state of the Func once return true, turn off otherwise.
     */
    boolean onClick();
}
