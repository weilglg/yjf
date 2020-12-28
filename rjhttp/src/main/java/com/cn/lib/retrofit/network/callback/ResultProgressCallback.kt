package com.cn.lib.retrofit.network.callback

import com.cn.lib.retrofit.network.exception.ApiThrowable

/**
 * 进度回调
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-12 16:19
 */
abstract class ResultProgressCallback<T> : ResultCallback<T>() {

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    abstract fun onUIProgressChanged(mTag: Any?, numBytes: Long, totalBytes: Long, percent: Float, speed: Float)

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    fun onUIProgressStart(mTag: Any?, totalBytes: Long) {

    }

    /**
     * 进度结束
     */
    fun onUIProgressFinish(mTag: Any?) {

    }

    override fun onStart(tag: Any?) {

    }

    override fun onSuccess(tag: Any?, t: T?) {

    }

    override fun onError(tag: Any?, e: ApiThrowable) {

    }
}
