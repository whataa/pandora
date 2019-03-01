package tech.linjiang.pandora;

import android.app.Application;
import android.support.v4.content.FileProvider;

/**
 * Created by linjiang on 2018/6/26.
 * <p>
 * Third-party apps cannot directly open the internal files of other apps,
 * so we will first copy the files to the external SD card and perform open operations.
 * <p>
 * Avoid other libraries also declare fileProvider in manifest
 */

public class PdFileProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        if (getContext() instanceof Application) {
            Pandora.init(((Application) getContext()));
        }
        return super.onCreate();
    }
}
