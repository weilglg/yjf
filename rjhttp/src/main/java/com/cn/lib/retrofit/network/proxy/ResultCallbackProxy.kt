package com.cn.lib.retrofit.network.proxy

import com.cn.lib.retrofit.network.callback.IResultType
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import com.cn.lib.retrofit.network.util.Util
import com.retrofit.network.util.TypesUtil

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import okhttp3.ResponseBody


/**
 * 回调代理类，实现泛型的解析以及组装（定义为抽象类是因为方便获取泛型）
 *
 * @param <T> [ApiResultEntity]的子类及返回结果的统一基类
 * @param <R> 真正的返回结果类型
</R></T> */
abstract class ResultCallbackProxy<T : ApiResultEntity<R>, R>(mCallback: ResultCallback<R>) : IResultType<T> {

    var callback: ResultCallback<R>? = null
        internal set

    init {
        this.callback = mCallback
    }

    //如果用户的信息是返回List需单独处理
    override fun getType(): Type {
        var typeArguments: Type? = null
        callback?.run {
            val rawType = getRawType()
            typeArguments = if (List::class.java.isAssignableFrom(Util.getClass(rawType, 0)) || Map::class.java.isAssignableFrom(Util.getClass(rawType, 0))) {
                getRawType()
            } else {
                val type = getRawType()
                Util.getClass(type, 0)
            }
        }
        if (typeArguments == null) {
            typeArguments = ResponseBody::class.java
        }
        var rawType = Util.findNeedType(javaClass)
        if (rawType is ParameterizedType) {
            rawType = rawType.rawType
        }

        return TypesUtil.newParameterizedTypeWithOwner(null, rawType = rawType, typeArguments = *arrayOf(typeArguments))
    }

    companion object {

        /**
         * 返回结果为ApiResultEntity<T>格式的回调代理类
        </T> */
        fun <T> NEW_DEFAULT_INSTANCE(callback: ResultCallback<T>): ResultCallbackProxy<ApiResultEntity<T>, T> {
            return object : ResultCallbackProxy<ApiResultEntity<T>, T>(callback) {}
        }
    }

}
