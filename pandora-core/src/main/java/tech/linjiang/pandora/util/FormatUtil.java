package tech.linjiang.pandora.util;

import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * Created by linjiang on 2018/6/21.
 */

public class FormatUtil {


    public static String formatHeaders(Headers headers) {
        JSONArray array = new JSONArray();
        for (int i = 0, size = headers.size(); i < size; i++) {
            array.put(new JSONArray().put(headers.name(i)).put(headers.value(i)));
        }
        if (array.length() > 0) {
            return array.toString();
        } else {
            return null;
        }
    }

    public static List<Pair<String, String>> parseHeaders(String headers) {
        List<Pair<String, String>> headerList = new ArrayList<>();
        if (!TextUtils.isEmpty(headers)) {
            try {
                JSONArray array = new JSONArray(headers);
                for (int i = 0; i < array.length(); i++) {
                    Pair<String, String> header = new Pair<>(
                            array.getJSONArray(i).getString(0), array.getJSONArray(i).getString(1));
                    headerList.add(header);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return headerList;
    }

}
