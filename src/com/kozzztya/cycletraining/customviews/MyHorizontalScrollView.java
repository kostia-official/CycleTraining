package com.kozzztya.cycletraining.customviews;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;

/*
    HorizontalScrollView with click events
*/

public class MyHorizontalScrollView extends HorizontalScrollView implements OnTouchListener {

    private OnScrollViewClickListener mOnScrollViewClickListener;
    private int mPosition;

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void configure(OnScrollViewClickListener onScrollViewClickListener, int position) {
        mOnScrollViewClickListener = onScrollViewClickListener;
        mPosition = position;
        setOnTouchListener(this);
    }

    @Override
    final public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            MyHorizontalScrollView.this.setPressed(true);
            mOnScrollViewClickListener.onScrollViewClick(MyHorizontalScrollView.this, mPosition);
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Old versions catch long click without gestureDetector
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                mOnScrollViewClickListener.onScrollViewLongClick(MyHorizontalScrollView.this, mPosition);
            }
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //Set long click state
            MyHorizontalScrollView.this.setPressed(true);
        }
    };

    private GestureDetector gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);

    public interface OnScrollViewClickListener {

        public void onScrollViewClick(View view, int position);

        public void onScrollViewLongClick(View view, int position);

    }
}
