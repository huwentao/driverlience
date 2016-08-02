package dl.cs.org.driverlinence.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huwentao on 16/8/2.
 */
public abstract class AppAdapter<T> extends BaseAdapter {
    private List<T> tList = new ArrayList<>();
    private int itemLayoutResId = 0;
    private Context context;

    public AppAdapter(Context context, List<T> tList, int itemLayoutResId) {
        this.tList = tList;
        this.itemLayoutResId = itemLayoutResId;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tList.size();
    }

    @Override
    public Object getItem(int i) {
        return tList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (itemLayoutResId > 0) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(itemLayoutResId, viewGroup, false);
            }
        }
        T t = tList.get(i);
        onBindView(i, view, t);
        return view;
    }

    public void refresh(List<T> tList) {
        if (this.tList.size() > 0)
            this.tList.clear();
        this.tList.addAll(tList);
        notifyDataSetChanged();
    }

    public void loadMore(List<T> tList) {
        int length = this.tList.size();
        this.tList.addAll(length - 1, tList);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public abstract void onBindView(int position, View view, T t);

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
