package com.litesnap.open.open.popup.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyCoordinatorLayout extends CoordinatorLayout {
    private OnDispatchTouchEventCallback callback;
    private OnLayoutChangeListener mLayoutListener;

    public MyCoordinatorLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (callback != null){
            callback.onCallback(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void close(){
        if (mLayoutListener != null){
            mLayoutListener.onClose();
        }
    }

    public void setCallback(OnDispatchTouchEventCallback callback) {
        this.callback = callback;
    }

    public void setLayoutListener(OnLayoutChangeListener listener) {
        this.mLayoutListener = listener;
    }

    public interface OnDispatchTouchEventCallback{
        void onCallback(MotionEvent event);
    }

    public interface OnLayoutChangeListener{
        void onClose();
    }
}
