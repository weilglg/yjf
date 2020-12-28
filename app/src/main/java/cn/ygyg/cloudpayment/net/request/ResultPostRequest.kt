package cn.ygyg.cloudpayment.net.request

import cn.ygyg.cloudpayment.net.BaseApiResultEntity
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.proxy.ResultCallbackProxy
import com.cn.lib.retrofit.network.proxy.ResultClazzCallProxy
import com.cn.lib.retrofit.network.request.ApiResultPostRequest
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.lang.reflect.Type

/**
 * 根据项目返回结果不同进行返回基类修改
 * Created by Admin on 2019/4/18.
 */
class ResultPostRequest(url: String) : ApiResultPostRequest(url) {

    override fun <T> execute(clazz: Class<T>): Observable<Optional<T>> {
        return super.execute(object : ResultClazzCallProxy<BaseApiResultEntity<T>, T>(clazz) {})
    }

    override fun <T> execute(type: Type): Observable<Optional<T>> {
        return execute(object : ResultClazzCallProxy<BaseApiResultEntity<T>, T>(type) {})
    }

    override fun <T> execute(tag: Any, callback: ResultCallback<T>): Disposable {
        return super.execute(tag, object : ResultCallbackProxy<BaseApiResultEntity<T>, T>(callback) {})
    }

}