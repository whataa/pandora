package tech.linjiang.pandora.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.PopupWindow;

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
        webView.setWebViewClient(new WebViewClient() {
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
        getToolbar().getMenu().add(-1, 0, 0, R.string.pd_name_search);
        getToolbar().getMenu().add(-1, 0, 1, R.string.pd_name_copy_value);
        getToolbar().getMenu().add(-1, 0, 2, R.string.pd_name_share);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    showSearchPopup();
                } else if (item.getOrder() == 1) {
                    Utils.copy2ClipBoard(originContent);
                } else if (item.getOrder() == 3) {
                    saveAsFileAndShare(originContent);
                }
                return true;
            }
        });
    }

    private void showSearchPopup() {
        final Context context = getContext();
        if (context == null) return;
        final View view = LayoutInflater.from(context).inflate(R.layout.pd_layout_search_view, null, true);
        final PopupWindow searchWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editText = view.findViewById(R.id.et_search);
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                //搜索
                webView.findAllAsync(s.toString().trim());
                webView.setFindListener(new WebView.FindListener() {
                    @Override
                    public void onFindResultReceived(int position, int all, boolean b) {
                        //tv.setText("(位置："+(position+1)+"/"+all+")");
                    }
                });
            }
        };
        editText.addTextChangedListener(textWatcher);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(false);
            }
        });
        view.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(true);
            }
        });
        searchWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchWindow.setContentView(view);
        searchWindow.setOutsideTouchable(true);
        searchWindow.setFocusable(true);
        searchWindow.showAtLocation(webView, Gravity.TOP, 100, 0);
        editText.requestFocus();
        searchWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        searchWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                editText.setText("");
                editText.removeTextChangedListener(textWatcher);
            }
        });
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(200, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
                    webView.loadDataWithBaseURL(null, result, decideMimeType(), "utf-8", null);
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
