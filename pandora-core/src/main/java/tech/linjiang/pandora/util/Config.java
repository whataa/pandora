package tech.linjiang.pandora.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by linjiang on 2018/6/23.
 */

public class Config {

    private final static String NAME = "pd_config";
    private static final String KEY_NET = "key_net";

    public static void setNetLogEnable(boolean enable) {
        getSp().edit()
                .putBoolean(KEY_NET, enable)
                .apply();
    }

    public static boolean isNetLogEnable() {
        return getSp().getBoolean(KEY_NET, false);
    }

    private static SharedPreferences getSp() {
        return Utils.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }
}
