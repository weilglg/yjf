package com.cn.lib.retrofit.network.subscriber

import android.content.Context
import com.cn.lib.retrofit.network.callback.ResponseCallback
import com.cn.lib.retrofit.network.callback.ResponseClazzCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.exception.ApiThrowable

class RxClazzCallbackSubscriber<T> : BaseSubscriber<Optional<T>> {

    private var mCallback: ResponseClazzCallback<T>? = null
    private var mTag: Any? = null

    constructor(tag: Any, callback: ResponseClazzCallback<T>) {
        this.mCallback = callback
        this.mTag = tag
    }

    constructor(context: Context, tag: Any, callback: ResponseClazzCallback<T>) : super(context) {
        this.mCallback = callback
        this.mTag = tag
    }

    override fun onStart() {
        super.onStart()
        mCallback?.run {
            onStart(mTag)
        }
    }

    override fun onNext(result: Optional<T>) {
        mCallback?.run {
            onSuccess(mTag, result.getIncludeNull())
        }
    }

    override fun onComplete() {
        mCallback?.run {
            onCompleted(mTag)
        }
    }

    override fun onError(throwable: ApiThrowable) {
        mCallback?.run {
            onError(mTag, throwable)
        }
    }
}