package tech.linjiang.pandora.network;

/**
 * Created by linjiang on 2018/6/21.
 * <p>
 * event emits on UI thread after data was inserted.
 */

public interface NetStateListener {

    void onRequestStart(long id);

    void onRequestEnd(long id);
}
