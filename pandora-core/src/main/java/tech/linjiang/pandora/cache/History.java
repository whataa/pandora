package tech.linjiang.pandora.cache;

import android.provider.BaseColumns;

/**
 * Created by linjiang on 2019/3/3.
 */

@CacheDatabase.Table("activity_history")
public class History {

    @CacheDatabase.Column(value = BaseColumns._ID, primaryKey = true)
    public int id;
    @CacheDatabase.Column("createTime")
    public long createTime;
    @CacheDatabase.Column("activity")
    public String activity;
    @CacheDatabase.Column("event")
    public String event;
}
