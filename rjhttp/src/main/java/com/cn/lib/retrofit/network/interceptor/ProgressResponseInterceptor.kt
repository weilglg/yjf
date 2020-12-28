package com.cn.lib.retrofit.network.interceptor


import com.cn.lib.retrofit.network.body.ProgressResponseBody
import com.cn.lib.retrofit.network.callback.ResultProgressCallback

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by admin on 2017/9/5.
 */

class ProgressResponseInterceptor(private val progressListener: ResultProgressCallback<*>, private val tag: Any) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //拦截
        val originalResponse = chain.proceed(chain.request())

        //包装响应体并返回
        return originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body()!!, progressListener, tag))
                .build()
    }
}
