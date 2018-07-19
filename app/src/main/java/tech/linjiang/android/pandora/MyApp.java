package tech.linjiang.android.pandora;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.view.View;

import java.io.File;
import java.util.List;

import tech.linjiang.android.pandora.db.StoreDatabase;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.database.DatabaseResult;
import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;
import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.inspector.model.Attribute;
import tech.linjiang.pandora.preference.protocol.IProvider;

/**
 * Created by linjiang on 30/05/2018.
 */

public class MyApp extends Application {

    private static Application mThis;

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        Pandora.init(this).enableShakeOpen();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // the store.db and testAllType.xml if exist will be moved to the Device encrypted storage area.
            createDeviceProtectedStorageContext().moveDatabaseFrom(this, StoreDatabase.NAME);
            createDeviceProtectedStorageContext().moveSharedPreferencesFrom(this, "testAllType");
        }
    }

    public static Application getContext() {
        return mThis;
    }
}
