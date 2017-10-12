package com.example.defaultaccount.filedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunjuntao on 16/6/6.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> mDataSet = new ArrayList<>();
    protected Context mContext;
    protected static final int TYPE_HEADER = 0;  //说明是带有Header的
    protected static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    protected static final int TYPE_NORMAL = 2;

    protected boolean isShowFooter = true;

    public void showFooter() {
        isShowFooter = true;

    }

    public void hideFooter() {
        isShowFooter = false;
    }

    public BaseAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (isShowFooter) {
            return mDataSet.size() + 1;
        }
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isShowFooter&&position>(mDataSet.size()-1))
            return TYPE_FOOTER;
        else return TYPE_NORMAL;
    }

    public void refreshItems(List<T> items) {
        mDataSet.clear();
        mDataSet.addAll(items);
        hideFooter();
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        mDataSet.addAll(items);
    }

    public void deleteItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, mDataSet.size() - 1);
    }
}
