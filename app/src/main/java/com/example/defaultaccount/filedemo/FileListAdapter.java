package com.example.defaultaccount.filedemo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.defaultaccount.filedemo.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by DefaultAccount on 2017/9/6.
 */

public class FileListAdapter extends BaseAdapter<ItemViewModel> {
    private boolean isMultiSelectMode=false;
    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(mContext);
        View thisItemsView;
        if (viewType==TYPE_FOOTER)
            thisItemsView = myInflater.inflate(R.layout.list_item_footer,
                    parent, false);
        else
            thisItemsView = myInflater.inflate(R.layout.list_item, parent, false);
        return new FileListViewHolder(thisItemsView,viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        FileListViewHolder fileListViewHolder = (FileListViewHolder) holder;
        if (getItemViewType(position)!=TYPE_FOOTER) {
            ItemViewModel item = mDataSet.get(position);
            ((FileListViewHolder) holder).setItemViewModel(item).initData();
        }

    }

    public class FileListViewHolder extends BaseViewHolder {
        ListItemBinding listItemBinding;
        ItemViewModel itemViewModel;
        TextView textView;
        CardView cardView;
        public FileListViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType==TYPE_FOOTER)
                return;
            else{
                listItemBinding = DataBindingUtil.bind(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv_delete);
                cardView = (CardView) itemView.findViewById(R.id.cv_item);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivityViewModel.deleteItem(getLayoutPosition());
                        itemViewModel.deleteFile();
                    }
                });
            }
        }

        public FileListViewHolder setItemViewModel(ItemViewModel itemViewModel) {
            this.itemViewModel = itemViewModel;
            return this;
        }

        public void initData() {
            if (itemViewModel != null && listItemBinding != null)
                listItemBinding.setViewModel(itemViewModel);
        }
    }
}
