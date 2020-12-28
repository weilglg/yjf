package com.cn.lib.retrofit.network.callback

import okhttp3.ResponseBody

interface IResponseCallback<T> {

    @Throws(Exception::class)
    fun onTransformationResponse(body: ResponseBody): T?

}
