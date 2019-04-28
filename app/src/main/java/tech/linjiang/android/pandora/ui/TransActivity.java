package tech.linjiang.android.pandora.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tech.linjiang.android.pandora.R;

/**
 * Created by linjiang on 2018/7/14.
 */

public class TransActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().alpha = 0.9f;
        setContentView(R.layout.activity_ui_test);
    }
}
