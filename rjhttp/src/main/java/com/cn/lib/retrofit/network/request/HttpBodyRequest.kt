package com.cn.lib.retrofit.network.request

import android.annotation.SuppressLint

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cn.lib.retrofit.network.util.Util

import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

@Suppress("UNCHECKED_CAST")
@SuppressLint("ParcelCreator")
open class HttpBodyRequest<R : BaseRequest<R>>(url: String) : BaseRequest<R>(url) {
    protected var mRequestBody: RequestBody? = null
    protected var mJsonStr: String? = null
    protected var mJsonObj: JSONObject? = null
    protected var mJsonArr: JSONArray? = null
    protected var mBytes: ByteArray? = null
    protected var mStr: String? = null
    protected var mObject: Any? = null
    protected var mMediaType: MediaType? = null
    private var bb: String? = null

    open fun requestBody(requestBody: RequestBody): R {
        this.mRequestBody = requestBody
        return this as R
    }

    fun json(jsonStr: String): R {
        this.mJsonStr = jsonStr
        return this as R
    }

    fun jsonObj(obj: JSONObject): R {
        this.mJsonObj = obj
        return this as R
    }

    fun jsonArr(jsonArray: JSONArray): R {
        this.mJsonArr = jsonArray
        return this as R
    }

    fun `object`(obj: Any): R {
        this.mObject = obj
        return this as R
    }

    fun bytes(bytes: ByteArray): R {
        this.mBytes = bytes
        return this as R
    }

    fun txt(txt: String): R {
        this.mStr = txt
        this.mMediaType = okhttp3.MediaType.parse("text/plain")
        return this as R
    }

    fun mediaType(txt: String, mediaType: MediaType): R {
        this.mStr = txt
        Util.checkNotNull<Any>(mediaType, "mediaType==null")
        this.mMediaType = mediaType
        return this as R
    }

    override fun generateRequest(): Observable<ResponseBody> {
        mRequestBody?.let {
            return mApiManager.postBody(mUrl,it)
        }
        mJsonStr?.let {
            return mApiManager.potJsonStr(mUrl, Util.createJson(it))
        }
        mJsonObj?.let {
            return mApiManager.postJson(mUrl, it)
        }
        mJsonArr?.let {
            return mApiManager.postJson(mUrl, it)
        }
        mStr?.let {
            val requestBody = RequestBody.create(mMediaType, it)
            return mApiManager.postBody(mUrl, requestBody)
        }
        mBytes?.let {
            return mApiManager.postBody(mUrl, Util.createBytes(it))
        }

        mObject?.let {
            return mApiManager.postBody(mUrl, it)
        }
        if(!mHttpParams.isBodyssEmpty()){
            return mApiManager.postBody(mUrl,mHttpParams.bodyparamMap)
        }
        if (!mHttpParams.isParamsEmpty() && mHttpParams.isFilesEmpty()) {
            return mApiManager.postMap(mUrl, mHttpParams.paramMap)
        }
        return mApiManager.post(mUrl)
    }

}
