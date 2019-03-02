package tech.linjiang.pandora.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/3.
 */

class CacheDatabase extends SQLiteOpenHelper {

    private static final String TAG = "CacheDatabase";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pandora.db";

    private static final List<Class> tables = new ArrayList<>();

    static {
        tables.add(Summary.class);
        tables.add(Content.class);
        tables.add(Crash.class);
        tables.add(History.class);
    }

    private CacheDatabase() {
        super(Utils.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class table : tables) {
            db.execSQL(assembleCreateSQL(table));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Class table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + getTableName(table));
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final CacheDatabase INSTANCE = new CacheDatabase();

    private static SQLiteDatabase getWDb() {
        return INSTANCE.getWritableDatabase();
    }

    private static SQLiteDatabase getRDb() {
        return INSTANCE.getReadableDatabase();
    }

    private static String getTableName(Class<?> clazz) {
        return clazz.getAnnotation(Table.class).value();
    }


    private static String assembleCreateSQL(Class<?> clazz) {
        StringBuilder sql = new StringBuilder();
        String preSql = "CREATE TABLE " + getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = field.getAnnotation(Column.class);
            if (columnName == null) {
                continue;
            }
            if (columnName.primaryKey()) {
                preSql += " (" + columnName.value() + " INTEGER PRIMARY KEY";
                continue;
            }
            sql.append(", ");
            sql.append(String.format("%s %s", columnName.value(), type2String(field.getType())));
        }
        sql.append(")");
        sql.insert(0, preSql);
        Log.i(TAG, "assembleCreateSQL: " + sql);

        return sql.toString();
    }

    private static String type2String(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return "INTEGER";
        } else if (type == float.class || type == Float.class) {
            return "REAL";
        } else if (type == double.class || type == Double.class) {
            return "REAL";
        } else if (type == long.class || type == Long.class) {
            return "INTEGER";
        } else if (type == short.class || type == Short.class) {
            return "INTEGER";
        } else if (type == byte[].class || type == Byte[].class) {
            return "BLOB";
        } else {
            return "TEXT";
        }
    }

    static void delete(Class<?> clazz) {
        getWDb().delete(getTableName(clazz), null, null);
//        getWDb().execSQL("DELETE FROM " + getTableName(clazz));
    }

    static void update(Object obj) {
        ContentValues values = new ContentValues();
        String[] primaryKey = new String[2];
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = field.getAnnotation(Column.class);
            if (columnName == null) {
                continue;
            }
            if (columnName.primaryKey()) {
                try {
                    primaryKey[0] = columnName.value();
                    primaryKey[1] = String.valueOf(field.get(obj));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                continue;
            }
            try {
                if (field.get(obj) == null) {
                    continue;
                }
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    values.put(columnName.value(), field.getInt(obj));
                } else if (field.getType() == float.class || field.getType() == Float.class) {
                    values.put(columnName.value(), field.getFloat(obj));
                } else if (field.getType() == double.class || field.getType() == Double.class) {
                    values.put(columnName.value(), field.getDouble(obj));
                } else if (field.getType() == long.class || field.getType() == Long.class) {
                    values.put(columnName.value(), field.getLong(obj));
                } else if (field.getType() == short.class || field.getType() == Short.class) {
                    values.put(columnName.value(), field.getShort(obj));
                } else if (field.getType() == byte[].class || field.getType() == Byte[].class) {
                    values.put(columnName.value(), (byte[]) field.get(obj));
                } else if (field.getType() == String.class) {
                    values.put(columnName.value(), (String) field.get(obj));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        try {
            getWDb().updateWithOnConflict(getTableName(obj.getClass()),
                    values, primaryKey[0] + " = ?", new String[]{primaryKey[1]}, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Throwable t) {
            // For example: Empty values, no need update.
            Log.w(TAG, "error when update: ", t);
        }
    }

    static long insert(Object obj) {
        ContentValues values = new ContentValues();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = field.getAnnotation(Column.class);
            if (columnName == null) {
                continue;
            }
            if (columnName.primaryKey()) {
                continue;
            }
            try {
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    values.put(columnName.value(), field.getInt(obj));
                } else if (field.getType() == float.class || field.getType() == Float.class) {
                    values.put(columnName.value(), field.getFloat(obj));
                } else if (field.getType() == double.class || field.getType() == Double.class) {
                    values.put(columnName.value(), field.getDouble(obj));
                } else if (field.getType() == long.class || field.getType() == Long.class) {
                    values.put(columnName.value(), field.getLong(obj));
                } else if (field.getType() == short.class || field.getType() == Short.class) {
                    values.put(columnName.value(), field.getShort(obj));
                } else if (field.getType() == byte[].class || field.getType() == Byte[].class) {
                    values.put(columnName.value(), (byte[]) field.get(obj));
                } else if (field.getType() == String.class) {
                    values.put(columnName.value(), (String) field.get(obj));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (values.size() > 0) {
            return getWDb().insertWithOnConflict(getTableName(obj.getClass()), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        return -1;
    }

    static <T> List<T> queryList(Class<T> clazz, String condition, String suffix) {
        List<T> result = new ArrayList<>();
        String sql = "select * from " + getTableName(clazz);
        if (!TextUtils.isEmpty(condition)) {
            sql +=  " where " + condition;
        }
        if (!TextUtils.isEmpty(suffix)) {
            sql += " " + suffix;
        }
        Log.i(TAG, "queryList: " + sql);
        try {
            Cursor cursor = CacheDatabase.getRDb().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Object obj = clazz.newInstance();
                assemble(obj, cursor);
                result.add((T) obj);
            }
            cursor.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static void assemble(Object obj, Cursor cursor) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Column columnName = field.getAnnotation(Column.class);
            if (columnName == null) {
                continue;
            }
            if (field.getType() == int.class || field.getType() == Integer.class) {
                int value = cursor.getInt(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == float.class || field.getType() == Float.class) {
                float value = cursor.getFloat(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                double value = cursor.getDouble(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                long value = cursor.getLong(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == short.class || field.getType() == Short.class) {
                short value = cursor.getShort(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == byte[].class || field.getType() == Byte[].class) {
                byte[] value = cursor.getBlob(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            } else if (field.getType() == String.class) {
                String value = cursor.getString(cursor.getColumnIndex(columnName.value()));
                field.set(obj, value);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String value();
        boolean primaryKey() default false;;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String value();
    }
}
