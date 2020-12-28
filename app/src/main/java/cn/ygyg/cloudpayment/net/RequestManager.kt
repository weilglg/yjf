package cn.ygyg.cloudpayment.net

import cn.ygyg.cloudpayment.net.request.ResultDeleteRequest
import cn.ygyg.cloudpayment.net.request.ResultGetRequest
import cn.ygyg.cloudpayment.net.request.ResultPostRequest
import cn.ygyg.cloudpayment.net.request.ResultPutRequest
import cn.ygyg.cloudpayment.app.Constants.IntentKey.TOKEN_KEY
import cn.ygyg.cloudpayment.utils.UserUtil

/**
 * 请求管理类
 * Created by Admin on 2019/4.
 */
object RequestManager {
    fun post(url: String): ResultPostRequest {
        return ResultPostRequest(url).addHeader(TOKEN_KEY, UserUtil.getToken()) as ResultPostRequest
    }

    fun delete(url: String): ResultDeleteRequest {
        return ResultDeleteRequest(url).addHeader(TOKEN_KEY, UserUtil.getToken()) as ResultDeleteRequest
    }

    fun put(url: String): ResultPutRequest {
        return ResultPutRequest(url).addHeader(TOKEN_KEY, UserUtil.getToken()) as ResultPutRequest
    }

    fun get(url: String): ResultGetRequest {
        return ResultGetRequest(url).addHeader(TOKEN_KEY, UserUtil.getToken()) as ResultGetRequest
    }
}