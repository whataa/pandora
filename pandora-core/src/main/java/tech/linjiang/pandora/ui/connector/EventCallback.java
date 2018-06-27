package tech.linjiang.pandora.ui.connector;

import java.io.Serializable;

/**
 * Created by linjiang on 07/06/2018.
 */

public interface EventCallback extends Serializable {
    void onComplete();
}
