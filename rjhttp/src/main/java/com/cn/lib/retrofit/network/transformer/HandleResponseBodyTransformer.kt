package com.cn.lib.retrofit.network.transformer

import com.cn.lib.retrofit.network.callback.ResponseCallback
import com.cn.lib.retrofit.network.config.Optional

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody

class HandleResponseBodyTransformer<T>(private val callback: ResponseCallback<T>) : ObservableTransformer<ResponseBody, Optional<T>> {

    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<Optional<T>> {
        return upstream.map { body -> Optional(callback.onTransformationResponse(body)) }
    }
}
