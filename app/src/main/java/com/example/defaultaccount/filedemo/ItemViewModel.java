package com.example.defaultaccount.filedemo;


import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DefaultAccount on 2017/8/30.
 */

public class ItemViewModel implements ViewModel {
    public final ObservableField<String> fileName = new ObservableField<>();
    public final ObservableField<String> date = new ObservableField<>();
    public final ObservableField<Integer> scrollX = new ObservableField<>(0);
    public final ObservableField<View.OnTouchListener> onTouchListener=new ObservableField<>();
    private RxAppCompatActivity mRxActivity;
    private int mLastX=0,mLastY=0;
    //用于表示册划菜单的显示状态：0：关闭，1：将要关闭，2：将要打开，3：打开
    private int mDeleteBtnState = 0;
//        public final ResponseCommand<MotionEvent,Boolean> onTouchCommand=new ResponseCommand<MotionEvent,Boolean>(e -> {
//        View layout= LayoutInflater.from(mRxActivity).inflate(R.layout.list_item, null, false);
//        TextView mDelete = (TextView) layout.findViewById(R.id.tv_delete);
//        int mMaxLength = mDelete.getWidth();
//        int x = (int) e.getX();
//        int y = (int) e.getY();
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if(mDeleteBtnState== 3)
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                scrollX.set(x);
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return false;
//    });
    private File file;

    public void onClick(View view) {
        if (file != null) {
            FileClient.getInstance(mRxActivity).rigistConsumer((s) -> {
                Intent intent = new Intent(mRxActivity, TextDetail.class);
                intent.putExtra("content", s);
                intent.putExtra("title", fileName.get());
                intent.putExtra("path", file.getPath());
                mRxActivity.startActivity(intent);
            }).readFile(file.getPath());

        }
    }

    public ItemViewModel(RxAppCompatActivity context, File file) {
        String fileName = file.getName();
        Long lastModified = file.lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileDate = sdf.format(date);
        mRxActivity = context;
        this.file = file;
        this.fileName.set(fileName);
        this.date.set(fileDate);
        onTouchListener.set(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                View layout= LayoutInflater.from(mRxActivity).inflate(R.layout.list_item, null, false);
                TextView mDelete = (TextView) layout.findViewById(R.id.tv_delete);
                int mMaxLength = mDelete.getWidth();
                int x = (int) e.getX();
                int y = (int) e.getY();
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mDeleteBtnState == 3)

                            break;
                    case MotionEvent.ACTION_MOVE:
                        scrollX.set(x/2);
                        break;
                    case MotionEvent.ACTION_UP:
                        mLastX=x;
                        break;
                }
                return true;
            }
        });
    }
    @BindingAdapter("bind:onTouch")
    public static void onTouch(View view,View.OnTouchListener onTouchListener){
        view.setOnTouchListener(onTouchListener);
    }
}
