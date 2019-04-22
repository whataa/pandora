package tech.linjiang.android.pandora.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.android.pandora.utils.ThreadPool;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.ViewKnife;

public class PicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewKnife.transStatusBar(getWindow());
        ViewKnife.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        RecyclerView recyclerView = new RecyclerView(this);
        UniversalAdapter adapter;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter = new UniversalAdapter());

        List<PictureItem> items = new ArrayList<>();
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=973231800,2770258116&fm=27&gp=0.jpg"));
        items.add(new PictureItem("http://img5.imgtn.bdimg.com/it/u=328517395,2303970886&fm=26&gp=0.jpg"));
        items.add(new PictureItem("http://img5.imgtn.bdimg.com/it/u=1810050752,207505815&fm=200&gp=0.jpg"));
        items.add(new PictureItem("http://img3.imgtn.bdimg.com/it/u=2593029726,847983021&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2891178660,473481395&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=923155321,1503094790&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=138662523,3773165341&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=814663857,560789006&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1741054598,1265588416&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=691950827,2308454275&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3823674128,1284221604&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=744475572,1363633948&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1814913138,2431912228&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2805867659,1277717018&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1436755091,2612803750&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2003961533,572853730&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1863669792,1614717395&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1317349462,3646749915&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2034840885,4119412488&fm=26&gp=0.jpg"));
        items.add(new PictureItem("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1237415688,2071220902&fm=26&gp=0.jpg"));

        adapter.setItems(items);
        setContentView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        ThreadPool.post(() -> {
            Glide.get(this).clearDiskCache();
        });
        super.onDestroy();
    }
}
