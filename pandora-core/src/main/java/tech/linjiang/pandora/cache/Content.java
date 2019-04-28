package tech.linjiang.pandora.cache;

import android.provider.BaseColumns;

import java.util.List;

import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2018/6/22.
 */
@CacheDatabase.Table("http_content")
public class Content {

    static {
        clear();
    }

    @CacheDatabase.Column(value = BaseColumns._ID, primaryKey = true)
    public long id;
    @CacheDatabase.Column("requestBody")
    public String requestBody;
    @CacheDatabase.Column("responseBody")
    public String responseBody;

    public static Content query(long id) {
        List<Content> result = CacheDatabase.queryList(Content.class, BaseColumns._ID + " = " + String.valueOf(id), "limit 1");
        if (Utils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    public static long insert(Content content) {
        return CacheDatabase.insert(content);
    }

    public static void update(Content content) {
        CacheDatabase.update(content);
    }
    public static void clear() {
        CacheDatabase.delete(Content.class);
    }
}
