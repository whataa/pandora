package tech.linjiang.pandora.ui.item;

/**
 * Created by linjiang on 05/06/2018.
 */

public class DBItem extends NameItem {

    public int key;

    public DBItem(String data, int key) {
        super(data);
        this.key = key;
    }
}
