package tech.linjiang.pandora.ui.fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.ui.GeneralDialog;

/**
 * Created by linjiang on 2019/3/5.
 */

public class PermissionReqFragment extends Fragment {

    private final int code = 0x10;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        GeneralDialog.build(code)
                .title(R.string.pd_permission_title)
                .message(R.string.pd_please_allow_permission)
                .positiveButton(R.string.pd_ok)
                .negativeButton(R.string.pd_cancel)
                .cancelable(false)
                .show(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getContext())) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            getActivity().finish();
        }
    }
}
