package com.example.defaultaccount.filedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

import com.example.defaultaccount.filedemo.BR;
import com.example.defaultaccount.filedemo.databinding.ListItemBinding;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DefaultAccount on 2017/8/30.
 */

public class MainActivityViewModel implements ViewModel {
    public final ObservableArrayList<ViewModel> items = new ObservableArrayList<>();
    public final ItemBinding<ViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.list_item);
    private RxAppCompatActivity rxActivity;
    File[] files;
    private String dirName = FileUtils.DIR_NAME;
    int mDeleteBtnState;

    public MainActivityViewModel(RxAppCompatActivity rxActivity) {
        this.rxActivity = rxActivity;
        refreshFileList();
    }

    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rxActivity);
        // Get the layout inflater
        LayoutInflater inflater = rxActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setTitle("新建文件")
                .setMessage("请输入文件名")
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        try {
                            boolean exist = false;
                            EditText editText = (EditText) dialogView.findViewById(R.id.et_add);
                            String fileName = editText.getText().toString();
                            //判断文件是否已存在
                            for (File file : files) {
                                if (file.getName().equals(fileName))
                                    exist = true;
                            }
                            if (!exist)
                                FileClient.getInstance(rxActivity).rigistConsumer(s -> {
                                    refreshFileList();
                                }).writeFile(dirName, fileName, "");
                            else
                                Toast.makeText(rxActivity,"文件已存在",Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void refreshFileList() {
        items.clear();
        files = FileClient.getInstance(rxActivity).getFileList(dirName);
        List<File> sortedFiles=FileSortUtil.sortByTime(Arrays.asList(files));
        for (File file : sortedFiles) {
            items.add(new ItemViewModel(rxActivity, file));
        }

    }
}
