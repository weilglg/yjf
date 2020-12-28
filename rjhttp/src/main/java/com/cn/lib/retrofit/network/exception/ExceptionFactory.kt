package com.cn.lib.retrofit.network.exception

import android.net.ParseException
import android.text.TextUtils
import android.util.Log

import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.config.ResultConfigLoader

import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertPathValidatorException

import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

import retrofit2.HttpException

class ExceptionFactory {


    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        const val UNKNOWN = "1000"
        /**
         * mTabs
         */
        const val PARSE_ERROR = "1001"
        /**
         * 网络错误
         */
        const val NETWORD_ERROR = "1002"
        /**
         * 协议出错
         */
        const val HTTP_ERROR = "1003"

        /**
         * 证书出错
         */
        const val SSL_ERROR = "1005"

        /**
         * 连接超时
         */
        const val TIMEOUT_ERROR = "1006"

        /**
         * 证书未找到
         */
        const val SSL_NOT_FOUND = "1007"

        /**
         * 出现空值
         */
        const val NULL = "-100"

        /**
         * 格式错误
         */
        const val FORMAT_ERROR = "1008"
    }

    companion object {

        private const val UNAUTHORIZED = "401"
        private const val FORBIDDEN = "403"
        private const val NOT_FOUND = "404"
        private const val REQUEST_TIMEOUT = "408"
        private const val INTERNAL_SERVER_ERROR = "500"
        private const val BAD_GATEWAY = "502"
        private const val SERVICE_UNAVAILABLE = "503"
        private const val GATEWAY_TIMEOUT = "504"
        private const val ACCESS_DENIED = "302"
        private const val HANDEL_ERRROR = "417"

        fun handleException(e: Throwable): ApiThrowable {
            e.cause?.let {
                LogUtil.e("ExceptionFactory", it.message)
            }
            val ex: ApiThrowable
            if (e is ApiThrowable) {
                ex = e
                return ex
            } else if (e !is ServerException && e is HttpException) {
                ex = ApiThrowable(e, e.code().toString())
                when (ex.code) {
                    UNAUTHORIZED -> ex.message = null
//                    UNAUTHORIZED -> ex.message = "未授权的请求"
                    FORBIDDEN -> ex.message = "禁止访问"
                    NOT_FOUND -> ex.message = "服务器地址未找到"
                    REQUEST_TIMEOUT -> ex.message = "请求超时"
                    GATEWAY_TIMEOUT -> ex.message = "网关响应超时"
                    INTERNAL_SERVER_ERROR -> ex.message = "服务器出错"
                    BAD_GATEWAY -> ex.message = "无效的请求"
                    SERVICE_UNAVAILABLE -> ex.message = "服务器不可用"
                    ACCESS_DENIED -> ex.message = "网络错误"
                    HANDEL_ERRROR -> ex.message = "接口处理失败"
                    else -> {
                        if (!TextUtils.isEmpty(ex.message)) {
                            ex.message = e.message
                        } else if (TextUtils.isEmpty(ex.message) && e.getLocalizedMessage() != null) {
                            ex.message = e.getLocalizedMessage()
                        } else {
                            ex.message = "未知错误"
                        }
                    }
                }
                return ex
            } else if (e is ServerException) {
                ex = ApiThrowable(e, e.code)
                val errorMsg = ResultConfigLoader.errorDesc(e.code)
                if ("" != errorMsg) {
                    ex.message = errorMsg
                    return ex
                }
                ex.message = e.message
                return ex
            } else if (e is com.alibaba.fastjson.JSONException
                    || e is JSONException
                    || e is ParseException) {
                Log.e("TAG", "===>>>>>>" + e.printStackTrace())
                Log.e("TAG", "===>>>>>>" + e.localizedMessage)
                Log.e("TAG", "===>>>>>>" + e.fillInStackTrace())
                Log.e("TAG", "===>>>>>>" + e.message)
                Log.e("TAG", "===>>>>>>" + e.toString())
                ex = ApiThrowable(e, ERROR.PARSE_ERROR)
                ex.message = "解析错误"
                return ex
            } else if (e is ConnectException) {
                ex = ApiThrowable(e, ERROR.NETWORD_ERROR)
                ex.message = "网络异常，请检查网络"
                return ex
            } else if (e is SSLHandshakeException) {
                ex = ApiThrowable(e, ERROR.SSL_ERROR)
                ex.message = "证书验证失败"
                return ex
            } else if (e is CertPathValidatorException) {
                LogUtil.e("ExceptionFactory", e.message)
                ex = ApiThrowable(e, ERROR.SSL_NOT_FOUND)
                ex.message = "证书路径没找到"

                return ex
            } else if (e is SSLPeerUnverifiedException) {
                LogUtil.e("ExceptionFactory", e.message)
                ex = ApiThrowable(e, ERROR.SSL_NOT_FOUND)
                ex.message = "无有效的SSL证书"
                return ex

            } else if (e is ConnectTimeoutException) {
                ex = ApiThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = "连接超时"
                return ex
            } else if (e is SocketTimeoutException) {
                ex = ApiThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = "连接超时"
                return ex
            } else if (e is ClassCastException) {
                ex = ApiThrowable(e, ERROR.FORMAT_ERROR)
                ex.message = "类型转换出错"
                return ex
            } else if (e is NullPointerException) {
                ex = ApiThrowable(e, ERROR.NULL)
                ex.message = "数据有空"
                return ex
            } else if (e is FormatException) {
                ex = ApiThrowable(e, e.code)
                ex.message = e.message
                return ex
            } else if (e is UnknownHostException) {
                LogUtil.e("ExceptionFactory", e.message)
                ex = ApiThrowable(e, NOT_FOUND)
                ex.message = "服务器地址未找到,请检查网络或Url"
                return ex
            } else {
                LogUtil.e("ExceptionFactory", e.message)
                ex = ApiThrowable(e, ERROR.UNKNOWN)
                ex.message = e.message
                return ex
            }
        }
    }
}
