package tech.linjiang.pandora.cache;

import android.provider.BaseColumns;

import java.util.List;

import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/3.
 */

@CacheDatabase.Table("log_crash")
public class Crash {

    @CacheDatabase.Column(value = BaseColumns._ID, primaryKey = true)
    public int id;

    @CacheDatabase.Column("createTime")
    public long createTime;
    @CacheDatabase.Column("cause")
    public String cause;
    @CacheDatabase.Column("stack")
    public String stack;

    public static List<Crash> query() {
        List<Crash> result = CacheDatabase.queryList(Crash.class, null, "order by createTime DESC");
        return result;
    }

    public static void insert(Throwable t) {
        Crash crash = new Crash();
        crash.cause = t.getMessage();
        crash.stack = Utils.collectThrow(t);
        crash.createTime = System.nanoTime();
        CacheDatabase.insert(crash);
    }

}
