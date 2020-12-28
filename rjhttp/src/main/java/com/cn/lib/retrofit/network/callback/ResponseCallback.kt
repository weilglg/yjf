package com.cn.lib.retrofit.network.callback

import com.cn.lib.retrofit.network.exception.ApiThrowable

abstract class ResponseCallback<T> : IResponseCallback<T> {

    /**
     * 请求开始
     */
    open fun onStart(tag: Any?) {

    }

    /**
     * 请求结束
     */
    open fun onCompleted(tag: Any?) {

    }

    /**
     * 下载进度
     */
    protected fun onProgress(tag: Any?, progress: Float, transfer: Long, total: Long) {

    }

    protected fun onProgress(tag: Any?, progress: Float, speed: Long, transfer: Long, total: Long) {

    }

    abstract fun onError(tag: Any?, throwable: ApiThrowable)

    abstract fun onSuccess(tag: Any?, result: T?)


}
