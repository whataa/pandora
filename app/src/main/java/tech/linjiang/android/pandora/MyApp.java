package tech.linjiang.android.pandora;

import android.app.Application;
import android.os.Build;

import tech.linjiang.android.pandora.db.StoreDatabase;
import tech.linjiang.pandora.Pandora;

/**
 * Created by linjiang on 30/05/2018.
 */

public class MyApp extends Application {

    private static Application mThis;

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        Pandora.init(this).enableShakeOpen(650);
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
