package com.cn.lib.retrofit.network.interceptor

import com.cn.lib.retrofit.network.entity.HttpParamEntity

/**
 * 请求参数拦截器，可以对参数进行加密
 */
interface RequestParamInterceptor {
    fun intercept(params: HttpParamEntity): HttpParamEntity
}