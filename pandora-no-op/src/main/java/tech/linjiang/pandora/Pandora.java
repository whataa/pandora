package tech.linjiang.pandora;

import android.app.Application;

import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.SharedPref;

/**
 * Created by linjiang on 29/05/2018.
 */
public class Pandora {


    public static Pandora init(Application application) {
        return new Pandora();
    }

    public static Pandora get() {
        return new Pandora();
    }

    private Pandora() {
    }

    public OkHttpInterceptor getInterceptor() {
        return new OkHttpInterceptor();
    }

    public Pandora enableNetwork(boolean use) {
        return this;
    }

    public Pandora enableSandbox(boolean use) {
        return this;
    }

    public Pandora enableUiInspect(boolean use) {
        return this;
    }

    public Databases getDatabases() {
        return new Databases();
    }

    public SharedPref getSharedPref() {
        return new SharedPref();
    }

    public Pandora enableShakeOpen() {
        return this;
    }

    public Pandora enableShakeOpen(int threshold) {
        return this;
    }

    public AttrFactory getAttrFactory() {
        return new AttrFactory();
    }


    public void open() {
    }

    public void close() {
    }

}
