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

public class JsonUtil {


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

    public static List<String> print(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        int tabNum = 0;
        List<String> result = new ArrayList<>();
        StringBuilder jsonFormat = new StringBuilder();
        int length = json.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = json.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c);
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
                jsonFormat.append(c);
            } else if (c == ',') {
                jsonFormat.append(c);
                result.add(jsonFormat.toString());
                jsonFormat = new StringBuilder();
                jsonFormat.append(addTab(tabNum));
            } else if (c == ':') {
                jsonFormat.append(c).append(" ");
            } else if (c == '[') {
                tabNum++;
                char next = json.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append(c);
                    result.add(jsonFormat.toString());
                    jsonFormat = new StringBuilder();
                    jsonFormat.append(addTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                } else {
                    result.add(jsonFormat.toString());
                    jsonFormat = new StringBuilder();
                    jsonFormat.append(addTab(tabNum)).append(c);
                }
            } else {
                jsonFormat.append(c);
            }
            last = c;
        }
        result.add(jsonFormat.toString());
        return result;
    }

    private static String addTab(int tabNum) {
        StringBuilder sbTab = new StringBuilder();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append("        "/* 8 space */);
        }
        return sbTab.toString();
    }
}
