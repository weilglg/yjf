package com.cn.lib.retrofit.network.callback

import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.util.Util

import java.lang.reflect.Type

/**
 * 回调函数
 *
 * @param <T> 真是需要的返回结果类型
</T> */
abstract class ResultCallback<T> : IResultType<T> {

    override fun getType(): Type {
        return Util.findNeedClass(javaClass)
    }

    /**
     * 获取需要解析的泛型T raw类型
     */
    fun getRawType(): Type {
        return Util.findRawType(javaClass)
    }

    abstract fun onStart(tag: Any?)

    abstract fun onCompleted(tag: Any?)

    abstract fun onError(tag: Any?, e: ApiThrowable)

    abstract fun onSuccess(tag: Any?, result: T?)
}
