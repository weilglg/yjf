package com.cn.lib.basic

import android.util.SparseArray

import com.alibaba.fastjson.JSONObject

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

/**
 * Created by admin on 2017/4/17.
 */

open class BasePresenterImpl<V : IBaseView>(view: V) : IBasePresenter<V> {

    protected var mvpView: V? = null

    // 事件管理类
    private val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val disposableMap = SparseArray<DisposableObserver<*>>()

    /**
     * 获得请求入参JsonObject
     */
    protected val requestParams: JSONObject = JSONObject()
        get() {
            field.clear()
            return field
        }

    init {
        attachView(view)
    }

    override fun attachView(view: V) {
        if (view != this.mvpView) {
            this.mvpView = view
        }
    }

    override fun detachView() {
        this.mvpView = null
        unSubscribe()
    }

    //RXjava取消注册，以避免内存泄露
    private fun unSubscribe() {
        mCompositeDisposable.clear()
    }

    protected fun addSubscribe(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }


}
