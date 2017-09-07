package com.example.defaultaccount.filedemo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.defaultaccount.filedemo.databinding.ListItemBinding;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by DefaultAccount on 2017/9/6.
 */

public class FileListAdapter extends BaseAdapter<ItemViewModel> {
    public FileListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater myInflater = LayoutInflater.from(mContext);
        View thisItemsView = myInflater.inflate(R.layout.list_item,
                parent, false);
        return new FileListViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        FileListViewHolder fileListViewHolder = (FileListViewHolder) holder;
        ItemViewModel item = mDataSet.get(position);
        ((FileListViewHolder) holder).setItemViewModel(item).initData();

    }

    public class FileListViewHolder extends BaseViewHolder {
        ListItemBinding listItemBinding;
        ItemViewModel itemViewModel;
        TextView textView;
        CardView cardView;

        public FileListViewHolder(View itemView) {
            super(itemView);
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

        public FileListViewHolder setItemViewModel(ItemViewModel itemViewModel) {
            this.itemViewModel = itemViewModel;
            return this;
        }

        public void initData() {
            if (itemViewModel != null)
                listItemBinding.setViewModel(itemViewModel);
        }
    }

}
