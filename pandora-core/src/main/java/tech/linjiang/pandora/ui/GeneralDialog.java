package tech.linjiang.pandora.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2019/1/7.
 */

public class GeneralDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String ATTR1 = "ATTR1";
    public static final String ATTR2 = "ATTR2";
    public static final String ATTR3 = "ATTR3";
    public static final String ATTR4 = "ATTR4";
    public static final String ATTR5 = "ATTR5";
    public static final String ATTR6 = "ATTR6";
    public static final String ATTR7 = "ATTR7";


    public static class Creator {
        Bundle bundle = new Bundle();

        Creator(int code) {
            bundle.putInt(ATTR1, code);
        }

        public Creator title(int res) {
            bundle.putString(ATTR2, ViewKnife.getString(res));
            return this;
        }

        public Creator message(int res) {
            message(res, false);
            return this;
        }

        public Creator message(int res, Object... param) {
            bundle.putString(ATTR3, (String.format(ViewKnife.getString(res), param)));
            return this;
        }

        public Creator message(int res, boolean center) {
            bundle.putString(ATTR3, ViewKnife.getString(res));
            bundle.putBoolean(ATTR7, center);
            return this;
        }

        public Creator negativeButton(int res) {
            bundle.putString(ATTR4, ViewKnife.getString(res));
            return this;
        }

        public Creator positiveButton(int res) {
            bundle.putString(ATTR5, ViewKnife.getString(res));
            return this;
        }

        public Creator cancelable(boolean value) {
            bundle.putBoolean(ATTR6, value);
            return this;
        }


        public void show(Fragment fragment) {
            GeneralDialog dialog = new GeneralDialog();
            dialog.setArguments(bundle);
            dialog.show(fragment.getChildFragmentManager(), "GeneralDialog#" + bundle.getInt(ATTR1));
        }
    }

    public static Creator build(int code) {
        return new Creator(code);
    }

    private AlertDialog.Builder builder;
    private int code;

    public GeneralDialog() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        builder = new AlertDialog.Builder(getContext(), R.style.PdTheme_Alert);

        String title = getArguments().getString(ATTR2);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        String message = getArguments().getString(ATTR3);
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        String negativeButton = getArguments().getString(ATTR4);
        if (!TextUtils.isEmpty(negativeButton)) {
            builder.setNegativeButton(negativeButton, this);
        }
        String positiveButton = getArguments().getString(ATTR5);
        if (!TextUtils.isEmpty(positiveButton)) {
            builder.setPositiveButton(positiveButton, this);
        }
        final boolean cancelable = getArguments().getBoolean(ATTR6, true);
        builder.setCancelable(cancelable);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return !cancelable;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                getParentFragment().onActivityResult(code, Activity.RESULT_OK, null);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                getParentFragment().onActivityResult(code, Activity.RESULT_CANCELED, null);
                break;
        }
    }



    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        code = getArguments().getInt(ATTR1);
        return builder.create();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public final void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
            transform(dialog.getWindow());
        } else {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    transform(getDialog().getWindow());
                }
            });
        }
    }

    private void transform(Window window) {
        try {
            View sysContent = window.findViewById(Window.ID_ANDROID_CONTENT);
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setCornerRadius(ViewKnife.dip2px(10));
            backgroundDrawable.setColor(Color.WHITE);
            ViewCompat.setBackground(sysContent, backgroundDrawable);

            DialogTitle title = window.findViewById(android.support.v7.appcompat.R.id.alertTitle);
            TextView message = window.findViewById(android.R.id.message);
            Button button1 = window.findViewById(android.R.id.button1);
            Button button2 = window.findViewById(android.R.id.button2);
            Button button3 = window.findViewById(android.R.id.button3);
            LinearLayout buttonParent = (LinearLayout) button1.getParent();

            buttonParent.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable verticalDrawable = new GradientDrawable();
            verticalDrawable.setColor(0xffE5E5E5);
            verticalDrawable.setSize(ViewKnife.dip2px(.5f), 0);
            buttonParent.setDividerDrawable(verticalDrawable);
            buttonParent.setPadding(0, 0, 0, 0);

            GradientDrawable innerDrawable = new GradientDrawable();
            innerDrawable.setStroke(ViewKnife.dip2px(.5f), 0xffE5E5E5);
            InsetDrawable insetDrawable = new InsetDrawable(innerDrawable,
                    ViewKnife.dip2px(-1), 0, ViewKnife.dip2px(-1), ViewKnife.dip2px(-1));
            ViewCompat.setBackground(buttonParent, insetDrawable);

            window.findViewById(android.support.v7.appcompat.R.id.spacer).setVisibility(View.GONE);

            View textSpacerNoButtons = window.findViewById(android.support.v7.appcompat.R.id.textSpacerNoButtons);
            if (textSpacerNoButtons != null) {
                textSpacerNoButtons.setVisibility(View.VISIBLE);
            }
            button1.setTextColor(0xff5B6B91);
            button2.setTextColor(0xff353535);
            button3.setTextColor(0xff353535);
            button1.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            button2.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            button3.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            ((LinearLayout.LayoutParams) button3.getLayoutParams()).weight = 1;
            ((LinearLayout.LayoutParams) button2.getLayoutParams()).weight = 1;
            ((LinearLayout.LayoutParams) button1.getLayoutParams()).weight = 1;

            if (message != null) {
                message.setTextColor(0xff202020);
                if (getArguments().getBoolean(ATTR7, false)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        message.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }
            }

            title.setTextColor(0xff353535);
            title.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
