package com.cn.lib.basic

import android.app.Activity
import android.content.Context
import android.os.Bundle

/**
 * Created by admin on 2017/4/17.
 */

interface IBaseView {

    fun getViewContext(): Context

    /**
     * 显示加载框
     */
    fun showLoading(msg: String)

    /**
     * 隐藏加载框
     */
    fun hidLoading()

    /**
     * 提示语
     *
     * @param msg
     */
    fun showToast(msg: String)

    /**
     * 提示语
     *
     * @param resId
     */
    fun showToast(resId: Int)

    fun toActivity(cls: Class<out Activity>)

    fun toActivity(cls: Class<out Activity>, bundle: Bundle?)

    fun toActivityForResult(context: Context, cls: Class<out Activity>, requestCode: Int)

    fun toActivityForResult(context: Context, cls: Class<out Activity>, requestCode: Int, options: Bundle?)

}
