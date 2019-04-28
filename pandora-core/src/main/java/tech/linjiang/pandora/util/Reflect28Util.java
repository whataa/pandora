package tech.linjiang.pandora.util;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by linjiang on 2019/3/5.
 */

class Reflect28Util {

    static {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Class classClazz = Class.class;
                // light greyList
                Field classLoaderField = classClazz.getDeclaredField("classLoader");
                classLoaderField.setAccessible(true);
                classLoaderField.set(Reflect28Util.class, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static Field getDeclaredField(Class<?> clz, String name) throws NoSuchFieldException {
        return clz.getDeclaredField(name);
    }

    public static Method getDeclaredMethod(Class<?> clz, String name, Class<?>... parameterType)
            throws NoSuchMethodException {
        return clz.getDeclaredMethod(name, parameterType);
    }


}