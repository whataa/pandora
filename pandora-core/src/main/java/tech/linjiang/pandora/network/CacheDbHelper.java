package tech.linjiang.pandora.network;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.network.model.Content;
import tech.linjiang.pandora.network.model.Summary;
import tech.linjiang.pandora.util.JsonUtil;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2018/6/21.
 */

public class CacheDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pd_cache.db";

    private CacheDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        reset();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SummaryEntry.SQL_CREATE_ENTRIES);
        db.execSQL(ContentEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SummaryEntry.SQL_DELETE_ENTRIES);
        db.execSQL(ContentEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void reset() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL(SummaryEntry.SQL_CLEAR_ENTRIES);
            db.execSQL(ContentEntry.SQL_CLEAR_ENTRIES);
        }
    }

    private static CacheDbHelper HELPER = new CacheDbHelper(Utils.getContext());

    static SQLiteDatabase getWDb() {
        return HELPER.getWritableDatabase();
    }

    static SQLiteDatabase getRDb() {
        return HELPER.getReadableDatabase();
    }

    public static void deleteAll() {
        SummaryEntry.delete();
        ContentEntry.delete();
    }

    public static List<Summary> getSummaries() {
        return SummaryEntry.query();
    }

    public static Summary getSummary(long id) {
        return SummaryEntry.query(id);
    }

    public static Content getContent(long id) {
        return ContentEntry.query(id);
    }


    public static class SummaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "summary";

        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_HOST = "host";
        public static final String COLUMN_NAME_METHOD = "method";
        public static final String COLUMN_NAME_PROTOCOL = "protocol";
        public static final String COLUMN_NAME_SSL = "ssl";
        public static final String COLUMN_NAME_TIME_START = "start_time";
        public static final String COLUMN_NAME_TIME_END = "end_time";
        public static final String COLUMN_NAME_CONTENT_TYPE_RESPONSE = "response_content_type";
        public static final String COLUMN_NAME_SIZE_REQUEST = "request_size";
        public static final String COLUMN_NAME_SIZE_RESPONSE = "response_size";
        public static final String COLUMN_NAME_HEADER_REQUEST = "request_header";
        public static final String COLUMN_NAME_HEADER_RESPONSE = "response_header";


        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SummaryEntry.TABLE_NAME + " (" +
                        SummaryEntry._ID + " INTEGER PRIMARY KEY," +
                        SummaryEntry.COLUMN_NAME_STATUS + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_CODE + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_URL + " TEXT," +
                        SummaryEntry.COLUMN_NAME_HOST + " TEXT," +
                        SummaryEntry.COLUMN_NAME_METHOD + " TEXT," +
                        SummaryEntry.COLUMN_NAME_PROTOCOL + " TEXT," +
                        SummaryEntry.COLUMN_NAME_SSL + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_TIME_START + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_TIME_END + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_CONTENT_TYPE_RESPONSE + " TEXT," +
                        SummaryEntry.COLUMN_NAME_SIZE_REQUEST + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_SIZE_RESPONSE + " INTEGER," +
                        SummaryEntry.COLUMN_NAME_HEADER_REQUEST + " TEXT," +
                        SummaryEntry.COLUMN_NAME_HEADER_RESPONSE + " TEXT" +
                        ")";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SummaryEntry.TABLE_NAME;

        static final String SQL_CLEAR_ENTRIES =
                "DELETE FROM " + SummaryEntry.TABLE_NAME;

        private static final int MAX_QUERY_COUNT = 512;

        public static void update(long pkValue, ContentValues values) {
            getWDb().update(TABLE_NAME, values, _ID + " = ?", new String[]{String.valueOf(pkValue)});
        }

        public static long insert(ContentValues values) {
            return getWDb().insert(TABLE_NAME, null, values);
        }

        public static void delete() {
            getWDb().execSQL(SQL_CLEAR_ENTRIES);
        }

        public static Summary query(long id) {
            Cursor cursor = getRDb().query(
                    TABLE_NAME,
                    null,
                    _ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null,
                    String.valueOf(1)
            );
            try {
                if (cursor.moveToNext()) {
                    return parse(cursor);
                } else {
                    return null;
                }
            } finally {
                cursor.close();
            }
        }

        public static List<Summary> query() {
            List<Summary> summaries = new ArrayList<>();
            Cursor cursor = getRDb().query(
                    TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_NAME_TIME_START + " DESC",
                    String.valueOf(MAX_QUERY_COUNT)
            );
            while(cursor.moveToNext()) {
                Summary summary = parse(cursor);
                summaries.add(summary);
            }
            cursor.close();
            return summaries;
        }

        private static Summary parse(Cursor cursor) {
            Summary summary = new Summary();
            summary.id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            summary.status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_STATUS));
            summary.code = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_CODE));
            summary.url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_URL));
            summary.host = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_HOST));
            summary.method = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_METHOD));
            summary.protocol = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_PROTOCOL));
            summary.ssl = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SSL)) == 1;
            summary.start_time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIME_START));
            summary.end_time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIME_END));
            summary.response_content_type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CONTENT_TYPE_RESPONSE));
            summary.request_size = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_SIZE_REQUEST));
            summary.response_size = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_SIZE_RESPONSE));
            String reqHeaders = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_HEADER_REQUEST));
            summary.request_header = JsonUtil.parseHeaders(reqHeaders);
            String resHeaders = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_HEADER_RESPONSE));
            summary.response_header = JsonUtil.parseHeaders(resHeaders);
            return summary;
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

    public static class ContentEntry {
        public static final String TABLE_NAME = "content";

        public static final String COLUMN_NAME_SUMMARY_ID = "summary_id";
        public static final String COLUMN_NAME_REQUEST = "request";
        public static final String COLUMN_NAME_RESPONSE = "response";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ContentEntry.TABLE_NAME + " (" +
                        ContentEntry.COLUMN_NAME_SUMMARY_ID + " INTEGER PRIMARY KEY," +
                        ContentEntry.COLUMN_NAME_REQUEST + " TEXT," +
                        ContentEntry.COLUMN_NAME_RESPONSE + " TEXT" +
                        ")";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ContentEntry.TABLE_NAME;

        static final String SQL_CLEAR_ENTRIES =
                "DELETE FROM " + ContentEntry.TABLE_NAME;


        public static void update(long pkValue, ContentValues values) {
            getWDb().update(TABLE_NAME, values, COLUMN_NAME_SUMMARY_ID + " = ?", new String[]{String.valueOf(pkValue)});
        }

        public static long insert(ContentValues values) {
            return getWDb().insert(TABLE_NAME, null, values);
        }

        public static void delete() {
            getWDb().execSQL(SQL_CLEAR_ENTRIES);
        }

        public static Content query(long pkValue) {
            Content content = new Content();
            Cursor cursor = getRDb().query(
                    TABLE_NAME,
                    null,
                    COLUMN_NAME_SUMMARY_ID + " = ?",
                    new String[]{String.valueOf(pkValue)},
                    null,
                    null,
                    null,
                    String.valueOf(1)
            );
            while(cursor.moveToNext()) {
                content.requestBody = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_REQUEST));
                content.responseBody = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_RESPONSE));
            }
            cursor.close();
            return content;
        }
    }
}
