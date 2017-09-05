package com.example.defaultaccount.filedemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.defaultaccount.filedemo.databinding.ActivityMainBinding;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;


import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;

import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends RxAppCompatActivity {
    ActivityMainBinding mainBinding;
    MainActivityViewModel viewModel;
    RxAppCompatActivity mRxActivity;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 255;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxActivity=this;
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MainActivityViewModel(this);
        mainBinding.setViewModel(viewModel);
        setSupportActionBar(mainBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 判断是否具有文件读写权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
//        mainBinding.fileList.setOnTouchListener(new View.OnTouchListener() {
//            int mPosition,mMaxLength,mLastX,mLastY;
//            @Override
//            public boolean onTouch(View mView, MotionEvent e) {
//                RecyclerView recyclerView=(RecyclerView) mView;
//                VelocityTracker mVelocityTracker = null;
//                boolean isItemMoving=false;
//                View mItemLayout;
//                TextView mDelete;
//                Scroller mScroller;
//                mScroller = new Scroller(recyclerView.getContext());
//                mDeleteBtnState=0;
//                if (mVelocityTracker == null) {
//                    mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
//                    mLastX=(int) e.getX();
//                    mLastY=(int) e.getY();
//                }
//                mVelocityTracker.addMovement(e);
//
//                int x = (int) e.getX();
//                int y = (int) e.getY();
//                mItemLayout = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.list_item, null, false);
////                    mPosition = viewHolder.getAdapterPosition();
//                View view = recyclerView.findChildViewUnder(x, y);
//                mDelete = (TextView) mItemLayout.findViewById(R.id.tv_delete);
//                mMaxLength = mDelete.getWidth();
//                switch (e.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (mDeleteBtnState == 0) {
//                            if (view == null) {
//                                return false;
//                            }
//                            mDelete.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mItemLayout.scrollTo(0, 0);
//                                    mDeleteBtnState = 0;
//                                }
//                            });
//                        } else if (mDeleteBtnState == 3) {
//                            mScroller.startScroll(mItemLayout.getScrollX(), 0, -mMaxLength, 0, 200);
//                            recyclerView.invalidate();
//                            mDeleteBtnState = 0;
//                            return false;
//                        } else {
//                            return false;
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int dx = mLastX - x;
//                        int dy = mLastY - y;
//
//                        int scrollX = mItemLayout.getScrollX();
//                        if (Math.abs(dx) > Math.abs(dy)) {
//                            isItemMoving = true;
//                            if (scrollX + dx <= 0) {//左边界检测
//                                mItemLayout.scrollTo(0, 0);
//                                return true;
//                            } else if (scrollX + dx >= mMaxLength) {//右边界检测
//                                mItemLayout.scrollTo(mMaxLength, 0);
//                                return true;
//                            }
//                            mItemLayout.scrollTo(dx, 0);//item跟随手指滑动
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
////                        if (!isItemMoving && !isDragging && mListener != null) {
////                            mListener.onItemClick(mItemLayout, mPosition);
////                        }
//                        isItemMoving = false;
//
//                        mVelocityTracker.computeCurrentVelocity(1000);//计算手指滑动的速度
//                        float xVelocity = mVelocityTracker.getXVelocity();//水平方向速度（向左为负）
//                        float yVelocity = mVelocityTracker.getYVelocity();//垂直方向速度
//
//                        int deltaX = 0;
//                        int upScrollX = mItemLayout.getScrollX();
//
//                        if (Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)) {
//                            if (xVelocity <= -100) {//左滑速度大于100，则删除按钮显示
//                                deltaX = mMaxLength - upScrollX;
//                                mDeleteBtnState = 2;
//                            } else if (xVelocity > 100) {//右滑速度大于100，则删除按钮隐藏
//                                deltaX = -upScrollX;
//                                mDeleteBtnState = 1;
//                            }
//                        } else {
//                            if (upScrollX >= mMaxLength / 2) {//item的左滑动距离大于删除按钮宽度的一半，则则显示删除按钮
//                                deltaX = mMaxLength - upScrollX;
//                                mDeleteBtnState = 2;
//                            } else if (upScrollX < mMaxLength / 2) {//否则隐藏
//                                deltaX = -upScrollX;
//                                mDeleteBtnState = 1;
//                            }
//                        }
//
//                        //item自动滑动到指定位置
//                        mScroller.startScroll(upScrollX, 0, deltaX, 0, 200);
////                        isStartScroll = true;
//                        recyclerView.invalidate();
//                        mVelocityTracker.clear();
//                        break;
//                }
//                mLastX = x;
//                mLastY = y;
//                boolean isStartScroll=true;
//                if (mScroller.computeScrollOffset()) {
//                    mItemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//                    recyclerView.invalidate();
//                } else if (isStartScroll) {
//                    isStartScroll = false;
//                    if (mDeleteBtnState == 1) {
//                        mDeleteBtnState = 0;
//                    }
//                    if (mDeleteBtnState == 2) {
//                        mDeleteBtnState = 3;
//                    }
//                }
//
//                return recyclerView.onTouchEvent(e);
//            }
//        });
        File file;
//        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2012, 0, 19, 7, 30);
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2012, 0, 19, 10, 30);
//        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
//        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
//        calendarIntent.putExtra(CalendarContract.Events.TITLE, "Ninja class");
//        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Secret dojo");
//        PackageManager packageManager = getPackageManager();
//        List activities = packageManager.queryIntentActivities(calendarIntent,
//                PackageManager.MATCH_DEFAULT_ONLY);
//        boolean isIntentSafe = activities.size() > 0;
//        if(isIntentSafe)
//            startActivity(calendarIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initData();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initData() {
        String filename = "my file";
        String text = "Hello World";
        FileClient.getInstance(this).writeFile("texts", "test.txt", text);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        viewModel.refreshFileList();
        super.onResume();
    }
}
