/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cn.lib.retrofit.network.interceptor


import android.util.Log
import com.alibaba.fastjson.util.IOUtils.UTF8
import com.cn.lib.retrofit.network.util.LogUtil
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset


/**
 *
 * 描述：判断响应是否有效的处理
 * 继承后扩展各种无效响应处理：包括token过期、账号异地登录、时间戳过期、签名sign错误等<br></br>
 */
abstract class BaseExpiredInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("TAG","================intercept=================")
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body()
        val source = responseBody!!.source()
        source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer()
        var charset: Charset? = UTF8
        val contentType = responseBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(UTF8)
        }
        val bodyString = buffer.clone().readString(charset!!)
        LogUtil.i("网络拦截器:" + bodyString + " host:" + request.url().toString())
        val isText = isText(contentType)
        if (!isText) {
            return response
        }
        //判断响应是否过期（无效）
        return if (isResponseExpired(response, bodyString)) {
            responseExpired(chain, bodyString)
        } else response
    }

    private fun isText(mediaType: MediaType?): Boolean {
        if (mediaType == null)
            return false
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype() == "json") {
                return true
            }
        }
        return false
    }

    /**
     * 处理响应是否有效
     */
    abstract fun isResponseExpired(response: Response, bodyString: String): Boolean

    /**
     * 无效响应处理
     */
    abstract fun responseExpired(chain: Interceptor.Chain, bodyString: String): Response
}
