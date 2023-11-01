package tech.linjiang.pandora.sandbox;

import static tech.linjiang.pandora.util.FileUtil.sortFiles;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 04/06/2018.
 */

public class Sandbox {

    public static List<File> getRootFiles() {
        List<File> files = getFiles(new File(Utils.getContext().getApplicationInfo().dataDir));
        return sortFiles(files);
    }

    @Nullable
    public static List<File> getExternalFiles() {
        File externalFilesDir = Utils.getContext().getExternalFilesDir("");
        if (externalFilesDir == null || externalFilesDir.getParentFile() == null) {
            return null;
        }
        return sortFiles(getFiles(externalFilesDir.getParentFile()));
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static List<File> getDPMFiles() {
        return sortFiles(getFiles(new File(Utils.getContext().getApplicationInfo().deviceProtectedDataDir)));
    }

    public static List<File> getFiles(File curFile) {
        List<File> descriptors = new ArrayList<>();
        if (curFile.isDirectory() && curFile.exists()) {
            descriptors.addAll(Arrays.asList(curFile.listFiles()));
        }
        return sortFiles(descriptors);
    }
}
