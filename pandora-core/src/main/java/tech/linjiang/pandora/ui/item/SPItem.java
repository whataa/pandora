package tech.linjiang.pandora.ui.item;

import java.io.File;

/**
 * Created by linjiang on 05/06/2018.
 */

public class SPItem extends NameItem {
    public File descriptor;

    public SPItem(String data, File descriptor) {
        super(data);
        this.descriptor = descriptor;
    }
}
