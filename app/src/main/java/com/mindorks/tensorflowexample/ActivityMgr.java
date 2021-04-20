package com.mindorks.tensorflowexample;

import android.app.Activity;

import java.util.Stack;

/**
 * 用于管理Activity,获取Activity
 * 在结束一个activity后应该判断当前栈是否为空,为空则将本类引用置为null,以便于虚拟机回收内存
 * 单例,调用 {@link #getActivityManager()} 获取实例
 * 成员变量 {@link #mActivityStack} 应该与系统的回退栈保持一致,所以在启动activity的时候必须在其onCreate中
 * 将该activity加入栈顶,在activity结束时,必须在onDestroy中将该activity出栈
 */

public class ActivityMgr {

    private static Stack<Activity> mActivityStack;    //Activity栈
    private static ActivityMgr mInstance;

    private ActivityMgr() {
        mActivityStack = new Stack<>();
    }

    /**
     * 获取ActivityManager的单例.
     *
     * @return ActivityManager实例
     */
    public static ActivityMgr getActivityManager() {
        if (mInstance == null) {
            mInstance = new ActivityMgr();
        }
        return mInstance;
    }

    /**
     * 添加一个activity到栈顶.
     *
     * @param activity 添加的activity
     */
    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.push(activity);
    }

    /**
     * 获取栈顶的Activity.
     *
     * @return 如果栈存在, 返回栈顶的activity
     */
    public Activity peekActivity() {
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            return mActivityStack.peek();
        } else {
            return null;
        }
    }

    /**
     * 结束当前的activity,在activity的onDestroy中调用.
     */
    public void popActivity() {
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            mActivityStack.pop();
        }
        //如果移除一个activity之后栈为空,将本类的引用取消,以便于让虚拟机回收
        if (mActivityStack != null && mActivityStack.isEmpty()) {
            mInstance = null;
        }
    }

    /**
     * 结束最接近栈顶的匹配类名的activity.
     * 遍历到的不一定是被结束的,遍历是从栈底开始查找,为了确定栈中有这个activity,并获得一个引用
     * 删除是从栈顶查找,结束查找到的第一个
     * 在activity外结束activity时调用
     *
     * @param klass 类名
     */
    public void popActivity(Class<? extends Activity> klass) {
        for (Activity activity : mActivityStack) {
            if (activity != null && activity.getClass().equals(klass)) {
                activity.finish();
                break;              //只结束一个activity
            }
        }
    }

    //移除所有的Activity
    public void removeAll() {
        for (Activity activity : mActivityStack) {
            if (activity != null) {
                activity.finish();
                break;
            }
        }
    }
}
