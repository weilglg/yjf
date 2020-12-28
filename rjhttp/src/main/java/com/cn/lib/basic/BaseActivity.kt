package com.cn.lib.basic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View

import com.cn.lib.util.ActivityListUtil
import com.cn.lib.util.CommonUtil
import com.cn.lib.util.ToastUtil


abstract class BaseActivity : FragmentActivity(), View.OnClickListener, IBaseView {

    /**
     * 返回layout id
     *
     * @return layout id
     */
    protected abstract fun getContentViewResId(): Int

    /**
     * 获取 toActivity的bundle
     */
    protected val bundle: Bundle?
        get() = if (intent == null) {
            null
        } else intent.getBundleExtra(ACTIVITY_BUNDLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityListUtil.INSTANCE.addActivity(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        if (getContentViewResId() > 0) {
            setContentView(getContentViewResId())
        }
        initViews()
        initListener()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityListUtil.INSTANCE.removeActivity(this)
    }

    override fun onClick(v: View) {
        if (CommonUtil.isFastDoubleClick) {
            return
        }
    }

    /**
     * 初始化控件
     */
    protected open fun initViews() {

    }

    protected open fun initListener() {

    }

    /**
     * 初始化数据
     */
    protected open fun initData() {

    }

    override fun getViewContext(): Context {
        return this
    }

    override fun showLoading(msg: String) {

    }

    override fun hidLoading() {

    }

    override fun showToast(msg: String) {
        ToastUtil.showToast(getViewContext(), msg)
    }

    override fun showToast(resId: Int) {
        ToastUtil.showToast(getViewContext(), resId)
    }

    override fun toActivity(cls: Class<out Activity>) {
        toActivity(this, cls, null)
    }


    override fun toActivity(cls: Class<out Activity>, bundle: Bundle?) {
        toActivity(this, cls, bundle)
    }

    fun toActivity(context: Context, cls: Class<out Activity>, bundle: Bundle?) {
        val intent = Intent(context, cls)
        intent.putExtra(ACTIVITY_BUNDLE, bundle)
        startActivity(intent)
    }

    override fun toActivityForResult(context: Context, cls: Class<out Activity>, requestCode: Int) {
        toActivityForResult(context, cls, requestCode, null)
    }

    override fun toActivityForResult(context: Context, cls: Class<out Activity>, requestCode: Int, options: Bundle?) {
        val intent = Intent(context, cls)
        intent.putExtra(ACTIVITY_BUNDLE, options)
        startActivityForResult(intent, requestCode)
    }


    companion object {
        /**
         * Bundle的Key
         */
        val ACTIVITY_BUNDLE = "ACTIVITY_BUNDLE"

    }


}

