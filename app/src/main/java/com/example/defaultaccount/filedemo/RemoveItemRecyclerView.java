package com.example.defaultaccount.filedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by DefaultAccount on 2017/9/4.
 */

public class RemoveItemRecyclerView extends RecyclerView {
    private VelocityTracker mVelocityTracker;
    int mDeleteBtnState,mPosition,mMaxLength,mLastX,mLastY;
    View mItemLayout;
    TextView mDelete;
    Scroller mScroller;
    View layout;
    public RemoveItemRecyclerView(Context context) {
        super(context);
        mScroller = new Scroller(context);
        layout = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
        }
        mVelocityTracker.addMovement(e);

        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteBtnState == 0) {
                    View view = findChildViewUnder(x, y);
                    if (view == null) {
                        return false;
                    }

                    MyViewHolder viewHolder = (MyViewHolder) getChildViewHolder(view);

                    mItemLayout = layout;
                    mPosition = viewHolder.getAdapterPosition();

                    mDelete = (TextView) mItemLayout.findViewById(R.id.tv_delete);
                    mMaxLength = mDelete.getWidth();
                    mDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemLayout.scrollTo(0, 0);
                            mDeleteBtnState = 0;
                        }
                    });
                } else if (mDeleteBtnState == 3) {
                    mScroller.startScroll(mItemLayout.getScrollX(), 0, -mMaxLength, 0, 200);
                    invalidate();
                    mDeleteBtnState = 0;
                    return false;
                } else {
                    return false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.onTouchEvent(e);
    }

    private class MyViewHolder extends ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);

        }
    }
}
