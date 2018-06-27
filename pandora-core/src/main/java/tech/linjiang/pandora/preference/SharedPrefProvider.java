package tech.linjiang.pandora.preference;

import android.content.Context;

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
        String rootPath = context.getApplicationInfo().dataDir + "/shared_prefs";
        File root = new File(rootPath);
        if (root.exists()) {
            for (File file : root.listFiles()) {
                if (file.getName().endsWith(DEF_SUFFIX)) {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
