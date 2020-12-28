package com.cn.lib.retrofit.network.request

import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.proxy.ResultCallbackProxy
import com.cn.lib.retrofit.network.proxy.ResultClazzCallProxy
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import com.cn.lib.retrofit.network.func.ApiResultFunc
import com.cn.lib.retrofit.network.func.RetryExceptionFunc
import com.cn.lib.retrofit.network.subscriber.ResultCallbackSubscriber
import com.cn.lib.retrofit.network.util.RxUtil
import com.cn.lib.retrofit.network.util.Util
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

open class ApiResultPutRequest(url: String) : HttpBodyRequest<ApiResultPutRequest>(url) {

    open fun <T> execute(clazz: Class<T>): Observable<Optional<T>> {
        return execute(object : ResultClazzCallProxy<ApiResultEntity<T>, T>(clazz) {})
    }

    open fun <T> execute(type: Type): Observable<Optional<T>> {
        return execute(object : ResultClazzCallProxy<ApiResultEntity<T>, T>(type) {})
    }

    fun <T> execute(proxy: ResultClazzCallProxy<out ApiResultEntity<T>, T>): Observable<Optional<T>> {
        return build().generateRequest()
                .map(ApiResultFunc<T>(proxy.getType()))
                .compose<Optional<T>>(if (isSyncRequest) RxUtil._io_main_result() else RxUtil._main_result())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }

    open fun <T> execute(tag: Any, callback: ResultCallback<T>): Disposable {
        return execute(tag, ResultCallbackProxy.NEW_DEFAULT_INSTANCE(callback))
    }

    fun <T> execute(tag: Any, proxy: ResultCallbackProxy<out ApiResultEntity<T>, T>): Disposable {
        val observable = build().generateObservable(generateRequest(), proxy)
        return observable.subscribeWith(ResultCallbackSubscriber(tag, proxy.callback))
    }

    private fun <T> generateObservable(observable: Observable<ResponseBody>, proxy: ResultCallbackProxy<out ApiResultEntity<T>, T>): Observable<Optional<T>> {
        return observable
                .map(ApiResultFunc<T>(proxy.getType()))
                .compose<Optional<T>>(if (isSyncRequest) RxUtil._io_main_result() else RxUtil._main_result())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }

    override fun generateRequest(): Observable<ResponseBody> {
        mRequestBody?.let {//自定义的请求体
            return mApiManager.putBody(mUrl, it)
        }
        mJsonStr?.let {//Json字符串
            return mApiManager.putJson(mUrl, Util.createJson(it))
        }
        mJsonObj?.let {//Json对象
            return mApiManager.putJson(mUrl, Util.createJson(it.toJSONString()))
        }
        mJsonArr?.let {//Json数组
            return mApiManager.putJson(mUrl, Util.createJson(it.toJSONString()))
        }
        mStr?.let { //文本内容
            val requestBody = RequestBody.create(mMediaType, it)
            return mApiManager.putBody(mUrl, requestBody)
        }
        mObject?.let {//自定义的请求object
            return mApiManager.putBody(mUrl, it)
        }
        if (!mHttpParams.isParamsEmpty() && mHttpParams.isFilesEmpty()) {
            return mApiManager.put(mUrl, mHttpParams.paramMap)
        }
       return mApiManager.putUrl(mUrl)
    }

}