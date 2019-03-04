package tech.linjiang.pandora;

import android.app.Activity;
import android.app.Application;
import android.support.v4.content.FileProvider;

import tech.linjiang.pandora.crash.CrashHandler;
import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;
import tech.linjiang.pandora.function.IFunc;
import tech.linjiang.pandora.history.HistoryRecorder;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.SharedPref;
import tech.linjiang.pandora.preference.protocol.IProvider;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 29/05/2018.
 */
public final class Pandora extends FileProvider {

    private static Pandora INSTANCE;

    @Override
    public boolean onCreate() {
        INSTANCE = this;
        init(((Application) getContext()));
        return super.onCreate();
    }

    private void init(Application app) {
        Utils.init(app);
        funcController = new FuncController(app);
        interceptor = new OkHttpInterceptor();
        databases = new Databases();
        sharedPref = new SharedPref();
        attrFactory = new AttrFactory();
        crashHandler = new CrashHandler();
        historyRecorder = HistoryRecorder.register(app);
    }

    public static Pandora get() {
        return INSTANCE;
    }

    private OkHttpInterceptor interceptor;
    private Databases databases;
    private SharedPref sharedPref;
    private AttrFactory attrFactory;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;

    public OkHttpInterceptor getInterceptor() {
        return interceptor;
    }

    public void addDbDriver(IDriver<? extends IDescriptor> driver) {
        databases.addDriver(driver);
    }

    public void addSpProvider(IProvider provider) {
        sharedPref.addProvider(provider);
    }

    public void addViewParser(IParser parser) {
        attrFactory.addParser(parser);
    }

    // hide
    public Activity getTopActivity() {
        return historyRecorder.getTopActivity();
    }

    public void addFunction(IFunc func) {
        funcController.addFunc(func);
    }

    public void open() {
        if (Utils.checkPermission()) {
            funcController.open();
        }
    }

    public void close() {
        funcController.close();
    }

    public void disableShakeSwitch() {
        funcController.disable();
    }
}
