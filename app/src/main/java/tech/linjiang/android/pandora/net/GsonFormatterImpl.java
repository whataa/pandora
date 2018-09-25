package tech.linjiang.android.pandora.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import tech.linjiang.pandora.network.JsonFormatter;

/**
 * Created by linjiang on 2018/9/25.
 */

public class GsonFormatterImpl implements JsonFormatter {
    @Override
    public String format(String result) {
        JsonElement je = new JsonParser().parse(result);
        return new Gson().toJson(je);
    }
}
