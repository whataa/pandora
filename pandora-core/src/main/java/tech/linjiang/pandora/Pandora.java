package tech.linjiang.pandora;

import android.app.Activity;
import android.app.Application;
import androidx.core.content.FileProvider;
import android.content.Context;

import tech.linjiang.pandora.crash.CrashHandler;
import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.function.IFunc;
import tech.linjiang.pandora.history.HistoryRecorder;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.SharedPref;
import tech.linjiang.pandora.util.SensorDetector;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 29/05/2018.
 */
public final class Pandora extends FileProvider implements SensorDetector.Callback {

    private static Pandora INSTANCE;

    public Pandora() {
        if (INSTANCE != null) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean onCreate() {
        INSTANCE = this;
        Context context = Utils.makeContextSafe(getContext());
        init(((Application) context));
        return super.onCreate();
    }

    private void init(Application app) {
        Utils.init(app);
        funcController = new FuncController(app);
        sensorDetector = new SensorDetector(notHostProcess ? null : this);
        interceptor = new OkHttpInterceptor();
        databases = new Databases();
        sharedPref = new SharedPref();
        attrFactory = new AttrFactory();
        crashHandler = new CrashHandler(app);
        historyRecorder = new HistoryRecorder(app);
    }

    public static Pandora get() {
        if (INSTANCE == null) {
            // Not the host process
            Pandora pandora = new Pandora();
            pandora.notHostProcess = true;
            pandora.onCreate();
        }
        return INSTANCE;
    }

    private boolean notHostProcess;
    private OkHttpInterceptor interceptor;
    private Databases databases;
    private SharedPref sharedPref;
    private AttrFactory attrFactory;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;
    private SensorDetector sensorDetector;

    public OkHttpInterceptor getInterceptor() {
        return interceptor;
    }

    public Databases getDatabases() {
        return databases;
    }

    public SharedPref getSharedPref() {
        return sharedPref;
    }

    public AttrFactory getAttrFactory() {
        return attrFactory;
    }

    /**
     * @hide
     */
    public Activity getTopActivity() {
        return historyRecorder.getTopActivity();
    }

    /**
     * Add a custom entry to the panel.
     * also see @{@link tech.linjiang.pandora.function.IFunc}
     *
     * @param func
     */
    public void addFunction(IFunc func) {
        funcController.addFunc(func);
    }

    /**
     * Open the panel.
     */
    public void open() {
        if (notHostProcess) {
            return;
        }
        funcController.open();
    }

    /**
     * Close the panel.
     */
    public void close() {
        funcController.close();
    }

    /**
     * Disable the Shake feature.
     */
    public void disableShakeSwitch() {
        sensorDetector.unRegister();
    }

    @Override
    public void shakeValid() {
        open();
    }
}
