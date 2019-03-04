package tech.linjiang.pandora;

import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;
import tech.linjiang.pandora.function.IFunc;
import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.preference.protocol.IProvider;

/**
 * Created by linjiang on 29/05/2018.
 */
public final class Pandora {


    public static Pandora get() {
        return new Pandora();
    }

    private Pandora() {
    }

    public OkHttpInterceptor getInterceptor() {
        return new OkHttpInterceptor();
    }

    public void addDbDriver(IDriver<? extends IDescriptor> driver) {
    }

    public void addSpProvider(IProvider provider) {
    }

    public void addViewParser(IParser parser) {
    }

    public void addFunction(IFunc func) {
    }

    public void open() {
    }

    public void close() {
    }

    public void disableShakeSwitch() {
    }
}
