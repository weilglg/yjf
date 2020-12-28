package com.cn.lib.retrofit.network.util

import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import com.cn.lib.retrofit.network.func.HandleResultFunc
import com.cn.lib.retrofit.network.transformer.HandleErrorTransformer

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object RxUtil {

    fun <T> io_main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe { disposable -> LogUtil.i("+++doOnSubscribe+++" + disposable.isDisposed) }
                    .doFinally { LogUtil.i("+++doFinally+++") }
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> _io_main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { disposable -> LogUtil.i("+++doOnSubscribe+++" + disposable.isDisposed) }
                    .doFinally { LogUtil.i("+++doFinally+++") }
        }
    }

    fun <T> _io_main_result(): ObservableTransformer<ApiResultEntity<T>, Optional<T>> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(HandleResultFunc())
                    .compose(HandleErrorTransformer())
                    .doOnSubscribe { disposable -> LogUtil.i("+++doOnSubscribe+++" + disposable.isDisposed) }
                    .doFinally { LogUtil.i("+++doFinally+++") }
        }
    }


    fun <T> _main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.doOnSubscribe { disposable -> LogUtil.i("+++doOnSubscribe+++" + disposable.isDisposed) }
                    .doFinally { LogUtil.i("+++doFinally+++") }
        }
    }

    fun <T> _main_result(): ObservableTransformer<ApiResultEntity<T>,  Optional<T>> {
        return ObservableTransformer { upstream ->
            upstream
                    .flatMap(HandleResultFunc())
                    .compose(HandleErrorTransformer())
                    .doOnSubscribe { disposable -> LogUtil.i("+++doOnSubscribe+++" + disposable.isDisposed) }
                    .doFinally { LogUtil.i("+++doFinally+++") }
        }
    }


}
