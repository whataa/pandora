package tech.linjiang.android.pandora.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.linjiang.android.pandora.db.StoreDatabase;
import tech.linjiang.android.pandora.db.entity.Drink;
import tech.linjiang.android.pandora.net.ApiService;
import tech.linjiang.android.pandora.utils.AssetUtil;
import tech.linjiang.android.pandora.utils.ThreadPool;
import tech.linjiang.pandora.util.FileUtil;

public class MainViewModel extends AndroidViewModel {

    private static final String CHAR = "0123456789qwertyuiopasdfghjklzxcvbnmABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public final MutableLiveData<String> dbResult = new MutableLiveData<>();
    public final MutableLiveData<String> assetResult = new MutableLiveData<>();
    public final MutableLiveData<String> xmlResult = new MutableLiveData<>();
    public final MutableLiveData<String> fileResult = new MutableLiveData<>();

    public void doFileDownload() {
        ApiService.getInstance().download().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void doUrlConnection() {
        ThreadPool.post(() -> {
            try {
                URL url = new URL("https://www.v2ex.com/api/topics/latest.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void doOKHttp() {
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
        api.redirectTo("https://www.v2ex.com/api/topics/hot.json").enqueue(cb);
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

    public void copyAsset2File() {
        ThreadPool.post(() -> {
            String path = AssetUtil.copyAssertToFiles(getApplication());
            assetResult.postValue(path);
        });
    }

    private String getRandomStr(int length) {
        char[] rands = new char[length];
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * CHAR.length());
            rands[i] = CHAR.charAt(rand);
        }
        return String.valueOf(rands);
    }

    public void resetDatabase() {
        ThreadPool.post(() -> {
            StoreDatabase.get().drinkDao().delete();
            Drink[] drinks = new Drink[100];
            for (int i = 0; i < drinks.length; i++) {
                Drink drink = new Drink();
                drink.color = String.format("#%06X", i * 1280);
                drink.flavor = i % 3;
                drink.ingredient = new Drink.Ingredient();
                drink.ingredient.carbon = i / 2f;
                drink.ingredient.energy = i / 5f;
                drink.ingredient.water = i / 7f;
                drink.name = getRandomStr(6);
                drink.type = i % 4;
                drinks[i] = drink;
            }
            try {
                StoreDatabase.get().drinkDao().insert(drinks);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            dbResult.postValue("Store.db");
        });
    }

    public void makeNewXml() {
        String name = getRandomStr(4);
        getApplication().getSharedPreferences(name, Context.MODE_PRIVATE)
                .edit()
                .putString("createTime", "" + new Date().toString())
                .apply();
        xmlResult.setValue(name+".xml");
    }

    public void makeNewFile() {
        ThreadPool.post(() -> {
            String name = getRandomStr(4);
            String content = getRandomStr(128);
            String path = FileUtil.saveFile(content.getBytes(), name, "txt");
            fileResult.postValue(path);
        });
    }
}
