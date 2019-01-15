package tech.linjiang.pandora.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import java.net.URLDecoder;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.network.CacheDbHelper;
import tech.linjiang.pandora.network.model.Content;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2018/6/24.
 */

public class NetContentFragment extends BaseFragment {

    private boolean showResponse;
    private long id;
    private String contentType;
    private String originContent;
    private WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showResponse = getArguments().getBoolean(PARAM1, true);
        id = getArguments().getLong(PARAM2);
        contentType = getArguments().getString(PARAM3);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected View getLayoutView() {
        webView = new WebView(getContext());
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        return webView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Content");
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeSoftInput();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    private void setSearchView() {
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_1, 0, "copy");
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_3, 2, "share");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.pd_menu_id_1) {
                    Utils.copy2ClipBoard(originContent);
                } else if (item.getItemId() == R.id.pd_menu_id_3) {
                    Utils.shareText(originContent);
                }
                return true;
            }
        });
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
            @Override
            public String doInBackground(Void[] params) {
                Content content = CacheDbHelper.getContent(id);
                String result;
                if (showResponse) {
                    result = content.responseBody;
                } else {
                    result = content.requestBody;
                }
                try {
                    return URLDecoder.decode(result, "utf-8");
                } catch (Exception e) {
                    return result;
                }
            }

            @Override
            public void onPostExecute(String result) {
                hideLoading();
                setSearchView();
                originContent = result;
                if (contentType.toLowerCase().contains("json")) {
                    result = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "    <meta charset=utf-8>\n" +
                            "    <meta name=viewport content=\"width=device-width\">\n" +
                            "    <script type=\"text/javascript\">\n" +
                            "    function output(content) {\n" +
                            "        document.body.appendChild(document.createElement('pre')).innerHTML = content;\n" +
                            "    }\n" +
                            "    function start() {\n" +
                            "        var obj = \n" + result + ";\n" +
                            "        var str = JSON.stringify(obj, undefined, 4);\n" +
                            "        output(str);\n" +
                            "    }\n" +
                            "    </script>\n" +
                            "</head>\n" +
                            "<body onLoad=\"javascript:start();\">\n" +
                            "</body>\n" +
                            "</html>";
                }
                webView.loadData(result,  decideMimeType(), "utf-8");
            }
        }).execute();
    }



    private String decideMimeType() {
        if (contentType.toLowerCase().contains("xml")) {
            return "text/xml";
        } else {
            return "text/html";
        }
    }

}
