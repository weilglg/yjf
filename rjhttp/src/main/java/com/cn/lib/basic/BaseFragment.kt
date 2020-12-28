package com.cn.lib.basic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cn.lib.util.ToastUtil


/**
 * Created by admin on 2017/4/17.
 */


abstract class BaseFragment : Fragment(), IBaseView {

    /**
     * 当前的Fragment是否可见
     */
    private var mCurrentViewIsVisible: Boolean = false
    /**
     * 标志位，标志布局已经加载完成
     */
    protected var isPrepared: Boolean = false
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     *
     *
     * 子类在加载完成数据后要把该值修改为true
     *
     */
    private var isHasLoadedOnce = false

    private var rootView: View? = null

    /**
     * 返回要加载的布局文件的ID
     */
    protected abstract val contentViewResId: Int

    init {
        rootView = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userVisibleHint = userVisibleHint
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutResID = contentViewResId
        if (layoutResID > 0 && rootView == null) {
            val view = inflateView(layoutResID)
            isPrepared = true
            isHasLoadedOnce = false
            return view
        } else if (rootView != null) {
            val parent = rootView!!.parent as ViewGroup
            parent.removeView(rootView)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this.rootView == null) {
            this.rootView = view
            initViews(view)
            initListener(view)
        }
    }


    /**
     * 重写此方法是为了延迟预加载
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (rootView == null) return
        if (userVisibleHint && isVisibleToUser) {
            mCurrentViewIsVisible = true
            onCurrentViewVisible()
        } else {
            mCurrentViewIsVisible = false
            onCurrentViewNoVisible()
        }
    }


    protected fun <T : View> findViewById(@IdRes resId: Int): T? {
        return rootView?.findViewById(resId)
    }

    /**
     * 当前Fragment不可见时的操作
     */
    protected fun onCurrentViewNoVisible() {

    }

    /**
     * 当前Fragment可见时的操作
     */
    protected fun onCurrentViewVisible() {
        if (!isPrepared || !mCurrentViewIsVisible || isHasLoadedOnce) {
            return
        }
        loaderData()
    }

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected fun inflateView(layoutResID: Int): View {
        return LayoutInflater.from(activity).inflate(layoutResID, null)
    }

    override fun getViewContext(): Context {
        return context!!
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

    /**
     * 根据指定的布局文件ID获取视图
     */
    protected fun inflateView(layoutResID: Int, root: ViewGroup): View {
        return LayoutInflater.from(activity).inflate(layoutResID, root, false)
    }

    override fun toActivity(cls: Class<out Activity>) {
        toActivity(cls, null)
    }

    override fun toActivity(cls: Class<out Activity>, bundle: Bundle?) {
        activity?.let {
            toActivity(it, cls, bundle)
        }
    }

    protected fun toActivity(context: Context, cls: Class<*>, bundle: Bundle?) {
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

    protected fun setHasLoadedOnce(hasLoadedOnce: Boolean){
        this.isHasLoadedOnce = hasLoadedOnce
    }

    /**
     * 初始化控件
     */
    protected abstract fun initViews(v: View)

    /**
     * 初始化监听
     */
    protected abstract fun initListener(v: View)

    /**
     * 加载数据(数据加载完成后需要把mHasLoadedOnce设置为true)
     */
    protected abstract fun loaderData()

    companion object {

        protected val ACTIVITY_BUNDLE = "ACTIVITY_BUNDLE"
    }


}

