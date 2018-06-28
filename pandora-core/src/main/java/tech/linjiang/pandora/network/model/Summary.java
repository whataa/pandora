package tech.linjiang.pandora.network.model;

import android.util.Pair;

import java.util.List;

/**
 * Created by linjiang on 2018/6/22.
 */

public class Summary {

    public long id;
    public int status;
    public int code;
    public String url;
    public String query;
    public String host;
    public String method;
    public String protocol;
    public boolean ssl;
    public long start_time;
    public long end_time;
    public String response_content_type;
    public long request_size;
    public long response_size;
    public List<Pair<String, String>> request_header;
    public List<Pair<String, String>> response_header;


}
