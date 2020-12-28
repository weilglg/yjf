package com.cn.lib.retrofit.network.subscriber

import android.content.Context

import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.exception.ExceptionFactory

import java.lang.ref.WeakReference

import io.reactivex.observers.DisposableObserver

abstract class BaseSubscriber<T> : DisposableObserver<T> {

     lateinit var contextWeakReference: WeakReference<Context>

    var context: Context
        get() = contextWeakReference.get()!!
        set(context) {
            this.contextWeakReference = WeakReference(context)
        }

    constructor() {}

    constructor(context: Context) {
        this.contextWeakReference = WeakReference(context)
    }

    override fun onStart() {
        LogUtil.v("RxHttp", "-->http is start")
    }

    override fun onNext(result: T) {
        LogUtil.v("RxHttp", "-->http is onNext")
    }

    override fun onError(e: Throwable) {
        if (e.message != null) {
            LogUtil.v("RxHttp", e.message)
        } else {
            LogUtil.v("RxHttp", "Throwable  || Message == Null")
        }
        if (e is ApiThrowable) {
            LogUtil.e("RxHttp", "--> e is ApiException err:" + e)
            onError(e)
        } else {
            LogUtil.e("RxHttp", "e !is ApiException err:" + e)
            onError(ExceptionFactory.handleException(e))
        }
        onComplete()
    }

    override fun onComplete() {
        LogUtil.v("RxHttp", "-->http is Complete")
    }

    abstract fun onError(throwable: ApiThrowable)


}
