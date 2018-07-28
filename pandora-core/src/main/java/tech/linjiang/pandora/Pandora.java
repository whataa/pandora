package tech.linjiang.pandora;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.inspector.CurInfoView;
import tech.linjiang.pandora.inspector.GridLineView;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.SharedPref;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.OnEntranceClick;
import tech.linjiang.pandora.ui.connector.SimpleActivityLifecycleCallbacks;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.ui.view.EntranceView;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 29/05/2018.
 */
@SuppressLint("StaticFieldLeak")
public final class Pandora {

    private static Pandora INSTANCE;


    static void init(Application application) {
        INSTANCE = new Pandora();
        Utils.init(application);
        application.registerActivityLifecycleCallbacks(INSTANCE.callbacks);
        application.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {

            }
        });
        Utils.registerSensor(INSTANCE.sensorEventListener);
    }

    public static Pandora get() {
        return INSTANCE;
    }

    private Pandora() {
        EntranceView.setListener(new OnEntranceClick() {
            @Override
            protected void onClick(int type) {
                if (type == Type.GRID) {
                    GridLineView.toggle();
                    return;
                } else if (type == Type.WINDOW) {
                    CurInfoView.toggle();
                    return;
                }
                preventFree = true;
                super.onClick(type);
            }
        });
    }

    private final OkHttpInterceptor interceptor = new OkHttpInterceptor();
    private final Databases databases = new Databases();
    private final SharedPref sharedPref = new SharedPref();
    private final AttrFactory attrFactory = new AttrFactory();
    private Activity bottomActivity;
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
    public Activity getBottomActivity() {
        return bottomActivity;
    }

    public void open() {
        if (Utils.checkPermission()) {
            EntranceView.open();
        }
    }

    public void close() {
        if (Utils.checkPermission()) {
            EntranceView.close();
        }
    }

    private final SimpleActivityLifecycleCallbacks callbacks = new SimpleActivityLifecycleCallbacks() {
        private int count;

        @Override
        public void onActivityStarted(Activity activity) {
            super.onActivityStarted(activity);
            count++;
            if (count == 1) {
                showOverlays();
            }
            if (activity instanceof Dispatcher) {
                EntranceView.hide();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);
            if (!(activity instanceof Dispatcher)) {
                INSTANCE.bottomActivity = activity;
            }
            CurInfoView.updateText(activity.getClass().getName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            super.onActivityPaused(activity);
            if (activity == INSTANCE.bottomActivity) {
                if (!INSTANCE.preventFree) {
                    INSTANCE.bottomActivity = null;
                }
            }
            CurInfoView.updateText(null);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            super.onActivityStopped(activity);
            count--;
            if (count <= 0) {
                hideOverlays();
            } else {
                if (activity instanceof Dispatcher) {
                    EntranceView.show();
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

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (Config.getSHAKE_SWITCH()) {
                if (event.sensor.getType() == 1) {
                    // app-window will only receive event at the top
                    if (Utils.checkIfShake(
                            event.values[0],
                            event.values[1],
                            event.values[2])) {
                        EntranceView.open();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    private void showOverlays() {
        EntranceView.show();
        CurInfoView.show();
        GridLineView.show();
    }

    private void hideOverlays() {
        EntranceView.hide();
        CurInfoView.hide();
        GridLineView.hide();
    }
}
