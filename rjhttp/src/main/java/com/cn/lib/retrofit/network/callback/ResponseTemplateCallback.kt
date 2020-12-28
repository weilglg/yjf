package com.cn.lib.retrofit.network.callback

import android.text.TextUtils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.parser.Feature
import com.alibaba.fastjson.parser.ParserConfig
import com.alibaba.fastjson.serializer.CollectionCodec
import com.cn.lib.retrofit.network.RxHttp
import com.cn.lib.retrofit.network.config.ResultConfigLoader
import com.cn.lib.retrofit.network.exception.ServerException

import java.lang.reflect.ParameterizedType

import okhttp3.ResponseBody

/**
 * 返回的数据格式是统一的并且只需要处理需要的数据
 * 例如：{"code":"0",
 * "msg":"成功",
 * "data":{
 * "name"："xxxx"
 * }
 * }
 */

abstract class ResponseTemplateCallback<T> protected constructor() : ResponseCallback<T>() {

    init {
        ResultConfigLoader.init(RxHttp.INSTANCE.getContext()!!)
    }

    @Throws(Exception::class)
    override fun onTransformationResponse(body: ResponseBody): T? {
        val jsonStr = body.string()
        if (TextUtils.isEmpty(jsonStr)) throw NullPointerException("body is null")
        val jsonObject = JSON.parseObject(jsonStr)
        val code = getCode(jsonObject)
        val msg = getMessage(jsonObject)
        val dataStr = getDataStr(jsonObject)
        val isSuccess = checkSuccessCode(code, msg)
        if (isSuccess) {
            dataStr?.let { jsonStr ->
                val genType = javaClass.genericSuperclass
                if (genType is ParameterizedType) {
                    val params = genType.actualTypeArguments
                    if (params.isNotEmpty()) {
                        val paramType = params[0]
                        if (paramType is Class<*> && String::class.java.isAssignableFrom(paramType)) {
                            return jsonStr as T
                        } else {
                            val deserializer = ParserConfig.getGlobalInstance().getDeserializer(paramType)
                            return if (deserializer is CollectionCodec && jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
                                val pageJson = JSON.parseObject(jsonStr)
                                val pageDataStr = getDataStr(pageJson)
                                if ("[]" == pageDataStr || pageDataStr == null || "" == pageDataStr) {
                                    throw NullPointerException("result data is null")
                                }
                                JSON.parseObject<T>(pageDataStr, paramType, Feature.UseBigDecimal)
                            } else {
                                JSON.parseObject<T>(jsonStr, paramType, Feature.UseBigDecimal)
                            }
                        }
                    }
                }
            }
            return null
        }
        throw ServerException(code, msg)
    }

    open fun checkSuccessCode(code: String, msg: String): Boolean {
        return ResultConfigLoader.checkSuccess(code = code)
    }

    private fun getCode(jsonObject: JSONObject): String {
        val codeKey = ResultConfigLoader.codeKey
        var code = "-1"
        if (jsonObject.containsKey(codeKey)) {
            code = jsonObject.getString(codeKey)
        }
        return code
    }

    private fun getMessage(jsonObject: JSONObject): String {
        val msgKey = ResultConfigLoader.msgKey
        return if (jsonObject.containsKey(msgKey)) {
            jsonObject.getString(msgKey)
        } else ""
    }

    private fun getDataStr(jsonObject: JSONObject): String? {
        val dataKey = ResultConfigLoader.dataKey
        dataKey?.let {
            for (key in it) {
                if (jsonObject.containsKey(key)) {
                    return jsonObject.getString(key)
                }
            }
        }
        return ""
    }

}
