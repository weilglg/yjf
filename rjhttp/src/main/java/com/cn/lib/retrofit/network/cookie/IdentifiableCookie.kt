package com.cn.lib.retrofit.network.cookie

import java.util.ArrayList

import okhttp3.Cookie

internal class IdentifiableCookie(val cookie: Cookie) {

    override fun equals(other: Any?): Boolean {
        if (other !is IdentifiableCookie) return false
        val that = other as IdentifiableCookie?
        return (that!!.cookie.name() == this.cookie.name()
                && that.cookie.domain() == this.cookie.domain()
                && that.cookie.path() == this.cookie.path()
                && that.cookie.secure() == this.cookie.secure()
                && that.cookie.hostOnly() == this.cookie.hostOnly())
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + cookie.name().hashCode()
        hash = 31 * hash + cookie.domain().hashCode()
        hash = 31 * hash + cookie.path().hashCode()
        hash = 31 * hash + if (cookie.secure()) 0 else 1
        hash = 31 * hash + if (cookie.hostOnly()) 0 else 1
        return hash
    }

    companion object {

        fun decorateAll(cookies: Collection<Cookie>): List<IdentifiableCookie> {
            val identifiableCookies = ArrayList<IdentifiableCookie>(cookies.size)
            for (cookie in cookies) {
                identifiableCookies.add(IdentifiableCookie(cookie))
            }
            return identifiableCookies
        }
    }
}
