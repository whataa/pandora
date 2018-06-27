package tech.linjiang.pandora.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.database.protocol.IProvider;

/**
 * Created by linjiang on 29/05/2018.
 */

class DatabaseProvider implements IProvider {
    private Context context;

    public DatabaseProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<File> getDatabaseFiles() {
        List<File> databaseFiles = new ArrayList<>();
        for (String databaseName : context.databaseList()) {
            databaseFiles.add(context.getDatabasePath(databaseName));
        }
        return databaseFiles;
    }

    @Override
    public SQLiteDatabase openDatabase(File databaseFile) throws SQLiteException {
        return performOpen(databaseFile, checkIfCanOpenWithWAL(databaseFile));
    }

    private int checkIfCanOpenWithWAL(File databaseFile) {
        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            File walFile = new File(databaseFile.getParent(), databaseFile.getName() + "-wal");
            if (walFile.exists()) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
        }
        return flags;
    }

    private SQLiteDatabase performOpen(File databaseFile, int options) {
        int flags = SQLiteDatabase.OPEN_READWRITE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if ((options & SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING) != 0) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, flags);
        return db;
    }
}
