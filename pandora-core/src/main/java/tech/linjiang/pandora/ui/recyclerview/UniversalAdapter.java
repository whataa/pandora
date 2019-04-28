package tech.linjiang.pandora.ui.recyclerview;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;

/**
 * Created by linjiang on 03/06/2018.
 */
public class UniversalAdapter
        extends RecyclerView.Adapter<UniversalAdapter.ViewPool>
        implements View.OnClickListener, View.OnLongClickListener {

    private List<BaseItem> data = new ArrayList<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public void setItems(List<? extends BaseItem> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void insertItems(List<? extends BaseItem> items, int index) {
        data.addAll(index, items);
        notifyDataSetChanged();
    }
    public void insertItem(BaseItem items, int index) {
        data.add(index, items);
        notifyDataSetChanged();
    }

    public void insertItem(BaseItem items) {
        data.add(items);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        data.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, getItemCount() - index);
    }

    public List<BaseItem> getItems() {
        return data;
    }

    public <T extends BaseItem> T getItem(int position) {
        return (T) data.get(position);
    }

    public void clearItems() {
        data.clear();
        notifyDataSetChanged();
    }

    public void performClick(int position) {
        if (listener != null) {
            listener.onItemClick(position, data.get(position));
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    @Override
    public ViewPool onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewPool(view);
    }

    @Override
    public void onBindViewHolder(ViewPool holder, int position) {
        holder.itemView.setTag(R.id.pd_recycler_adapter_id, position);
        data.get(position).onBinding(position, holder, data.get(position).data);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getLayout();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = (int) v.getTag(R.id.pd_recycler_adapter_id);
            listener.onItemClick(position, data.get(position));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longListener != null) {
            int position = (int) v.getTag(R.id.pd_recycler_adapter_id);
            return longListener.onItemLongClick(position, data.get(position));
        }
        return false;
    }

    public static final class ViewPool extends RecyclerView.ViewHolder {

        private final SparseArray<View> views;

        public ViewPool(View itemView) {
            super(itemView);
            views = new SparseArray<>();
        }

        public <T extends View> T getView(@IdRes int id) {
            if (id == View.NO_ID) {
                throw new RuntimeException("id is invalid");
            }
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return (T) view;
        }

        public ViewPool setText(@IdRes int id, String text) {
            TextView tv = getView(id);
            tv.setText(text);
            return this;
        }

        public ViewPool setCompoundDrawableLeft(@IdRes int id, @DrawableRes int left) {
            TextView tv = getView(id);
            tv.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
            return this;
        }

        public ViewPool setTextColor(@IdRes int id, @ColorInt int color) {
            TextView tv = getView(id);
            tv.setTextColor(color);
            return this;
        }

        public ViewPool setImageResource(@IdRes int id, @DrawableRes int resId) {
            ImageView tv = getView(id);
            tv.setImageResource(resId);
            return this;
        }

        public ViewPool setTextGravity(@IdRes int id, int gravity) {
            TextView tv = getView(id);
            tv.setGravity(gravity);
            return this;
        }

        public ViewPool setVisibility(@IdRes int id, int visibility) {
            View v = getView(id);
            v.setVisibility(visibility);
            return this;
        }

        public ViewPool setBackgroundColor(@IdRes int id, @ColorInt int color) {
            View v = getView(id);
            v.setBackgroundColor(color);
            return this;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, BaseItem item);
    }
    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, BaseItem item);
    }
}
