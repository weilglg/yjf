package com.cn.lib.retrofit.network.interceptor

import okhttp3.FormBody
import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response


class RequestInterceptor(private val tag: Any) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body()
        val newBodyBuilder = FormBody.Builder()
        val method = request.method()
        if (method == "GET") {
            val httpUrl = request.url()
            val urlStr = httpUrl.toString()
            val urlSplit = urlSplit(urlStr)

        } else if (method == "POST" && body is FormBody) {
            val size = body.size()
            for (index in 0 until size) {
                val value = body.value(index)
                val name = body.name(index)
                newBodyBuilder.add(name, value)
            }
        } else {

        }
        return chain.proceed(request.newBuilder().method(request.method(), newBodyBuilder.build()).build())
    }

    private fun urlSplit(url: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        val start = url.indexOf("?")
        if (start != -1) {
            val str: String = url.substring(start)
            if (str.contains("&")) {
                val split = str.split("&")
                split.map { it.split("=") }
                        .forEach { map[it[0]] = it[1] }
            } else if (str.contains("&")) {
                val split = str.split("&")
                split.map { it.split("=") }
                        .forEach { map[it[0]] = it[1] }
            }
        }
        return map
    }
}
