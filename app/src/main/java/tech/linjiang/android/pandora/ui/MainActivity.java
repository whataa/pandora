package tech.linjiang.android.pandora.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import tech.linjiang.android.pandora.R;
import tech.linjiang.android.pandora.viewmodel.MainViewModel;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.function.IFunc;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_shake).setOnClickListener(v -> {
            toast(getString(R.string.pandora_shake_please));
        });
        findViewById(R.id.btn_open).setOnClickListener(v -> {
            Pandora.get().open();
        });
        findViewById(R.id.btn_net_okhttp).setOnClickListener(v -> {
            viewModel.doOKHttp();
            toast(getString(R.string.pandora_open_net));

        });
        findViewById(R.id.btn_net_file).setOnClickListener(v -> {
            viewModel.doFileDownload();
            toast(getString(R.string.pandora_open_net));
        });

        findViewById(R.id.btn_ui_activity).setOnClickListener(v -> {
            startActivity(new Intent(this, TransActivity.class));
            toast(getString(R.string.pandora_open_select));
        });
        findViewById(R.id.btn_ui_window).setOnClickListener(v -> {
            View content = LayoutInflater.from(this).inflate(R.layout.activity_ui_test, null);
            PopupWindow window = new PopupWindow(content,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setContentView(content);
            window.setOutsideTouchable(true);
            window.setFocusable(true);
            window.showAsDropDown(v);
            toast(getString(R.string.pandora_open_select));
        });
        findViewById(R.id.btn_ui_dialog).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setView(R.layout.activity_ui_test)
                    .show();
            toast(getString(R.string.pandora_open_select));
        });
        findViewById(R.id.btn_ui_view).setOnClickListener(v -> {
            startActivity(new Intent(this, StackViewActivity.class));
        });

        findViewById(R.id.btn_feature_crash).setOnClickListener(v -> {
            new Thread() {
                @Override
                public void run() {
                    throw new RuntimeException("Test rash");
                }
            }.start();
        });
        findViewById(R.id.btn_feature_func).setOnClickListener(v -> {
            Pandora.get().addFunction(customFunc);
            toast(getString(R.string.pandora_add_success));
        });

        findViewById(R.id.btn_file_assets).setOnClickListener(v -> {
            viewModel.copyAsset2File();
        });
        findViewById(R.id.btn_file_xml).setOnClickListener(v -> {
            viewModel.makeNewXml();
        });
        findViewById(R.id.btn_file_db).setOnClickListener(v -> {
            viewModel.resetDatabase();
        });
        findViewById(R.id.btn_file_file).setOnClickListener(v -> {
            viewModel.makeNewFile();
        });

        initViewModel();
    }

    private void initViewModel() {
        viewModel.dbResult.observe(this, name -> {
            toast(getString(R.string.pandora_reseted) + name);
        });
        viewModel.assetResult.observe(this, path -> {
            toast(getString(R.string.pandora_copyed) + path);
        });
        viewModel.fileResult.observe(this, path -> {
            toast(getString(R.string.pandora_created_file) + path);
        });
        viewModel.xmlResult.observe(this, path -> {
            toast(getString(R.string.pandora_created_xml) + path);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Github");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = "https://github.com/whataa/pandora";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri githubUrl = Uri.parse(url);
            intent.setData(githubUrl);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
        return true;
    }


    void toast(String msg) {
        Snackbar.make(findViewById(R.id.container), msg, Snackbar.LENGTH_LONG).show();
    }

    private IFunc customFunc = new IFunc() {
        @Override
        public int getIcon() {
            return R.drawable.ic_launcher_round;
        }

        @Override
        public String getName() {
            return getString(R.string.pandora_click_me);
        }

        @Override
        public boolean onClick() {
            toast("I am the custom Function.");
            return false;
        }
    };
}
