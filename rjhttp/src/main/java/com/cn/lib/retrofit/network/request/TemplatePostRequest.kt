package com.cn.lib.retrofit.network.request

import com.cn.lib.retrofit.network.callback.ResponseCallback
import com.cn.lib.retrofit.network.callback.ResponseClazzCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.func.RetryExceptionFunc
import com.cn.lib.retrofit.network.subscriber.RxCallbackSubscriber
import com.cn.lib.retrofit.network.subscriber.RxClazzCallbackSubscriber
import com.cn.lib.retrofit.network.transformer.HandleClazzBodyTransformer
import com.cn.lib.retrofit.network.transformer.HandleErrorTransformer
import com.cn.lib.retrofit.network.transformer.HandleResponseBodyTransformer
import com.cn.lib.retrofit.network.util.RxUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.lang.reflect.Type

class TemplatePostRequest(url: String) : HttpBodyRequest<TemplatePostRequest>(url) {

    fun <T> execute(type: Type): Observable<Optional<T>> {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main<ResponseBody>() else RxUtil._main())
                .compose(HandleClazzBodyTransformer<T>(type, null))
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }

    fun <T> execute(clazz: Class<T>): Observable<Optional<T>> {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleClazzBodyTransformer<T>(clazz, null))
                .compose(HandleErrorTransformer())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }

    fun <T> execute(tag: Any, type: Type, callback: ResponseClazzCallback<T>): Disposable {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleClazzBodyTransformer(type, callback))
                .compose(HandleErrorTransformer<Optional<T>>())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
                .subscribeWith(RxClazzCallbackSubscriber(mContext, tag, callback))
    }

    fun <T> execute(tag: Any, clazz: Class<T>, callback: ResponseClazzCallback<T>): Disposable {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleClazzBodyTransformer(type = clazz, callback = callback))
                .compose(HandleErrorTransformer())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
                .subscribeWith(RxClazzCallbackSubscriber(mContext, tag, callback))

    }

    fun <T> execute(tag: Any, callback: ResponseCallback<T>): Disposable {
        val observable = build().generateObservable(generateRequest())
        return observable.compose(HandleResponseBodyTransformer(callback))
                .compose(HandleErrorTransformer())
                .subscribeWith(RxCallbackSubscriber(mContext, tag, callback))
    }

    private fun generateObservable(observable: Observable<ResponseBody>): Observable<ResponseBody> {
        return observable.compose(if (isSyncRequest) RxUtil._io_main<ResponseBody>() else RxUtil._main())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }


}
