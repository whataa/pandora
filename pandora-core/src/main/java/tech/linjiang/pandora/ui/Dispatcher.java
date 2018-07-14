package tech.linjiang.pandora.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.inspector.BaseLineView;
import tech.linjiang.pandora.inspector.OperableView;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.ui.connector.UIStateCallback;
import tech.linjiang.pandora.ui.fragment.NetFragment;
import tech.linjiang.pandora.ui.fragment.SandboxFragment;
import tech.linjiang.pandora.ui.fragment.ViewFragment;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 30/05/2018.
 */

public class Dispatcher extends AppCompatActivity implements UIStateCallback {

    public static final String PARAM1 = "param1";

    public static void start(Context context, @Type int type) {
        Intent intent = new Intent(context, Dispatcher.class)
                .putExtra(PARAM1, type);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private @Type
    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra(PARAM1, Type.FILE);
        ViewKnife.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        ViewKnife.transStatusBar(getWindow());
        dispatch(savedInstanceState);
    }

    private void dispatch(Bundle savedInstanceState) {
        View view = null;
        switch (type) {
            case Type.BASELINE:
                view = new BaseLineView(this);
                setContentView(view);
                break;
            case Type.ATTR:
            case Type.HIERARCHY:
                view = new FrameLayout(this);
                view.setId(R.id.pd_fragment_container_id);
                setContentView(view);
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.pd_fragment_container_id, ViewFragment.newInstance(type))
                            .commit();
                }
                break;
            case Type.NET:
                view = new FrameLayout(this);
                view.setId(R.id.pd_fragment_container_id);
                setContentView(view);
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.pd_fragment_container_id, new NetFragment())
                            .commit();
                }
                break;
            case Type.FILE:
                view = new FrameLayout(this);
                view.setId(R.id.pd_fragment_container_id);
                setContentView(view);
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.pd_fragment_container_id, new SandboxFragment())
                            .commit();
                }
                break;
            case Type.SELECT:
                OperableView operableView = new OperableView(this);
                operableView.tryGetFrontView(Pandora.get().getBottomActivity());
                setContentView(operableView);
                break;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (type != Type.NET && type != Type.FILE) {
            finish();
            return;
        }
    }


    private View hintView;

    @Override
    public void showHint() {
        if (hintView == null) {
            hintView = new ProgressBar(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            hintView.setLayoutParams(params);
        }
        if (hintView.getParent() == null) {
            if (getWindow() != null) {
                if (getWindow().getDecorView() instanceof ViewGroup) {
                    ((ViewGroup) getWindow().getDecorView()).addView(hintView);
                }
            }
        }
        if (hintView.getVisibility() == View.GONE) {
            hintView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideHint() {
        if (hintView != null) {
            if (hintView.getVisibility() != View.GONE) {
                hintView.setVisibility(View.GONE);
            }
        }
    }
}
