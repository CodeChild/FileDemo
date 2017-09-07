package com.example.defaultaccount.filedemo;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.example.defaultaccount.filedemo.model.FileClient;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DefaultAccount on 2017/8/30.
 */

public class ItemViewModel implements ViewModel {
    public final ObservableField<String> fileName = new ObservableField<>();
    public final ObservableField<String> date = new ObservableField<>();
    public final ObservableField<Integer> scrollX = new ObservableField<>(0);
    public final ObservableField<View.OnTouchListener> onTouchListener = new ObservableField<>();
    public final ObservableField<View.OnClickListener> deleteOnClickListener=new ObservableField<>();
    private RxAppCompatActivity mRxActivity;
    private int position;
    private static int positionOffset=0;
    private static int deletedPosition=0;
    private VelocityTracker mVelocityTracker;
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

    public ItemViewModel(RxAppCompatActivity context, File file,int position) {
        String fileName = file.getName();
        this.position=position;
        Long lastModified = file.lastModified();
        Date date = new Date(lastModified);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileDate = sdf.format(date);
        mRxActivity = context;
        this.file = file;
        this.fileName.set(fileName);
        this.date.set(fileDate);
        mVelocityTracker = VelocityTracker.obtain();
        deleteOnClickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityViewModel.deleteItem(position);
            }
        });
        onTouchListener.set(new View.OnTouchListener() {
            private int mLastX = 0, mLastY = 0;
            //用于表示册划菜单的显示状态：0：关闭，1：将要关闭，2：将要打开，3：打开
            private int mDeleteBtnState = 0;
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                final int mMaxLength = (int) convertDpToPixel(60, mRxActivity.getApplicationContext());
                mVelocityTracker.addMovement(e);
                int x = (int) e.getX();
                int y = (int) e.getY();
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mDeleteBtnState == 0)
                            ;
                        else if (mDeleteBtnState == 3) {
                            scrollX.set(scrollX.get() - mMaxLength);
                            mDeleteBtnState=0;
                            return false;
                        }
                        else
                            return false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = mLastX - x;
                        int dy = mLastY - y;
                        int mScrollX = scrollX.get();
                        Log.d("dx", Integer.toString(dx));
                        if (Math.abs(dx) > Math.abs(dy)) {
                            Log.d("scrollX", Integer.toString(scrollX.get()));
                            if (mScrollX + dx <= 0) {//左边界检测
                                Log.d("left", "ss");
                                scrollX.set(0);
                                return true;
                            } else if (mScrollX + dx >= mMaxLength) {//右边界检测
                                Log.d("right", "ss");
                                scrollX.set(mMaxLength);
                                return true;
                            }
                            scrollX.set(scrollX.get()+dx);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mVelocityTracker.computeCurrentVelocity(1000);//计算手指滑动的速度
                        float xVelocity = mVelocityTracker.getXVelocity();//水平方向速度（向左为负）
                        float yVelocity = mVelocityTracker.getYVelocity();//垂直方向速度
                        int deltaX = 0;
                        int upScrollX = scrollX.get();

                        if (Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            if (xVelocity <= -100) {//左滑速度大于100，则删除按钮显示
                                deltaX = mMaxLength - upScrollX;
                                mDeleteBtnState = 2;
                            } else if (xVelocity > 100) {//右滑速度大于100，则删除按钮隐藏
                                deltaX = -upScrollX;
                                mDeleteBtnState = 1;
                            }
                        }else {
                            if (upScrollX >= mMaxLength / 2) {//item的左滑动距离大于删除按钮宽度的一半，则则显示删除按钮
                                deltaX = mMaxLength - upScrollX;
                                mDeleteBtnState = 2;
                            } else if (upScrollX < mMaxLength / 2) {//否则隐藏
                                deltaX = -upScrollX;
                                mDeleteBtnState = 1;
                            }
                        }
                        scrollX.set(upScrollX-deltaX);
                        mVelocityTracker.clear();
                        break;
                }
                mLastX = x;
                mLastY = y;
                return view.onTouchEvent(e);
            }
        });
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public void deleteFile(){
        FileClient.getInstance(mRxActivity).deleteFile(file.getPath());
    }
    @BindingAdapter("android:onTouch")
    public static void setOnTouch(View view,View.OnTouchListener onTouchListener){
        view.setOnTouchListener(onTouchListener);
    }
    @BindingAdapter("android:onClickListener")
    public static void setOnClickListener(View view,View.OnClickListener onClickListener){
        view.setOnClickListener(onClickListener);
    }
}
