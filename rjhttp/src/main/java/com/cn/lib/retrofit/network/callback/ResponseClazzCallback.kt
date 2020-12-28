package com.cn.lib.retrofit.network.callback

import android.text.TextUtils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.cn.lib.retrofit.network.config.ResultConfigLoader
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.exception.ServerException

import okhttp3.ResponseBody

abstract class ResponseClazzCallback<in D> : IResponseCallback<String> {

    @Throws(Exception::class)
    override fun onTransformationResponse(body: ResponseBody): String {
        val jsonStr = body.string()
        if (TextUtils.isEmpty(jsonStr)) throw NullPointerException("body is null")
        val jsonObject = JSON.parseObject(jsonStr)
        val code = getCode(jsonObject)
        val msg = getMessage(jsonObject)
        val dataStr = getDataStr(jsonObject)
        if (checkSuccess(code)) {
            return dataStr
        }
        throw ServerException(code, msg)
    }

    internal abstract fun checkSuccess(code: String): Boolean
    /**
     * 请求开始
     */
    open fun onStart(tag: Any?) {

    }

    /**
     * 请求结束
     */
    open fun onCompleted(tag: Any?) {

    }

    abstract fun onError(tag: Any?, throwable: ApiThrowable)

    abstract fun onSuccess(tag: Any?, result: D?)

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

    private fun getDataStr(jsonObject: JSONObject): String {
        val dataKey = ResultConfigLoader.dataKey
        for (key in dataKey!!) {
            if (jsonObject.containsKey(key)) {
                return jsonObject.getString(key)
            }
        }
        return ""
    }
}
