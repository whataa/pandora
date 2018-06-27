package tech.linjiang.pandora.util;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import tech.linjiang.pandora.core.R;

/**
 * Created by linjiang on 03/06/2018.
 */
public class SimpleTask<Params, Result> extends AsyncTask<Params, Void, Result> {

    private static final String TAG = "SimpleTask";

    // On some low memory phones, the GC is very frequent, which causes the object to be released
//    private WeakReference<Callback<Params, Result>> callbackRef;
    private Callback<Params, Result> callback;

    private Callback<Params, Result> getCallback() {
//        if (callbackRef != null) {
//            Callback<Params, Result> callback = callbackRef.get();
//            if (callback != null) {
//                return callback;
//            }
//        }
//        return null;
        return callback;
    }

    public SimpleTask(Callback<Params, Result> callback) {
//        this.callbackRef = new WeakReference<>(callback);
        this.callback = callback;
    }

    @Override
    protected final void onPreExecute() {

    }

    @Override
    protected final Result doInBackground(Params[] params) {
        if (getCallback() != null) {
            try {
                return getCallback().doInBackground(params);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            Log.w(TAG, "doInBackground: getCallback() == null");
        }
        return null;
    }

    @Override
    protected final void onPostExecute(Result result) {
        if (getCallback() != null) {
            try {
                getCallback().onPostExecute(result);
            } catch (Throwable t) {
                t.printStackTrace();
            }
//            if (callbackRef != null) {
//                callbackRef.clear();
//                callbackRef = null;
//            }
            callback = null;
        } else {
            Log.w(TAG, "onPostExecute: getCallback() == null");
        }
    }

    public interface Callback<T, K> {
        K doInBackground(T[] params);
        void onPostExecute(K result);
    }
}
