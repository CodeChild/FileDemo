package com.example.defaultaccount.filedemo;

import android.databinding.ObservableField;

import com.example.defaultaccount.filedemo.model.FileClient;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by DefaultAccount on 2017/8/31.
 */

public class TextDetailViewModel implements ViewModel {
    public static final ObservableField<String> content=new ObservableField<>();
    public static final ObservableField<String> title=new ObservableField<>();
    private RxAppCompatActivity rxActivity;
    public TextDetailViewModel(RxAppCompatActivity rxActivity,String content,String title){
        this.rxActivity=rxActivity;
        this.content.set(content);
        this.title.set(title);
    }
    public void saveFile(String path,String content){
        FileClient.getInstance(rxActivity).saveFile(path,content);
    }
}
