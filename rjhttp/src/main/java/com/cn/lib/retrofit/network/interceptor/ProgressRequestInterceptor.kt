package com.cn.lib.retrofit.network.interceptor


import com.cn.lib.retrofit.network.body.ProgressRequestBody
import com.cn.lib.retrofit.network.callback.ResultProgressCallback

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by admin on 2017/9/5.
 */

class ProgressRequestInterceptor(private var tag: Any?, private var progressListener: ResultProgressCallback<*>?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
                .method(original.method(), ProgressRequestBody(original.body()!!, progressListener, tag!!))
                .build()
        return chain.proceed(request)
    }
}
