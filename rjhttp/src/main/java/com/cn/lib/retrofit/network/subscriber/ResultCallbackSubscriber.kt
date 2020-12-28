package com.cn.lib.retrofit.network.subscriber

import android.content.Context

import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.exception.ApiThrowable


class ResultCallbackSubscriber<T> : BaseSubscriber<Optional<T>> {

    private var mCallback: ResultCallback<T>? = null
    private var mTag: Any? = null

    constructor(tag: Any, callback: ResultCallback<T>?) {
        this.mCallback = callback
        this.mTag = tag
    }

    constructor(context: Context, tag: Any, callback: ResultCallback<T>) : super(context) {
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
        super.onNext(result)
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
