package com.cn.lib.retrofit.network.func

import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.exception.ExceptionFactory
import com.cn.lib.retrofit.network.util.LogUtil

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function

class RetryExceptionFunc : Function<Observable<out Throwable>, ObservableSource<*>> {
    /* retry次数*/
    private var count = 0
    /*延迟*/
    private var delay: Long = 500
    /*叠加延迟*/
    private var increaseDelay: Long = 3000

    constructor() {

    }

    constructor(count: Int, delay: Long) {
        this.count = count
        this.delay = delay
    }

    constructor(count: Int, delay: Long, increaseDelay: Long) {
        this.count = count
        this.delay = delay
        this.increaseDelay = increaseDelay
    }

    @Throws(Exception::class)
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable.zipWith(Observable.range(1, count + 1), BiFunction<Throwable, Int, Wrapper> { throwable, integer -> Wrapper(throwable, integer) }).flatMap { wrapper ->
            if (wrapper.index > 1)
                LogUtil.i("重试次数：" + wrapper.index)
            var errCode = ""
            val throwable = wrapper.throwable
            if (throwable is ApiThrowable) {
                errCode = throwable.code
            }
            if ((wrapper.throwable is ConnectException || wrapper.throwable is SocketTimeoutException
                    || errCode == ExceptionFactory.ERROR.NETWORD_ERROR || errCode == ExceptionFactory.ERROR.TIMEOUT_ERROR
                    || wrapper.throwable is TimeoutException) && wrapper.index < count + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS)

            } else Observable.error(wrapper.throwable)
        }
    }

    private data class Wrapper(var throwable: Throwable, var index: Int)

}
