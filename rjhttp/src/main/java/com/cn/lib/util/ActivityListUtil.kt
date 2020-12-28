package com.cn.lib.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*


/**
 * activity 集合
 *
 * @author jianglihui
 */
class ActivityListUtil {

    private var mainActivity: Class<*>? = null

    // Activity栈
    private var activityStack: Stack<Activity> = Stack()

    fun setMainActivity(mainActivity: Class<*>) {
        this.mainActivity = mainActivity
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (!activityStack.contains(activity)) {
            activityStack.add(activity)
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack.lastElement()

    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishLastActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activityStack.remove(activity)
            activity.finish()
        }
    }

    fun removeActivity(activity: Activity?) {
        activityStack.remove(activity)
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @JvmOverloads
    fun finishAllActivity(isFinishMainActivity: Boolean = false) {
        for (item in activityStack){
            if (mainActivity == item && !isFinishMainActivity){
                continue
            }
            item.finish()
        }
    }

    /**
     * 退出应用程序
     */
    fun appExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(context.packageName)
            System.exit(0)
        } catch (e: Exception) {
        }

    }

    /**
     * 结束多个Acitivity
     */
    fun exitAccount(num: Int) {
        val size = activityStack.size - 1
        val index = size - num
        if (index > 0) {
            for (i in size downTo index + 1) {
                if (activityStack[i] != null) {
                    activityStack[i].finish()
                    activityStack.removeAt(i)
                }
            }
        }
    }

    fun returnToActivity(clz: Class<out Activity>): Activity? {
        var activity: Activity? = null
        val activities = ArrayList<Activity>()
        val size = activityStack.size
        for (i in 0 until size) {
            val a = activityStack[size - i - 1]
            if (a.javaClass == clz) {
                activity = a
                break
            }
            activities.add(a)
        }
        if (activity != null) {
            for (a in activities) {
                a.finish()
            }
            return activity
        }
        return null
    }

    companion object {

        val INSTANCE: ActivityListUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ActivityListUtil() }
    }

}
