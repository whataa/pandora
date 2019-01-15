package tech.linjiang.android.pandora;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import tech.linjiang.android.pandora.db.StoreDatabase;
import tech.linjiang.android.pandora.net.JsonFormatterImpl;
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
//        strictMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // the store.db and testAllType.xml if exist will be moved to the Device encrypted storage area.
            createDeviceProtectedStorageContext().moveDatabaseFrom(this, StoreDatabase.NAME);
            createDeviceProtectedStorageContext().moveSharedPreferencesFrom(this, "testAllType");
        }

        // choose one to let your network json result looks more pretty
        Pandora.get().getInterceptor().setJsonFormatter(new JsonFormatterImpl());
//        Pandora.get().getInterceptor().setJsonFormatter(new GsonFormatterImpl());
    }

    public static Application getContext() {
        return mThis;
    }

    private void strictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDialog()
                .penaltyLog()
                .penaltyFlashScreen()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
