package com.cn.lib.retrofit.network.interceptor


import android.content.Context
import android.text.TextUtils
import android.util.Log

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

open class CacheInterceptor(protected var context: Context, protected var cacheControlValue_Offline: String, protected var cacheControlValue_Online: String) : Interceptor {

    @JvmOverloads constructor(context: Context, cacheControlValue: String = String.format("max-age=%d", maxStaleOnline)) : this(context, cacheControlValue, String.format("max-age=%d", maxStale)) {}

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cacheControl = originalResponse.header("Cache-Control")
        //String cacheControl = request.cacheControl().toString();
        Log.d("RxHttp", maxStaleOnline.toString() + "s load cache:" + cacheControl)
        return if (TextUtils.isEmpty(cacheControl) || cacheControl!!.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=$maxStale")
                    .build()

        } else {
            originalResponse
        }
    }

    companion object {
        //set cahe times is 3 days
        protected val maxStale = 60 * 60 * 24 * 3
        // read from cache for 60 s
        protected val maxStaleOnline = 60
    }
    /* Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, " + cacheOnlineControlValue)
                    .build();
        } */

    /*else {
     *//*((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.load_cache, Toast.LENGTH_SHORT).show();
                }
            });*//*
            LogWraper.e("Novate", " no network load cache");
          *//*  request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();*//*
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .build();
        }
    }*/
}
