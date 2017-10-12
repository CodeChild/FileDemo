package com.example.defaultaccount.filedemo;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.example.defaultaccount.filedemo.model.FileClient;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DefaultAccount on 2017/8/30.
 */

public class ItemViewModel implements ViewModel {
    public final ObservableField<String> fileName = new ObservableField<>();
    public final ObservableField<String> date = new ObservableField<>();
    public final ObservableField<Integer> scrollX = new ObservableField<>(0);
    public final ObservableField<View.OnTouchListener> onTouchListener = new ObservableField<>();
    public final ObservableField<View.OnClickListener> deleteOnClickListener = new ObservableField<>();
    private RxAppCompatActivity mRxActivity;
    private VelocityTracker mVelocityTracker;
    //用于表示册划菜单的显示状态：0：关闭，1：将要关闭，2：将要打开，3：打开
    private int mDeleteBtnState = 0;
    private boolean isStartScroll;
    private int mLastX = 0, mLastY = 0;
    private ActionMode.Callback mActionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_item1:
                    actionMode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            actionMode = null;
        }
    };
    ;
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
                mRxActivity.overridePendingTransition(R.anim.schedule_scale_up,R.anim.schedule_scale_down);
            }).readFile(file.getPath());
        } else ;
    }

    public boolean onLongClick(View view) {
        view.startActionMode(mActionModeCallBack);
        return true;
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
        mVelocityTracker = VelocityTracker.obtain();
        onTouchListener.set(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent e) {
                int mMaxLength = (int) convertDpToPixel(60, mRxActivity.getApplicationContext());
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(1000);//计算手指滑动的速度
                float xVelocity = mVelocityTracker.getXVelocity();//水平方向速度（向左为负）
                float yVelocity = mVelocityTracker.getYVelocity();//垂直方向速度
                if (Math.abs(xVelocity) > Math.abs(yVelocity))
                    view.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                int x = (int) e.getX();
                int y = (int) e.getY();
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mDeleteBtnState == 0)
                            ;
                        else if (mDeleteBtnState == 3) {
                            scrollX.set(scrollX.get() - mMaxLength);
                            mDeleteBtnState = 0;
                            return false;
                        } else if (mDeleteBtnState == 2 || mDeleteBtnState == 1)
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
                            mDeleteBtnState = 2;
                            scrollX.set(scrollX.get() + dx);
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        int deltaX = 0;
                        int upScrollX = scrollX.get();

                        if (Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            if (xVelocity <= -10) {//左滑速度大于100，则删除按钮显示
                                deltaX = mMaxLength - upScrollX;
                                mDeleteBtnState = 2;
                            } else if (xVelocity > 10) {//右滑速度大于100，则删除按钮隐藏
                                deltaX = -upScrollX;
                                mDeleteBtnState = 1;
                            }
                        } else {
                            if (upScrollX >= mMaxLength / 2) {//item的左滑动距离大于删除按钮宽度的一半，则则显示删除按钮
                                deltaX = mMaxLength - upScrollX;
                                mDeleteBtnState = 2;
                            } else if (upScrollX < mMaxLength / 2) {//否则隐藏
                                deltaX = -upScrollX;
                                mDeleteBtnState = 1;
                            }
                        }
                        Log.d("touchup", "upScrollX:" + upScrollX + " deltax:" + deltaX);
                        scrollX.set(upScrollX - deltaX);
                        isStartScroll = true;
                        mVelocityTracker.clear();
                        break;
                }
                mLastX = x;
                mLastY = y;
                if (isStartScroll) {
                    isStartScroll = false;
                    if (mDeleteBtnState == 1) {
                        mDeleteBtnState = 0;
                    }
                    if (mDeleteBtnState == 2) {
                        mDeleteBtnState = 3;
                    }
                }
                if (mDeleteBtnState == 3)
                    return true;
                else
                    return false;
            }
        });

    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    //    public void deleteFile(FileListAdapter adapter,int position) {
//        FileClient.getInstance(mRxActivity).rigistConsumer(s -> adapter.deleteItem(position))
//                .deleteFile(file.getPath());
//    }
    public void deleteFile() {
        FileClient.getInstance(mRxActivity)
                .deleteFile(file.getPath());
    }

    @BindingAdapter("android:onTouch")
    public static void setOnTouch(View view, View.OnTouchListener onTouchListener) {
        view.setOnTouchListener(onTouchListener);
    }

    @BindingAdapter("android:onClickListener")
    public static void setOnClickListener(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(onClickListener);
    }
}
