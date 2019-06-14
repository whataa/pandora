package tech.linjiang.pandora.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by linjiang on 2019/3/5.
 */

public class SensorDetector implements SensorEventListener {

    private static long lastCheckTime;
    private static float[] lastXyz = new float[3];
    private Callback callback;

    public SensorDetector(Callback callback) {
        if (callback != null) {
            register();
            this.callback = callback;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Config.getSHAKE_SWITCH()) {
            if (event.sensor.getType() == 1) {
                // app-window will only receive event at the top
                if (checkIfShake(
                        event.values[0],
                        event.values[1],
                        event.values[2])) {
                    Utils.cancelTask(task);
                    Utils.postDelayed(task, 150);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void register() {
        try {
            SensorManager manager = (SensorManager) Utils.getContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void unRegister() {
        try {
            SensorManager manager = (SensorManager) Utils.getContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.unregisterListener(this, sensor);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (callback != null) {
                callback.shakeValid();
            }
        }
    };


    private static boolean checkIfShake(float x, float y, float z) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastCheckTime;
        if (diffTime < 100) {
            return false;
        }
        lastCheckTime = currentTime;
        float deltaX = x - lastXyz[0];
        float deltaY = y - lastXyz[1];
        float deltaZ = z - lastXyz[2];
        lastXyz[0] = x;
        lastXyz[1] = y;
        lastXyz[2] = z;
        int delta = (int) (Math.sqrt(deltaX * deltaX
                + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
        return delta > Config.getSHAKE_THRESHOLD();
    }

    public interface Callback {
        void shakeValid();
    }

}
