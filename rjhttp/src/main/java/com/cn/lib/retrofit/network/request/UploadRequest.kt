package com.cn.lib.retrofit.network.request

import android.text.TextUtils

import com.cn.lib.retrofit.network.entity.UploadFileType
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.proxy.ResultCallbackProxy
import com.cn.lib.retrofit.network.proxy.ResultClazzCallProxy
import com.cn.lib.retrofit.network.callback.ResultProgressCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import com.cn.lib.retrofit.network.entity.FileEntity
import com.cn.lib.retrofit.network.func.ApiResultFunc
import com.cn.lib.retrofit.network.func.RetryExceptionFunc
import com.cn.lib.retrofit.network.interceptor.ProgressRequestInterceptor
import com.cn.lib.retrofit.network.subscriber.ResultCallbackSubscriber
import com.cn.lib.retrofit.network.util.RxUtil
import com.cn.lib.retrofit.network.util.Util

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.lang.NullPointerException

open class UploadRequest(url: String) : HttpBodyRequest<UploadRequest>(url) {

    private var mUploadType: UploadFileType? = null

    override fun requestBody(requestBody: RequestBody): UploadRequest {
        this.mRequestBody = requestBody
        return this
    }

    fun params(key: String, file: File): UploadRequest {
        mHttpParams.put(key, file)
        return this
    }

    fun params(key: String, fileName: String, stream: InputStream): UploadRequest {
        mHttpParams.put(key, fileName, stream)
        return this
    }

    fun params(key: String, fileName: String, bytes: ByteArray): UploadRequest {
        mHttpParams.put(key, fileName, bytes)
        return this
    }

    fun addFileParams(key: String, files: List<File>?): UploadRequest {
        if (TextUtils.isEmpty(key) && files != null && !files.isEmpty()) {
            for (file in files) {
                params(key, file)
            }
        }
        return this
    }

    fun addFileWrapperParams(key: String, fileWrappers: List<FileEntity<*>>): UploadRequest {
        mHttpParams.put(key, fileWrappers)
        return this
    }

    fun params(key: String, file: File, fileName: String): UploadRequest {
        mHttpParams.put(key, file, fileName)
        return this
    }

    fun <T> params(key: String, fileName: String, file: T, contentType: MediaType): UploadRequest {
        mHttpParams.put(key, fileName, file, contentType)
        return this
    }

    fun uploadType(type: UploadFileType): UploadRequest {
        this.mUploadType = type
        return this
    }

    override fun generateRequest(): Observable<ResponseBody> {
        Util.checkNotNull(mUploadType, "UploadType is null")
        return if (mUploadType == UploadFileType.BODY_MAP) {
            uploadFilesWithBodyMap()
        } else if (mUploadType == UploadFileType.PART_FROM) {
            uploadFilesWithPartList()
        } else if (mUploadType == UploadFileType.PART_MAP) {
            uploadFilesWithPartMap()
        } else {
            mRequestBody?.let {
                return mApiManager.postBody(mUrl, it)
            }
            throw NullPointerException("request body is null")
        }
    }


     fun <T> execute(clazz: Class<T>, callback: ResultProgressCallback<T>): Observable<Optional<T>> {
        return execute(object : ResultClazzCallProxy<ApiResultEntity<T>, T>(clazz) {

        }, callback)
    }

     fun <T> execute(type: Type, callback: ResultProgressCallback<T>): Observable<Optional<T>> {
        return execute(object : ResultClazzCallProxy<ApiResultEntity<T>, T>(type) {

        }, callback)
    }

    private fun <T> execute(proxy: ResultClazzCallProxy<out ApiResultEntity<T>, T>, callback: ResultProgressCallback<T>): Observable<Optional<T>> {
        return addInterceptor(ProgressRequestInterceptor(null, callback)).build().generateRequest()
                .map(ApiResultFunc<T>(proxy.getType()))
                .compose<Optional<T>>(if (isSyncRequest) RxUtil._io_main_result() else RxUtil._main_result())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }

     open fun <T> execute(tag: Any, callback: ResultCallback<T>): Disposable {
        return execute(tag, ResultCallbackProxy.NEW_DEFAULT_INSTANCE(callback))
    }

