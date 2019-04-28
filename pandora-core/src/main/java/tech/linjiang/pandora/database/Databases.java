package tech.linjiang.pandora.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 29/05/2018.
 */

public final class Databases {
    private static final String TAG = "Databases";

    /**
     * key:     databaseId
     * value:   IDriver & IDescriptor
     */
    private final SparseArray<DatabaseHolder> holders = new SparseArray<>();
    private final List<DriverHolder> drivers = new ArrayList<>();

    public Databases() {
        addDriver(new DatabaseDriver(new DatabaseProvider(Utils.getContext())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addDriver(new DatabaseDriver(new DatabaseProvider(Utils.getContext().createDeviceProtectedStorageContext())));
        }
    }

    public boolean addDriver(IDriver<? extends IDescriptor> driver) {
        List<? extends IDescriptor> descriptors = driver.getDatabaseNames();
        drivers.add(new DriverHolder(driver, descriptors == null ? 0 : descriptors.size()));
        return bindDriver(driver, descriptors);
    }

    private boolean bindDriver(IDriver<? extends IDescriptor> driver, List<? extends IDescriptor> descriptors) {
        if (descriptors == null || descriptors.isEmpty()) {
            return false;
        }
        for (int i = 0; i < descriptors.size(); i++) {
            holders.put(holders.size(), new DatabaseHolder(descriptors.get(i), driver));
        }
        return true;
    }

    public SparseArray<String> getDatabaseNames() {
        boolean needRebind = false;
        for (int i = 0; i < drivers.size(); i++) {
            List descriptors = drivers.get(i).driver.getDatabaseNames();
            int latestCount = descriptors == null ? 0 : descriptors.size();
            if (drivers.get(i).databaseCount != latestCount) {
                drivers.get(i).databaseCount = latestCount;
                needRebind = true;
            }
        }
        if (needRebind) {
            holders.clear();
            for (int i = 0; i < drivers.size(); i++) {
                bindDriver(drivers.get(i).driver, drivers.get(i).driver.getDatabaseNames());
            }
        }
        SparseArray<String> names = new SparseArray<>();
        for (int i = 0; i < holders.size(); i++) {
            if (holders.valueAt(i).descriptor.exist()) {
                names.put(holders.keyAt(i), holders.valueAt(i).descriptor.name());
            }
        }
        return names;
    }

    public List<String> getTableNames(int databaseId) {
        IDriver driver = holders.get(databaseId).driver;
        IDescriptor descriptor = holders.get(databaseId).descriptor;
        return driver.getTableNames(descriptor);
    }

    public DatabaseResult getTableInfo(int databaseId, String table) {
        String sql = String.format("pragma table_info(%s)", table);
        return executeSQL(databaseId, sql);
    }

    public DatabaseResult query(int databaseId, String table, String condition) {
        // TODO if ROW_ID not exists
        String sql = String.format("select "
                + Column.ROW_ID + " as " + Column.ROW_ID + ", * from %s", table);
        if (!TextUtils.isEmpty(condition)) {
            sql = sql.concat(" where ").concat(condition);
        }
        return executeSQL(databaseId, sql);
    }

    public DatabaseResult update(int databaseId,
                                 String table,
                                 String primaryKey,
                                 String primaryValue,
                                 String key,
                                 String value) {
        String sql = String.format("update %s set %s = '%s' where %s = '%s'",
                table, key, value, primaryKey, primaryValue);
        return executeSQL(databaseId, sql);
    }

    public DatabaseResult insert(int databaseId, String table, ContentValues values) {
        Iterator<String> sets = values.keySet().iterator();
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        while (sets.hasNext()) {
            String key = sets.next();
            if (values.getAsString(key) == null) {
                // distinguish NULL and ''
                continue;
            }
            keyBuilder.append(key);
            valueBuilder.append("'").append(values.getAsString(key)).append("'");
            keyBuilder.append(",");
            valueBuilder.append(",");
        }
        if (keyBuilder.length() > 0) {
            keyBuilder.deleteCharAt(keyBuilder.lastIndexOf(","));
            valueBuilder.deleteCharAt(valueBuilder.lastIndexOf(","));
        }
        String sql = String.format("insert into %s (%s) values (%s)",
                table, keyBuilder.toString(), valueBuilder.toString());
        return executeSQL(databaseId, sql);
    }

    public DatabaseResult delete(int databaseId,
                                 String table,
                                 String primaryKey,
                                 String primaryValue) {
        String sql = String.format("delete from %s", table);
        if (!TextUtils.isEmpty(primaryKey) && !TextUtils.isEmpty(primaryValue)) {
            sql = sql.concat(String.format(" where %s = '%s'", primaryKey, primaryValue));
        }
        return executeSQL(databaseId, sql);
    }

    /**
     * primaryKey first, and then is ROW_ID
     * @param databaseId
     * @param table
     * @return
     */
    public String getPrimaryKey(int databaseId, String table) {
        // 1. get the struct of table
        DatabaseResult info = getTableInfo(databaseId, table);
        int columnSize = info.columnNames.size();
        int pkIndex = -1;
        int nameIndex = -1;
        // 2. find the position of name and pk
        for (int i = 0; i < columnSize; i++) {
            if (TextUtils.equals(info.columnNames.get(i), Column.PK)) {
                pkIndex = i;
            } else if (TextUtils.equals(info.columnNames.get(i), Column.NAME)) {
                nameIndex = i;
            }
            if (pkIndex >= 0 && nameIndex >= 0) {
                break;
            }
        }
        // 3. determine the primary key based on the value of pk
        String primaryKeyName = null;
        for (int i = 0; i < info.values.size(); i++) {
            String pkValue = info.values.get(i).get(pkIndex);
            if (!TextUtils.isEmpty(pkValue) && "1".equals(pkValue)) {
                primaryKeyName = info.values.get(i).get(nameIndex);
                break;
            }
        }
        // 4. if no primary key defined, use ROW_ID as default
        if (TextUtils.isEmpty(primaryKeyName)) {
            primaryKeyName = Column.ROW_ID;
        }
        return primaryKeyName;
    }

    public DatabaseResult executeSQL(int databaseId, String sql) {
        Log.d(TAG, "executeSQL: " + sql);
        DatabaseResult result = new DatabaseResult();
        IDriver driver = holders.get(databaseId).driver;
        IDescriptor descriptor = holders.get(databaseId).descriptor;
        try {
            driver.executeSQL(descriptor, sql, result);
        } catch (SQLiteException e) {
            DatabaseResult.Error error = new DatabaseResult.Error();
            error.code = 0;
            error.message = e.getMessage();
            result.sqlError = error;
        }
        return result;
    }

    private static class DatabaseHolder {
        IDescriptor descriptor;
        IDriver<? extends IDescriptor> driver;

        DatabaseHolder(IDescriptor descriptor, IDriver<? extends IDescriptor> driver) {
            this.descriptor = descriptor;
            this.driver = driver;
        }
    }

    private static class DriverHolder {
        IDriver<? extends IDescriptor> driver;
        int databaseCount;

        DriverHolder(IDriver<? extends IDescriptor> driver, int databaseCount) {
            this.driver = driver;
            this.databaseCount = databaseCount;
        }
    }
}
