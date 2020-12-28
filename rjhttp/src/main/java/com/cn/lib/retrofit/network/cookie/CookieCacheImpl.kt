package com.cn.lib.retrofit.network.cookie

import java.util.HashSet

import okhttp3.Cookie

abstract class CookieCacheImpl : CookieCache {
    private val cookies: MutableSet<IdentifiableCookie>

    init {
        cookies = HashSet()
    }

    override fun addAll(newCookies: Collection<Cookie>) {
        for (cookie in IdentifiableCookie.decorateAll(newCookies)) {
            this.cookies.remove(cookie)
            this.cookies.add(cookie)
        }
    }

    override fun clear() {
        cookies.clear()
    }

    fun iterator(): SetCookieCacheIterator {
        return SetCookieCacheIterator()
    }

    inner class SetCookieCacheIterator internal constructor() : MutableIterator<Cookie> {

        private val iterator: MutableIterator<IdentifiableCookie> = cookies.iterator()

        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): Cookie {
            return iterator.next().cookie
        }

        override fun remove() {
            iterator.remove()
        }
    }
}
