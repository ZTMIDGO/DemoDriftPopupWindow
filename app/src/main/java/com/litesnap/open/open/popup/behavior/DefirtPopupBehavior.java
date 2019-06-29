package com.litesnap.open.open.popup.behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import com.litesnap.open.open.popup.behavior.source.ViewOffsetBehavior;
import com.litesnap.open.open.popup.view.MyCoordinatorLayout;

public class DefirtPopupBehavior extends ViewOffsetBehavior<View> {
    public static final String TAG = "DefirtPopupBehavior";

    private static final int SCROLL_STATE_IDLE = 0;
    private static final int SCROLL_STATE_DRAGGING = 1;
    private static final int SCROLL_STATE_UN_CONSUMED = 2;

    private int mLastY;
    private View mChild;
    private View mParent;

    private int mScrollStatu = SCROLL_STATE_IDLE;
    private boolean mCanUnConsumed = true;
    private boolean mCanOffset = false;
    private boolean mIsClose;
    private float mPercen = 0.30f;
    private boolean mIsLayoutOk;

    private final List<ObjectAnimator> mAnimationList;
    private VelocityTracker mVelocityTracker;

    public DefirtPopupBehavior() {
        this(null, null);
    }

    public DefirtPopupBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnimationList = new ArrayList<>();
    }

    @Override
    public boolean onMeasureChild(@NonNull final CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        mChild = child;
        mParent = parent;
        setCallback((MyCoordinatorLayout) parent);
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        super.onLayoutChild(parent, child, layoutDirection);
        Rect rect = new Rect();
        child.getHitRect(rect);
        int height = parent.getMeasuredHeight();
        rect.top = (int) (height * mPercen);
        rect.bottom = rect.top + height;
        child.layout(rect.left, rect.top, rect.right, rect.bottom);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        int[] point = new int[2];
        child.getLocationInWindow(point);
        int top = point[1];

        if (dy > 0 && top > 0 && mCanOffset){
            if (top < child.getMeasuredHeight()){
                top -= dy;
                int offsetY = top <= 0 ? dy + top : dy;
                if (mCanOffset){
                    child.offsetTopAndBottom(- offsetY);
                }
            }
        }

        if (dy > 0 && top > 0){
            if (top < parent.getMeasuredHeight()){
                consumed[1] = dy;
            }
        }

        super.onNestedPreScroll(parent, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        int[] point = new int[2];
        child.getLocationInWindow(point);
        int windowBottom = child.getHeight();
        int top = point[1];

        if (dyConsumed < 0){
            mScrollStatu = SCROLL_STATE_DRAGGING;
            mCanUnConsumed = false;
        }else if (dyUnconsumed < 0){
            mScrollStatu = SCROLL_STATE_UN_CONSUMED;
        }

        if (dyUnconsumed < 0 && mCanUnConsumed && mCanOffset){
            if (top < windowBottom){
                int calcuTop = top - dyUnconsumed;
                int offsetY = calcuTop >= windowBottom ? top - windowBottom : dyUnconsumed;
                child.offsetTopAndBottom(- offsetY);
            }
        }

        super.onNestedScroll(parent, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        if (mIsClose){
            return mIsClose;
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    public void setCallback(final MyCoordinatorLayout parent){
        parent.setCallback(new MyCoordinatorLayout.OnDispatchTouchEventCallback() {
            @Override
            public void onCallback(MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (mScrollStatu == SCROLL_STATE_UN_CONSUMED){
                            mCanUnConsumed = true;
                        }

                        mCanOffset = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCanOffset = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mCanOffset = false;
                        int[] point = new int[2];
                        mChild.getLocationInWindow(point);
                        int top = point[1];
                        int limit = (int) (parent.getHeight() * mPercen);
                        int wrap = limit / 2;

                        if (!isTouchInGlobaRect(mChild, event)){
                            startCloseAnimation();
                            return;
                        }

                        if (top < mChild.getHeight() / 2){
                            if (top > wrap){
                                startDownAnimation();
                            }else if (top < wrap){
                                startUpAnimation();
                            }
                        }else {
                            startCloseAnimation();
                        }
                        break;
                }
            }
        });
    }

    private void startDownAnimation(){
        for (ObjectAnimator animator : mAnimationList){
            animator.cancel();
            animator.end();
        }
        mAnimationList.clear();

        Rect rect = new Rect();
        mChild.getGlobalVisibleRect(rect);

        int end = (int) (rect.height() - mChild.getHeight() * (1.0f - mPercen));
        ObjectAnimator animator = ObjectAnimator.ofInt(new Spac(), "DownSpac", 0, end);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimationList.add(animator);
        animator.start();
    }

    private void startUpAnimation(){
        for (ObjectAnimator animator : mAnimationList) {
            animator.cancel();
            animator.end();
        }
        mAnimationList.clear();

        Rect rect = new Rect();
        mChild.getGlobalVisibleRect(rect);
        int end = mChild.getHeight() - rect.height();
        ObjectAnimator animator = ObjectAnimator.ofInt(new Spac(), "UpSpac", 0, -end);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimationList.add(animator);
        animator.start();
    }

    private void startCloseAnimation(){
        for (ObjectAnimator animator : mAnimationList) {
            animator.cancel();
            animator.end();
        }
        mIsClose = true;
        mAnimationList.clear();

        Rect rect = new Rect();
        mChild.getGlobalVisibleRect(rect);
        int end = rect.height();
        ObjectAnimator animator = ObjectAnimator.ofInt(new Spac(), "UpSpac", 0, end);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ((MyCoordinatorLayout)mParent).close();
            }
        });
        mAnimationList.add(animator);
        animator.start();
    }

    public boolean isTouchInGlobaRect(View view, MotionEvent event){
        Rect rect = new Rect();
        int[] point = new int[2];
        view.getGlobalVisibleRect(rect);
        view.getLocationOnScreen(point);
        rect.top = point[1];
        rect.bottom = rect.top + view.getHeight();

        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();
        if (touchX >= rect.left&& touchX <= rect.right && touchY >= rect.top && touchY <= rect.bottom){
            return true;
        }else {
            return false;
        }
    }

    private class Spac{
        private int num;

        private void setDownSpac(int x){
            int offset = x - num;
            mChild.offsetTopAndBottom(offset);
            num = x;
        }

        private void setUpSpac(int x){
            int offset = x - num;
            mChild.offsetTopAndBottom(offset);
            num = x;
        }
    }
}
