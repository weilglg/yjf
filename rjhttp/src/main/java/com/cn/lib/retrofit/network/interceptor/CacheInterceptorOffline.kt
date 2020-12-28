package com.cn.lib.retrofit.network.interceptor

import android.content.Context
import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.util.NetUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class CacheInterceptorOffline : CacheInterceptor {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, cacheControlValue: String) : super(context, cacheControlValue) {}

    constructor(context: Context, cacheControlValue: String, cacheOnlineControlValue: String) : super(context, cacheControlValue, cacheOnlineControlValue) {}

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        LogUtil.d("RxHttp", "request url :" + request.url().url())
        LogUtil.d("RxHttp", "request tag :" + request.tag().toString())
        LogUtil.d("RxHttp", "request header :" + request.headers().toString())
        if (!NetUtil.isNetworkAvailable(context)) {
            LogUtil.d("RxHttp", " no network load cache:" + request.cacheControl().toString())
            /* request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "only-if-cached, " + cacheControlValue_Offline)
                    .build();*/

            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()
            val response = chain.proceed(request)
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, $cacheControlValue_Offline")
                    .build()
        }
        return chain.proceed(request)
    }
}
