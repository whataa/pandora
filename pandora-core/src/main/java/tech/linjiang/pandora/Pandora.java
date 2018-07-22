package tech.linjiang.pandora;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;

import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.SharedPref;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.OnEntranceClick;
import tech.linjiang.pandora.ui.connector.SimpleActivityLifecycleCallbacks;
import tech.linjiang.pandora.ui.view.EntranceView;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 29/05/2018.
 */
@SuppressLint("StaticFieldLeak")
public class Pandora {
    private static final String TAG = "Pandora";

    private static Pandora INSTANCE;

    public static Pandora init(Application application) {
        INSTANCE = new Pandora();
        Utils.init(application);
        application.registerActivityLifecycleCallbacks(INSTANCE.callbacks);
        return INSTANCE;
    }

    public static Pandora get() {
        if (INSTANCE == null) {
            throw new RuntimeException("need to call Pandora#init in Application#onCreate firstly.");
        }
        return INSTANCE;
    }

    public Pandora enableNetwork(boolean use) {
        entranceView.enableNetwork(use);
        return this;
    }

    public Pandora enableSandbox(boolean use) {
        entranceView.enableSandbox(use);
        return this;
    }

    public Pandora enableUiInspect(boolean use) {
        entranceView.enableUiInspect(use);
        return this;
    }

    private Pandora() {
        entranceView.setOnClickListener(new OnEntranceClick() {
            @Override
            protected void onClick(int type) {
                preventFree = true;
                super.onClick(type);
            }
        });
    }

    private final EntranceView entranceView = new EntranceView(Utils.getContext());
    private OkHttpInterceptor interceptor = new OkHttpInterceptor();
    private final Databases databases = new Databases();
    private final SharedPref sharedPref = new SharedPref();
    private final AttrFactory attrFactory = new AttrFactory();
    private Activity bottomActivity;
    private String curActivityName;
    // let dispatcher doesn't looks like an activity
    private boolean preventFree;

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

    // hide
    public View getViewRoot() {
        if (bottomActivity != null) {
            return bottomActivity.getWindow().peekDecorView();
        }
        return null;
    }

    // hide
    public Activity getBottomActivity() {
        return bottomActivity;
    }

    public void open() {
        if (Utils.checkPermission()) {
            entranceView.open();
        }
    }

    public void close() {
        if (Utils.checkPermission()) {
            entranceView.close();
        }
    }

    public Pandora enableShakeOpen() {
        Utils.registerSensor(sensorEventListener);
        return this;
    }


    private SimpleActivityLifecycleCallbacks callbacks = new SimpleActivityLifecycleCallbacks() {
        private int count;

        @Override
        public void onActivityStarted(Activity activity) {
            super.onActivityStarted(activity);
            count++;
            if (count == 1) {
                INSTANCE.entranceView.show();
            }
            if (activity instanceof Dispatcher) {
                INSTANCE.entranceView.hide();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);
            if (!(activity instanceof Dispatcher)) {
                INSTANCE.bottomActivity = activity;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            super.onActivityPaused(activity);
            if (activity == INSTANCE.bottomActivity) {
                if (!INSTANCE.preventFree) {
                    INSTANCE.bottomActivity = null;
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            super.onActivityStopped(activity);
            count--;
            if (count <= 0) {
                INSTANCE.entranceView.hide();
            } else {
                if (activity instanceof Dispatcher) {
                    INSTANCE.entranceView.show();
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            super.onActivityDestroyed(activity);
            if (activity instanceof Dispatcher) {
                INSTANCE.preventFree = false;
            }
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                // app-window will only receive event at the top
                if (Utils.checkIfShake(
                        event.values[0],
                        event.values[1],
                        event.values[2])) {
                    if (!INSTANCE.entranceView.isOpen()) {
                        INSTANCE.open();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


}
