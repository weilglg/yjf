package com.cn.lib.retrofit.network.callback

import android.text.TextUtils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.parser.Feature

import java.lang.reflect.ParameterizedType

import okhttp3.ResponseBody

/**
 * 无模板解析，对返回的数据不进行其他处理
 */
abstract class ResponseGenericsCallback<T> : ResponseCallback<T>() {


    @Throws(Exception::class)
    override fun onTransformationResponse(body: ResponseBody): T {
        try {
            val jsonStr = body.string()
            if (TextUtils.isEmpty(jsonStr)) throw NullPointerException("body is null")
            val genType = javaClass.genericSuperclass
            if (genType is ParameterizedType) {
                val params = genType.actualTypeArguments
                if (params.isNotEmpty()) {
                    val paramType = params[0]
                    return JSON.parseObject(jsonStr, paramType, Feature.UseBigDecimal)
                }
            }
        } finally {
            body.close()
        }
        throw JSONException("泛型解析异常")
    }
}
