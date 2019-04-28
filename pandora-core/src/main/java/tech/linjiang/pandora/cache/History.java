package tech.linjiang.pandora.cache;

import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by linjiang on 2019/3/3.
 */

@CacheDatabase.Table("activity_history")
public class History {

    static {
        clear();
    }

    @CacheDatabase.Column(value = BaseColumns._ID, primaryKey = true)
    public int id;
    @CacheDatabase.Column("createTime")
    public long createTime;
    @CacheDatabase.Column("activity")
    public String activity;
    @CacheDatabase.Column("event")
    public String event;

    public static void clear() {
        CacheDatabase.delete(History.class);
    }

    public static void insert(History history) {
        CacheDatabase.insert(history);
    }

    public static List<History> query() {
        String condition = "order by createTime desc";
        return CacheDatabase.queryList(History.class, null, condition);
    }
}
