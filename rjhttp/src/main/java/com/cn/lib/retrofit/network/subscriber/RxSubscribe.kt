package com.cn.lib.retrofit.network.subscriber

import com.cn.lib.retrofit.network.callback.ResponseCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.exception.ExceptionFactory

import okhttp3.ResponseBody

class RxSubscribe<T>(private val callback: ResponseCallback<T>?, tag: Any) : BaseSubscriber<ResponseBody>() {
    private var tag: Any? = null

    init {
        this.tag = tag
    }

    override fun onStart() {
        super.onStart()
        callback?.onStart(tag)
    }

    override fun onComplete() {
        super.onComplete()
        callback?.onCompleted(tag)
    }

    override fun onNext(responseBody: ResponseBody) {
        try {
            callback?.onSuccess(tag, callback.onTransformationResponse(responseBody))
        } catch (e: Exception) {
            e.printStackTrace()
            callback?.onError(tag, ExceptionFactory.handleException(e))
        }

    }

    override fun onError(throwable: ApiThrowable) {
        callback?.onError(tag, throwable)
    }


}
