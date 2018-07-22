package tech.linjiang.pandora.preference;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.preference.protocol.IProvider;

/**
 * Created by linjiang on 04/06/2018.
 */

public class SharedPrefProvider implements IProvider {

    @Override
    public List<File> getSharedPrefFiles(Context context) {
        List<File> files = new ArrayList<>();
        buildWithPath(context.getApplicationInfo().dataDir, files);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            buildWithPath(context.getApplicationInfo().deviceProtectedDataDir, files);
        }
        return files;
    }

    private void buildWithPath(String path, List<File> container) {
        File root = new File(path + "/shared_prefs");
        if (root.exists()) {
            for (File file : root.listFiles()) {
                if (file.getName().endsWith(DEF_SUFFIX)) {
                    container.add(file);
                }
            }
        }
    }
}
