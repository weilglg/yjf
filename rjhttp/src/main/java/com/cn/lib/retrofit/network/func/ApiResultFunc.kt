package com.cn.lib.retrofit.network.func

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.parser.Feature
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class ApiResultFunc<T>(private val type: Type) : Function<ResponseBody, ApiResultEntity<T>> {

    @Throws(Exception::class)
    override fun apply(body: ResponseBody): ApiResultEntity<T> {
        var apiResult = ApiResultEntity<T>()
        apiResult.code = "-1"
        body.use {
            val jsonStr = body.string()
            val subClazz = (type as ParameterizedType).rawType as Class<*>
            if (ApiResultEntity::class.java.isAssignableFrom(subClazz)) {
                apiResult = JSON.parseObject(jsonStr, type, Feature.UseBigDecimal)
            } else {
                apiResult.code = "-1"
                apiResult.msg = "ApiResultEntity.class.isAssignableFrom(subClazz) err!!"
            }
        }
        if (apiResult.code == "-1") {
            throw JSONException(apiResult.msg)
        }
        return apiResult
    }
}