     fun <T> execute(tag: Any, proxy: ResultCallbackProxy<out ApiResultEntity<T>, T>): Disposable {
        val callback = proxy.callback
        if (callback is ResultProgressCallback) {
            addInterceptor(ProgressRequestInterceptor(tag, callback))
        }
        val observable = build().generateObservable(generateRequest(), proxy)
        return observable.subscribeWith(ResultCallbackSubscriber(tag, callback))
    }

    private fun <T> generateObservable(observable: Observable<ResponseBody>, proxy: ResultCallbackProxy<out ApiResultEntity<T>, T>): Observable<Optional<T>> {
        return observable.map(ApiResultFunc<T>(proxy.getType()))
                .compose<Optional<T>>(if (isSyncRequest) RxUtil._io_main_result() else RxUtil._main_result())
                .retryWhen(RetryExceptionFunc(mRetryCount, mRetryDelay.toLong(), mRetryIncreaseDelay.toLong()))
    }


    /**
     * 以RequestBody的Map形式提交
     *
     * @return
     */
    private fun uploadFilesWithBodyMap(): Observable<ResponseBody> {
        val mBodyMap = HashMap<String, RequestBody>()
        //拼接参数键值对
        for ((key, value) in mHttpParams.paramMap) {
            val body = RequestBody.create(MediaType.parse("text/plain"), value)
            mBodyMap[key] = body
        }
        //拼接文件
        for ((key, fileValues) in mHttpParams.fileMap) {
            for (fileWrapper in fileValues) {
                val requestBody = getRequestBody(fileWrapper)
                requestBody?.let {
                    mBodyMap[key] = it
                }
            }
        }
        return mApiManager.uploadFileWithBodyMap(mUrl, mBodyMap)
    }

    /**
     * 以MultipartBody.Part的List形式提交
     *
     * @return
     */
    private fun uploadFilesWithPartList(): Observable<ResponseBody> {
        val partList = ArrayList<MultipartBody.Part>()
        val paramMap = mHttpParams.paramMap
        for (key in paramMap.keys) {
            partList.add(MultipartBody.Part.createFormData(key, paramMap[key]))
        }
        for ((key, value) in mHttpParams.fileMap) {
            for (entity in value) {
                val part = createPartBody(key, entity)
                partList.add(part)
            }
        }
        return mApiManager.uploadFileWithPartList(mUrl, partList)
    }

    /**
     * 以MultipartBody.Part的Map形式提交
     *
     * @return
     */
    private fun uploadFilesWithPartMap(): Observable<ResponseBody> {
        val partMap = HashMap<String, MultipartBody.Part>()
        //拼接普通参数
        val paramMap = mHttpParams.paramMap
        for (key in paramMap.keys) {
            partMap[key] = MultipartBody.Part.createFormData(key, paramMap[key])
        }
        //拼接文件参数
        val fileMap = mHttpParams.fileMap
        for ((key, value) in fileMap) {
            for (entity in value) {
                val part = createPartBody(key, entity)
                partMap[key] = part
            }
        }
        return mApiManager.uploadFileWithPartMap(mUrl, partMap)
    }

    private fun createPartBody(key: String, value: FileEntity<*>): MultipartBody.Part {
        val requestBody = getRequestBody(value)
        Util.checkNotNull<Any>(requestBody, "requestBody==null fileEntity.data must is File/InputStream/byte[]")
        return MultipartBody.Part.createFormData(key, value.fileName, requestBody!!)
    }


    private fun getRequestBody(value: FileEntity<*>): RequestBody? {
        var requestBody: RequestBody? = null
        if (value.data is File) {
            requestBody = RequestBody.create(value.mediaType, (value.data as File?)!!)
        } else if (value.data is InputStream) {
            requestBody = create(value.mediaType, value.data as InputStream)
        } else if (value.data is ByteArray) {
            requestBody = RequestBody.create(value.mediaType, (value.data as ByteArray?)!!)
        }
        return requestBody
    }

    private fun create(mediaType: MediaType?, inputStream: InputStream): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return mediaType
            }

            override fun contentLength(): Long {
                try {
                    return inputStream.available().toLong()
                } catch (e: IOException) {
                    return 0
                }

            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = Okio.source(inputStream)
                    sink.writeAll(source!!)
                } finally {
                    okhttp3.internal.Util.closeQuietly(source)
                }
            }
        }
    }

}
