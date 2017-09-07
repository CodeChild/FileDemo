package com.example.defaultaccount.filedemo;

import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Toast;


import com.example.defaultaccount.filedemo.model.FileClient;
import com.example.defaultaccount.filedemo.model.FileSortUtil;
import com.example.defaultaccount.filedemo.model.FileUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DefaultAccount on 2017/8/30.
 */

public class MainActivityViewModel implements ViewModel {
    public final ObservableArrayList<ItemViewModel> items = new ObservableArrayList<>();
    public final ObservableField<FileListAdapter> adapter=new ObservableField<>();
    public final ObservableField<RecyclerView.LayoutManager> layoutManager=new ObservableField<>();
    private RxAppCompatActivity rxActivity;
    File[] files;
    private String dirName = FileUtils.DIR_NAME;
    int mDeleteBtnState;

    public MainActivityViewModel(RxAppCompatActivity rxActivity) {
        this.rxActivity = rxActivity;
        adapter.set(new FileListAdapter(rxActivity));
        layoutManager.set(new LinearLayoutManager(rxActivity));
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
        List<File> sortedFiles= FileSortUtil.sortByTime(Arrays.asList(files));
        for (File file : sortedFiles) {
            items.add(new ItemViewModel(rxActivity, file));
        }
        adapter.get().refreshItems(items);
    }
    @BindingAdapter("android:adapter")
    public static void setAdapter(RecyclerView view,RecyclerView.Adapter adapter){
        view.setAdapter(adapter);
    }
    @BindingAdapter("android:layoutManager")
    public static void setLayoutMananger(RecyclerView view, RecyclerView.LayoutManager layoutManager){
        view.setLayoutManager(layoutManager);
    }
}
