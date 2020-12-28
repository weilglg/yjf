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
import com.cn.lib.retrofit.network.util.Util
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

class TemplateDeleteRequest(url: String) : HttpBodyRequest<TemplateDeleteRequest>(url) {

    fun <T> execute(type: Type): Observable<Optional<T>> {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main<ResponseBody>() else RxUtil._main())
                .compose(HandleErrorTransformer<ResponseBody>())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
                .compose(HandleClazzBodyTransformer(type, null))
    }

    fun <T> execute(clazz: Class<T>): Observable<Optional<T>> {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleErrorTransformer())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
                .compose(HandleClazzBodyTransformer(clazz, null))
    }

    fun <T> execute(tag: Any, type: Type, callback: ResponseClazzCallback<T>): Disposable {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleErrorTransformer())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
                .compose(HandleClazzBodyTransformer(type, callback))
                .subscribeWith(RxClazzCallbackSubscriber(mContext, tag, callback))
    }

    fun <T> execute(tag: Any, clazz: Class<T>, callback: ResponseClazzCallback<T>): Disposable? {
        return build().generateRequest()
                .compose<ResponseBody>(if (isSyncRequest) RxUtil._io_main() else RxUtil._main())
                .compose(HandleErrorTransformer())
                .compose(HandleClazzBodyTransformer(type = clazz, callback = callback))
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

    override fun generateRequest(): Observable<ResponseBody> {
        mRequestBody?.let {//自定义的请求体
            return mApiManager.deleteBody(mUrl, it)
        }
        mJsonStr?.let {//Json字符串
            return mApiManager.deleteJson(mUrl, Util.createJson(it))
        }
        mJsonObj?.let {//Json对象
            return mApiManager.deleteBody(mUrl, Util.createJson(it.toJSONString()))
        }
        mJsonArr?.let {//Json数组
            return mApiManager.deleteBody(mUrl, Util.createJson(it.toJSONString()))
        }
        mStr?.let { //文本内容
            val requestBody = RequestBody.create(mMediaType, it)
            return mApiManager.deleteBody(mUrl, requestBody)
        }
        mObject?.let {//自定义的请求object
            return mApiManager.deleteBody(mUrl, it)
        }
        if (!mHttpParams.isParamsEmpty() && mHttpParams.isFilesEmpty()) {
            return mApiManager.delete(mUrl, mHttpParams.paramMap)
        }
        return mApiManager.deleteUrl(url = mUrl)
    }

}