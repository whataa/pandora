package tech.linjiang.pandora.preference.protocol;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * Created by linjiang on 04/06/2018.
 */

public interface IProvider {

    List<File> getSharedPrefFiles(Context context);
}
