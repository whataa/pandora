package tech.linjiang.pandora.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import tech.linjiang.pandora.cache.Content;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.FileUtil;
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
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                loadData();
            }
        });
        return webView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Content");
        webView.loadUrl("file:///android_asset/tmp_json.html");

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
        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_copy_value);
        getToolbar().getMenu().add(-1, 0, 1, R.string.pd_name_share);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(originContent);
                } else if (item.getOrder() == 1) {
                    saveAsFileAndShare(originContent);
                }
                return true;
            }
        });
    }

    private void saveAsFileAndShare(String msg) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<String, Intent>() {
            @Override
            public Intent doInBackground(String[] params) {
                String path = FileUtil.saveFile(params[0].getBytes(), "json", "txt");
                String newPath = FileUtil.fileCopy2Tmp(new File(path));
                if (!TextUtils.isEmpty(newPath)) {
                    return FileUtil.getFileIntent(newPath);
                }
                return null;
            }

            @Override
            public void onPostExecute(Intent result) {
                hideLoading();
                if (result != null) {
                    try {
                        startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Utils.toast(t.getMessage());
                    }
                } else {
                    Utils.toast(R.string.pd_failed);
                }

            }
        }).execute(msg);
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
            @Override
            public String doInBackground(Void[] params) {
                Content content = Content.query(id);
                String result;
                if (showResponse) {
                    result = content.responseBody;
                } else {
                    result = content.requestBody;
                }

                return result;
            }

            @Override
            public void onPostExecute(String result) {
                hideLoading();
                if (TextUtils.isEmpty(result)) {
                    Utils.toast(R.string.pd_error_msg);
                    return;
                }
                setSearchView();
                originContent = result;
                webView.setWebViewClient(null);

                if (contentType != null && contentType.toLowerCase().contains("json")) {
                    // help me
                    result = result.replaceAll("\n", "");
                    result = result.replace("\\", "\\\\");
                    result = result.replace("'", "\\\'");
                    // https://issuetracker.google.com/issues/36995865
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        webView.loadUrl(String.format("javascript:showJson('%s')", result));
                    } else {
                        webView.evaluateJavascript(String.format("showJson('%s')", result), null);
                    }
                } else {
                    webView.loadDataWithBaseURL(null, result,  decideMimeType(), "utf-8", null);
                }
            }
        }).execute();
    }



    private String decideMimeType() {
        if (contentType != null && contentType.toLowerCase().contains("xml")) {
            return "text/xml";
        } else {
            return "text/html";
        }
    }

}
