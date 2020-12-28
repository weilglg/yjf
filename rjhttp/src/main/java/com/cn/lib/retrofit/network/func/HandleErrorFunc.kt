package com.cn.lib.retrofit.network.func


import com.cn.lib.retrofit.network.exception.ExceptionFactory

import io.reactivex.Observable
import io.reactivex.functions.Function

class HandleErrorFunc<T> : Function<Throwable, Observable<T>> {

    @Throws(Exception::class)
    override fun apply(throwable: Throwable): Observable<T> {
        return Observable.error(ExceptionFactory.handleException(throwable))
    }

}
