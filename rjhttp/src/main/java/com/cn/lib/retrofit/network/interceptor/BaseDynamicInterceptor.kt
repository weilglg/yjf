package com.cn.lib.retrofit.network.interceptor

import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.util.Util
import okhttp3.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/**
 * <p>描述：动态拦截器</p>
 * 主要功能是针对参数：<br>
 * 1.可以获取到全局公共参数和局部参数，统一进行签名sign<br>
 * 2.可以自定义动态添加参数，类似时间戳timestamp是动态变化的，token（登录了才有），参数签名等<br>
 * 3.参数值是经过UTF-8编码的<br>
 * 4.默认提供询问是否动态签名（签名需要自定义），动态添加时间戳等<br>
 */
abstract class BaseDynamicInterceptor<R : BaseDynamicInterceptor<R>> : Interceptor {
    private var httpUrl: HttpUrl? = null
    private var isSign = false //是否需要签名
    private var isTimeStamp = false //是否需要追加时间戳
    private var isAccessToken = false//是否需要添加token


    fun sign(sign: Boolean): R {
        isSign = sign
        return this as R
    }

    fun timeStamp(timeStamp: Boolean): R {
        this.isTimeStamp = timeStamp
        return this as R
    }

    fun accessToken(accessToken: Boolean): R {
        this.isAccessToken = accessToken
        return this as R
    }

    fun isTimeStamp(): Boolean {
        return isTimeStamp
    }

    fun isAccessToken(): Boolean {
        return isAccessToken
    }

    fun isSign(): Boolean {
        return isSign
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.method() == "GET") {
            this.httpUrl = HttpUrl.parse(parseUrl(request.url().url().toString()))
            request = addGetParamsSign(request)
        } else if (request.method() == "POST") {
            this.httpUrl = request.url()
            request = addPostParamsSign(request)
        }
        return chain.proceed(request)
    }

    //get 添加签名和公共动态参数
    @Throws(UnsupportedEncodingException::class)
    private fun addGetParamsSign(request: Request): Request {
        var newRequest = request
        var httpUrl = newRequest.url()
        val newBuilder = httpUrl.newBuilder()

        //获取原有的参数
        val nameSet = httpUrl.queryParameterNames()
        val nameList = ArrayList<String>()
        nameList.addAll(nameSet)
        val oldParams = TreeMap<String, String>()
        for (i in nameList.indices) {
            val value = if (httpUrl.queryParameterValues(nameList[i]) != null && httpUrl.queryParameterValues(nameList[i]).size > 0) httpUrl.queryParameterValues(nameList[i])[0] else ""
            oldParams[nameList[i]] = value
        }
        val nameKeys = listOf(nameList).toString()
        //拼装新的参数
        val newParams = dynamic(oldParams)
        Util.checkNotNull<Any>(newParams, "newParams==null")
        for ((key, value) in newParams) {
            val urlValue = URLEncoder.encode(value, "UTF-8")
            if (!nameKeys.contains(key)) {//避免重复添加
                newBuilder.addQueryParameter(key, urlValue)
            }
        }

        httpUrl = newBuilder.build()
        newRequest = newRequest.newBuilder().url(httpUrl).build()
        return newRequest
    }

    //templatePost 添加签名和公共动态参数
    @Throws(UnsupportedEncodingException::class)
    private fun addPostParamsSign(request: Request): Request {
        var newRequest = request
        if (newRequest.body() is FormBody) {
            val bodyBuilder = FormBody.Builder()
            var formBody: FormBody = newRequest.body() as FormBody

            //原有的参数
            val oldParams = TreeMap<String, String>()
            for (i in 0 until formBody.size()) {
                oldParams[formBody.encodedName(i)] = formBody.encodedValue(i)
            }

            //拼装新的参数
            val newParams = dynamic(oldParams)
            Util.checkNotNull<Any>(newParams, "newParams==null")
            //Logc.i("======post请求参数===========");
            for ((key, value1) in newParams) {
                val value = URLEncoder.encode(value1, "UTF-8")
                bodyBuilder.addEncoded(key, value)
                //Logc.i(entry.getKey() + " -> " + value);
            }
            val url = createUrlFromParams(httpUrl?.url().toString(), newParams)
            LogUtil.i(url)
            formBody = bodyBuilder.build()
            newRequest = newRequest.newBuilder().post(formBody).build()
        } else if (newRequest.body() is MultipartBody) {
            var multipartBody: MultipartBody = newRequest.body() as MultipartBody
            val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            val oldParts = multipartBody.parts()

            //拼装新的参数
            val newParts = ArrayList<MultipartBody.Part>()
            newParts.addAll(oldParts)
            val oldParams = TreeMap<String, String>()
            val newParams = dynamic(oldParams)
            for ((key, value) in newParams) {
                val part = MultipartBody.Part.createFormData(key, value)
                newParts.add(part)
            }
            for (part in newParts) {
                bodyBuilder.addPart(part)
            }
            multipartBody = bodyBuilder.build()
            newRequest = newRequest.newBuilder().post(multipartBody).build()
        }
        return newRequest
    }

    //解析前：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
    //解析后：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult
    private fun parseUrl(url: String): String {
        if ("" != url && url.contains("?")) {// 如果URL不是空字符串
            return url.substring(0, url.indexOf('?'))
        }
        return url
    }

    private fun createUrlFromParams(url: String, params: Map<String, String>): String {
        try {
            val sb = StringBuilder()
            sb.append(url)
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0)
                sb.append("&")
            else
                sb.append("?")
            for ((key, urlValues) in params) {
//对参数进行 utf-8 编码,防止头信息传中文
                //String urlValue = URLEncoder.encode(urlValues, UTF8.name());
                sb.append(key).append("=").append(urlValues).append("&")
            }
            sb.deleteCharAt(sb.length - 1)
            return sb.toString()
        } catch (e: Exception) {
            LogUtil.e(e.message)
        }

        return url
    }


    /**
     * 动态处理参数
     *
     * @param dynamicMap
     * @return 返回新的参数集合
     */
    abstract fun dynamic(dynamicMap: TreeMap<String, String>): TreeMap<String, String>

}
