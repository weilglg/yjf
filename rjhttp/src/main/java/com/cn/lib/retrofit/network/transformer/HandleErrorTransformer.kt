package com.cn.lib.retrofit.network.transformer

import com.cn.lib.retrofit.network.func.HandleErrorFunc

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class HandleErrorTransformer<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.onErrorResumeNext(HandleErrorFunc())
    }
}
