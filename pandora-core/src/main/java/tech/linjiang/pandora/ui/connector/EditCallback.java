package tech.linjiang.pandora.ui.connector;

import java.io.Serializable;

/**
 * Created by linjiang on 06/06/2018.
 */

public interface EditCallback extends Serializable {
    void onValueChanged(String value);
}
