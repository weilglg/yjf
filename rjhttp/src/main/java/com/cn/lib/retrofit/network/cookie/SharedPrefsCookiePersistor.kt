package com.cn.lib.retrofit.network.cookie

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

import java.util.ArrayList

import okhttp3.Cookie

@SuppressLint("CommitPrefEdits")
class SharedPrefsCookiePersistor(private val sharedPreferences: SharedPreferences) : CookiePersistor {

    constructor(context: Context) : this(context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)) {}

    override fun loadAll(): List<Cookie> {
        val cookies = ArrayList<Cookie>(sharedPreferences.all.size)

        for ((_, value) in sharedPreferences.all) {
            val serializedCookie = value as String
            val cookie = SerializableCookie().decode(serializedCookie)
            if (cookie != null) {
                cookies.add(cookie)
            }
        }
        return cookies
    }

    override fun saveAll(cookies: Collection<Cookie>) {
        val editor = sharedPreferences.edit()
        for (cookie in cookies) {
            editor.putString(createCookieKey(cookie), SerializableCookie().encode(cookie))
        }
        editor.commit()
    }

    override fun removeAll(cookies: Collection<Cookie>) {
        val editor = sharedPreferences.edit()
        for (cookie in cookies) {
            editor.remove(createCookieKey(cookie))
        }
        editor.commit()
    }

    override fun clear() {
        sharedPreferences.edit().clear().commit()
    }

    companion object {

        private val COOKIE_PREFS = "Cookies_Prefs"

        private fun createCookieKey(cookie: Cookie): String {
            return (if (cookie.secure()) "https" else "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name()
        }
    }
}
