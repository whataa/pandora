package tech.linjiang.pandora.ui.connector;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        Type.NET,
        Type.FILE,
        Type.HIERARCHY,
        Type.BASELINE,
        Type.SELECT,
        Type.ATTR
})
@Retention(RetentionPolicy.SOURCE)
public @interface Type {
    int NET = 0x01;
    int FILE = 0x02;
    int HIERARCHY = 0x03;
    int BASELINE = 0x05;
    int SELECT = 0x06;
    int ATTR = 0x07;
}