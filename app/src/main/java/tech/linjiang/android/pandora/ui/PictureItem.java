package tech.linjiang.android.pandora.ui;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import tech.linjiang.android.pandora.R;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

public class PictureItem extends BaseItem<String> {
    public PictureItem(String data) {
        super(data);
    }

    @Override
    public void onBinding(int position, UniversalAdapter.ViewPool pool, String data) {
        ImageView view = pool.getView(R.id.image_view);
        Glide.with(view)
                .load(data)
                .into(view);
    }

    @Override
    public int getLayout() {
        return R.layout.item_picture;
    }
}
