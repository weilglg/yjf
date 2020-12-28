package com.cn.lib.retrofit.network.transformer

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.Feature
import com.cn.lib.retrofit.network.callback.ResponseClazzCallback
import com.cn.lib.retrofit.network.config.Optional

import java.lang.reflect.Type

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody

/**
 * 返回结果解析器，在assets文件夹下的result-config.json中定义过键值对
 */
class HandleClazzBodyTransformer<T>(private val type: Type, private val callback: ResponseClazzCallback<T>?) : ObservableTransformer<ResponseBody, Optional<T>> {

    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<Optional<T>> {
        return upstream.map { body ->
            Optional(
                    if (callback != null) {
                        val jsonStr = callback.onTransformationResponse(body)
                        JSON.parseObject(jsonStr, type, Feature.UseBigDecimal)
                    } else {
                        JSON.parseObject<T>(body.string(), type, Feature.UseBigDecimal)
                    })
        }
    }

}
