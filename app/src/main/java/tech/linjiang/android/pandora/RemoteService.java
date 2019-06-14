package tech.linjiang.android.pandora;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.linjiang.android.pandora.net.ApiService;
import tech.linjiang.android.pandora.utils.ThreadPool;
import tech.linjiang.pandora.Pandora;

/**
 * This service is used to test the behaviors of Pandora in multiple processes
 */
public class RemoteService extends Service {

    public static void start(Context context) {
        Intent intent = new Intent(context, RemoteService.class);
        context.startService(intent);
    }
    private static final String TAG = "ProcessService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Pandora.get().open();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        ThreadPool.post(() -> {
            try {
                URL url = new URL("https://www.v2ex.com/api/topics/latest.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ApiService.HttpbinApi api = ApiService.getInstance();
        Callback<Void> cb = new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        };
        api.get().enqueue(cb);
        return super.onStartCommand(intent, flags, startId);
    }
}
