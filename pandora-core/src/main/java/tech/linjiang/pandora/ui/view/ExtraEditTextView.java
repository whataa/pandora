package tech.linjiang.pandora.ui.view;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by linjiang on 07/06/2018.
 */

public class ExtraEditTextView extends EditText {
    public ExtraEditTextView(Context context) {
        this(context, null);
    }

    public ExtraEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtraEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            Field field = TextView.class.getDeclaredField("mListeners");
            field.setAccessible(true);
            field.set(this, listeners);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private ArrayList<TextWatcher> listeners = new ArrayList<>();

    public final void clearTextChangedListeners() {
        listeners.clear();
    }
}
