package tech.linjiang.pandora.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.List;

/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseResult {

    public List<String> columnNames;

    public List<List<String>> values;

    public Error sqlError;

    public static class Error {
        public String message;
        public int code;
    }

    public void transformRawQuery() throws SQLiteException {
    }

    public void transformSelect(Cursor result) throws SQLiteException {
    }

    public void transformInsert(long insertedId) throws SQLiteException {
    }

    public void transformUpdateDelete(int count) throws SQLiteException {
    }


    private static List<List<String>> wrapRows(Cursor cursor) {
        return null;
    }

    private static String blobToString(byte[] blob) {
        return null;
    }

    private static boolean fastIsAscii(byte[] blob) {
        return false;
    }
}
