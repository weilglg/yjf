package com.cn.lib.retrofit.network.cookie

import okhttp3.Cookie

interface CookieCache : Iterator<Any> {


    fun addAll(newCookies: Collection<Cookie>)


    fun clear()
}

