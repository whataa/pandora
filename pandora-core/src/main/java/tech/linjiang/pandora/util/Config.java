package tech.linjiang.pandora.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by linjiang on 2018/6/23.
 */

public class Config {

    private final static String NAME = "pd_config";
    private static final String KEY_NET = "key_net";

    private static final String KEY_SHAKE_SWITCH            = "key_shake_switch";
    private static final String KEY_SHAKE_THRESHOLD         = "key_shake_threshold";
    private static final String KEY_COMMON_ACTIVITY         = "key_common_activity";
    private static final String KEY_COMMON_NETWORK_SWITCH   = "key_common_network_switch";
    private static final String KEY_COMMON_SANDBOX_SWITCH   = "key_common_sandbox_switch";
    private static final String KEY_COMMON_UI_SWITCH        = "key_common_ui_switch";
    private static final String KEY_NETWORK_DELAY_REQ       = "key_network_delay_req";
    private static final String KEY_NETWORK_DELAY_RES       = "key_network_delay_res";
    private static final String KEY_SANBOX_DPM              = "key_sanbox_dpm";
    private static final String KEY_NETWORK_PAGE_SIZE       = "key_network_page_size";

    private static final boolean DEF_KEY_SHAKE_SWITCH = true;
    private static final int DEF_KEY_SHAKE_THRESHOLD = 1500;
    private static final boolean DEF_KEY_COMMON_ACTIVITY = false;
    private static final boolean DEF_KEY_COMMON_NETWORK_SWITCH = true;
    private static final boolean DEF_KEY_COMMON_SANDBOX_SWITCH = true;
    private static final boolean DEF_KEY_COMMON_UI_SWITCH = true;
    private static final long DEF_KEY_NETWORK_DELAY_REQ = 0;
    private static final long DEF_KEY_NETWORK_DELAY_RES = 0;
    private static final boolean DEF_KEY_SANBOX_DPM = false;
    private static final int DEF_KEY_NETWORK_PAGE_SIZE = 512;

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


    // =============================================================================================
    public static void reset() {
        getSp().edit().clear().commit();
    }

    public static boolean getSHAKE_SWITCH() {
        return getSp().getBoolean(KEY_SHAKE_SWITCH, DEF_KEY_SHAKE_SWITCH);
    }
    public static void setSHAKE_SWITCH(Boolean value) {
        getSp().edit()
                .putBoolean(KEY_SHAKE_SWITCH, value)
                .apply();
    }

    //

    public static int getSHAKE_THRESHOLD() {
        return getSp().getInt(KEY_SHAKE_THRESHOLD, DEF_KEY_SHAKE_THRESHOLD);
    }
    public static void setSHAKE_THRESHOLD(int value) {
        getSp().edit()
                .putInt(KEY_SHAKE_THRESHOLD, value)
                .apply();
    }

    //

    public static boolean getCOMMON_ACTIVITY() {
        return getSp().getBoolean(KEY_COMMON_ACTIVITY, DEF_KEY_COMMON_ACTIVITY);
    }
    public static void setCOMMON_ACTIVITY(boolean value) {
        getSp().edit()
                .putBoolean(KEY_COMMON_ACTIVITY, value)
                .apply();
    }

    //

    public static boolean getCOMMON_NETWORK_SWITCH() {
        return getSp().getBoolean(KEY_COMMON_NETWORK_SWITCH, DEF_KEY_COMMON_NETWORK_SWITCH);
    }
    public static void setCOMMON_NETWORK_SWITCH(boolean value) {
        getSp().edit()
                .putBoolean(KEY_COMMON_NETWORK_SWITCH, value)
                .apply();
    }

    //

    public static boolean getCOMMON_SANDBOX_SWITCH() {
        return getSp().getBoolean(KEY_COMMON_SANDBOX_SWITCH, DEF_KEY_COMMON_SANDBOX_SWITCH);
    }
    public static void setCOMMON_SANDBOX_SWITCH(boolean value) {
        getSp().edit()
                .putBoolean(KEY_COMMON_SANDBOX_SWITCH, value)
                .apply();
    }

    //

    public static boolean getCOMMON_UI_SWITCH() {
        return getSp().getBoolean(KEY_COMMON_UI_SWITCH, DEF_KEY_COMMON_UI_SWITCH);
    }
    public static void setCOMMON_UI_SWITCH(boolean value) {
        getSp().edit()
                .putBoolean(KEY_COMMON_UI_SWITCH, value)
                .apply();
    }

    //

    public static long getNETWORK_DELAY_REQ() {
        return getSp().getLong(KEY_NETWORK_DELAY_REQ, DEF_KEY_NETWORK_DELAY_REQ);
    }
    public static void setNETWORK_DELAY_REQ(long value) {
        getSp().edit()
                .putLong(KEY_NETWORK_DELAY_REQ, value)
                .apply();
    }

    //

    public static long getNETWORK_DELAY_RES() {
        return getSp().getLong(KEY_NETWORK_DELAY_RES, DEF_KEY_NETWORK_DELAY_RES);
    }
    public static void setNETWORK_DELAY_RES(long value) {
        getSp().edit()
                .putLong(KEY_NETWORK_DELAY_RES, value)
                .apply();
    }

    //

    public static boolean getSANBOX_DPM() {
        return getSp().getBoolean(KEY_SANBOX_DPM, DEF_KEY_SANBOX_DPM);
    }
    public static void setSANBOX_DPM(boolean value) {
        getSp().edit()
                .putBoolean(KEY_SANBOX_DPM, value)
                .apply();
    }

    //

    public static int getNETWORK_PAGE_SIZE() {
        return getSp().getInt(KEY_NETWORK_PAGE_SIZE, DEF_KEY_NETWORK_PAGE_SIZE);
    }
    public static void setNETWORK_PAGE_SIZE(int value) {
        getSp().edit()
                .putInt(KEY_NETWORK_PAGE_SIZE, value)
                .apply();
    }



    @IntDef({
            Type.SHAKE_SWITCH,
            Type.SHAKE_THRESHOLD,
            Type.COMMON_ACTIVITY,
            Type.COMMON_NETWORK_SWITCH,
            Type.COMMON_SANDBOX_SWITCH,
            Type.COMMON_UI_SWITCH,
            Type.NETWORK_DELAY_REQ,
            Type.NETWORK_DELAY_RES,
            Type.NETWORK_PAGE_SIZE,
            Type.SANBOX_DPM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int SHAKE_SWITCH = 0x01;
        int SHAKE_THRESHOLD = 0x02;

        int COMMON_ACTIVITY = 0x10;
        int COMMON_NETWORK_SWITCH = 0x11;
        int COMMON_SANDBOX_SWITCH = 0x12;
        int COMMON_UI_SWITCH = 0x13;

        int NETWORK_DELAY_REQ = 0x20;
        int NETWORK_DELAY_RES = 0x21;
        int NETWORK_PAGE_SIZE = 0x22;

        int SANBOX_DPM = 0x30;

    }
}
