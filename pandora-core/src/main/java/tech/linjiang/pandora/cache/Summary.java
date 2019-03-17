package tech.linjiang.pandora.cache;

import android.provider.BaseColumns;
import android.support.annotation.IntDef;
import android.util.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import tech.linjiang.pandora.util.Config;

/**
 * Created by linjiang on 2018/6/22.
 */

@CacheDatabase.Table("http_summary")
public class Summary {

    static {
        clear();
    }

    @CacheDatabase.Column(value = BaseColumns._ID, primaryKey = true)
    public long id;
    @CacheDatabase.Column("status")
    public int status;
    @CacheDatabase.Column("code")
    public int code;
    @CacheDatabase.Column("url")
    public String url;
    @CacheDatabase.Column("query")
    public String query;
    @CacheDatabase.Column("host")
    public String host;
    @CacheDatabase.Column("method")
    public String method;
    @CacheDatabase.Column("protocol")
    public String protocol;
    @CacheDatabase.Column("ssl")
    public boolean ssl;
    @CacheDatabase.Column("start_time")
    public long start_time;
    @CacheDatabase.Column("end_time")
    public long end_time;
    @CacheDatabase.Column("request_content_type")
    public String request_content_type;
    @CacheDatabase.Column("response_content_type")
    public String response_content_type;
    @CacheDatabase.Column("request_size")
    public long request_size;
    @CacheDatabase.Column("response_size")
    public long response_size;
    @CacheDatabase.Column("request_header")
    public String requestHeader;
    @CacheDatabase.Column("response_header")
    public String responseHeader;

    public List<Pair<String, String>> request_header;
    public List<Pair<String, String>> response_header;


    public static List<Summary> queryList() {
        String condition = "order by start_time desc limit " + String.valueOf(Config.getNETWORK_PAGE_SIZE());
        List<Summary> result = CacheDatabase.queryList(Summary.class, null, condition);
        return result;
    }

    public static Summary query(long id) {
        return CacheDatabase.queryList(Summary.class, BaseColumns._ID + " = " + String.valueOf(id), "limit 1").get(0);
    }

    public static long insert(Summary summary) {
        return CacheDatabase.insert(summary);
    }

    public static void update(Summary summary) {
        CacheDatabase.update(summary);
    }

    public static void clear() {
        CacheDatabase.delete(Summary.class);
    }

    @IntDef({
            Status.REQUESTING,
            Status.ERROR,
            Status.COMPLETE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int REQUESTING = 0x00;
        int ERROR = 0x01;
        int COMPLETE = 0x02;
    }
}
