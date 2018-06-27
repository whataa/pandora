package tech.linjiang.android.pandora;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.view.View;

import java.io.File;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.database.DatabaseResult;
import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;
import tech.linjiang.pandora.inspector.attribute.IParser;
import tech.linjiang.pandora.inspector.model.Attribute;
import tech.linjiang.pandora.preference.protocol.IProvider;

/**
 * Created by linjiang on 30/05/2018.
 */

public class MyApp extends Application {

    private static Application mThis;

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        Pandora.init(this).enableShakeOpen();
    }

    public static Application getContext() {
        return mThis;
    }
}
