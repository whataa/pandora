package tech.linjiang.android.pandora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.linjiang.android.pandora.db.KeyDatabase;
import tech.linjiang.android.pandora.db.StoreDatabase;
import tech.linjiang.android.pandora.db.entity.Drink;
import tech.linjiang.android.pandora.db.entity.KeyValue;
import tech.linjiang.android.pandora.net.ApiService;
import tech.linjiang.pandora.Pandora;

public class MainActivity extends AppCompatActivity {

    public static final String KEY = "ifCanFillData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pandora.get().open();
            }
        });
        findViewById(R.id.another_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransActivity.class));
            }
        });
        findViewById(R.id.toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.http).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHttp();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                KeyValue keyValue = KeyDatabase.get().keyDao().get(KEY);
                if (keyValue == null || keyValue.value) {
                    fillTestDB();
                    fillTestSP();
                    fillTestFile();
                    KeyDatabase.get().keyDao().insert(new KeyValue(KEY, false));
                }

            }
        }).start();


    }

    private void fillTestDB() {
        for (int i = 0; i < 300; i++) {
            Drink drink = new Drink();
            drink.color = Color.RED;
            drink.flavor = i;
            drink.type = i % 2;
            drink.ingredient = null;
            if (i % 5 == 0) {
                drink.ingredient = new Drink.Ingredient();
                drink.ingredient.carbon = i;
                drink.ingredient.energy = i;
                drink.ingredient.water = i;
            }
            StoreDatabase.get().drinkDao().insert(drink);
        }
    }

    private void fillTestSP() {
        SharedPreferences preferences = getSharedPreferences("testAllType", MODE_PRIVATE);
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            set.add("setValue:" + i);
        }
        preferences.edit()
                .putBoolean("putBoolean", true)
                .putFloat("putFloat", 1.234f)
                .putLong("putLong", 2L)
                .putInt("putInt", 1980)
                .putString("putString", "putString")
                .putStringSet("putString", set)
                .apply();
        SharedPreferences preferencesDef = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesDef.edit()
                .putBoolean("putBoolean", true)
                .putFloat("putFloat", 1.234f)
                .putLong("putLong", 2L)
                .putInt("putInt", 1980)
                .putString("putString", "putString")
                .apply();
    }

    private void fillTestFile() {
        AssetUtil.copyAssertToFiles(this);

    }

    private void doHttp() {
        ApiService.HttpbinApi api = ApiService.getInstance();
        Callback<Void> cb = new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        };
        api.get().enqueue(cb);
        api.post(new ApiService.Data("posted")).enqueue(cb);
        api.patch(new ApiService.Data("patched")).enqueue(cb);
        api.put(new ApiService.Data("put")).enqueue(cb);
        api.delete().enqueue(cb);
        api.status(201).enqueue(cb);
        api.status(401).enqueue(cb);
        api.status(500).enqueue(cb);
        api.delay(9).enqueue(cb);
        api.delay(15).enqueue(cb);
        api.redirectTo("https://http2.akamai.com").enqueue(cb);
        api.redirect(3).enqueue(cb);
        api.redirectRelative(2).enqueue(cb);
        api.redirectAbsolute(4).enqueue(cb);
        api.stream(500).enqueue(cb);
        api.streamBytes(2048).enqueue(cb);
        api.image("image/png").enqueue(cb);
        api.gzip().enqueue(cb);
        api.xml().enqueue(cb);
        api.utf8().enqueue(cb);
        api.deflate().enqueue(cb);
        api.cookieSet("v").enqueue(cb);
        api.basicAuth("me", "pass").enqueue(cb);
        api.drip(512, 5, 1, 200).enqueue(cb);
        api.deny().enqueue(cb);
        api.cache("Mon").enqueue(cb);
        api.cache(30).enqueue(cb);
    }
}
