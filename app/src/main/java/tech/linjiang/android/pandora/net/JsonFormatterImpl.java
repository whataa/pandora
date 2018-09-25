package tech.linjiang.android.pandora.net;

import com.alibaba.fastjson.JSON;

import tech.linjiang.pandora.network.JsonFormatter;

/**
 * Created by linjiang on 2018/9/25.
 */

public class JsonFormatterImpl implements JsonFormatter {
    @Override
    public String format(String result) {
        return JSON.toJSONString(JSON.parse(result));
    }
}
