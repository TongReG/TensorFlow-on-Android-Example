package com.mindorks.tensorflowexample;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

public class DragFloatActionButton extends FloatingActionButton {

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    private int virtualHeight;
    private int fabround, fabmargin;

    public DragFloatActionButton(Context context) {
        super(context);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtils.getScreenHeight(getContext());
        statusHeight = ScreenUtils.getStatusHeight(getContext());
        virtualHeight = ScreenUtils.getVirtualBarHeigh(getContext());
        fabround = getSize();
        if (fabround == -1) { //public static final int SIZE_AUTO = -1;
            fabround = 64;
        } else if (fabround == 1) { //public static final int SIZE_MINI = 1;
            fabround = 40;
        } else {
            fabround = 56;
        }
        fabmargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
    }

    private int lastX;
    private int lastY;

    private boolean isDrag;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                Log.d("ACTION_DOWN ----> ", "getX=" + getX() + " screenWidthHalf=" + screenWidthHalf);
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                Log.d("MoveDistance ----> ", distance + "");
                if (distance < 3) {
                    //给个容错范围，不然有部分手机还是无法点击
                    isDrag = false;
                    break;
                }

                float x = getX() + dx;
                float y = getY() + dy;

                //检测是否到达边缘 左上右下
                x = (x < fabmargin) ? fabmargin : x > screenWidth - getWidth() - fabmargin ? screenWidth - getWidth() - fabmargin : x;
                // y = y < statusHeight ? statusHeight : (y + getHeight() >= screenHeight ? screenHeight - getHeight() : y);
                if (y < fabmargin) {
                    y = fabmargin;
                }
                if (y > screenHeight - statusHeight - virtualHeight - fabmargin - getHeight()) {
                    if (virtualHeight == 0) {
                        y = screenHeight - statusHeight - fabmargin - getHeight();
                    } else {
                        y = screenHeight - statusHeight - virtualHeight - fabmargin - getHeight();
                    }
                }
                setX(x);
                setY(y);

                lastX = rawX;
                lastY = rawY;
                Log.d("ACTION_MOVE ----> ", "getX=" + getX() + " y=" + y + " screenWidthHalf=" + screenWidthHalf + " isDrag=" + isDrag + " statusHeight=" + statusHeight + " virtualHeight=" + virtualHeight + " screenHeight=" + screenHeight + " getHeight=" + getHeight());
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
                    Log.d("ACTION_UP ----> ", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX() - fabmargin)
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), getX());
 /*                       if (getX() < fabmargin) {
                            oa = ObjectAnimator.ofFloat(this, "x", fabmargin, 0);
                        } else {
                            oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        }*/
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }
                }
                else {
                    Log.d("ACTION_UP_FALSE ----> ", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX() - fabmargin)
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), getX());
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }
                }
                Log.d("ACTION_UP ----> ", "isDrag=" + isDrag);
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);

    }
}
