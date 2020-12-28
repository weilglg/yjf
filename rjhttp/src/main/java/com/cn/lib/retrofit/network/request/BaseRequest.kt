package com.cn.lib.retrofit.network.request

import android.content.Context
import com.cn.lib.retrofit.network.ApiManager
import com.cn.lib.retrofit.network.RxHttp
import com.cn.lib.retrofit.network.cookie.CookieManager
import com.cn.lib.retrofit.network.entity.HttpParamEntity
import com.cn.lib.retrofit.network.interceptor.BaseDynamicInterceptor
import com.cn.lib.retrofit.network.interceptor.HeaderInterceptor
import com.cn.lib.retrofit.network.interceptor.RequestParamInterceptor
import com.cn.lib.retrofit.network.util.SSLUtil
import com.cn.lib.retrofit.network.util.Util
import io.reactivex.Observable
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.io.InputStream
import java.lang.NullPointerException
import java.net.Proxy
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Suppress("UNCHECKED_CAST")
abstract class BaseRequest<R : BaseRequest<R>>(internal var mUrl: String) {
    private var mCache: Cache? = null                                             //OkHttp缓存对象
    private var mCacheFile: File? = null                                          //缓存目录
    private var mCacheMaxSize: Long = 0                                           //最大缓存
    private var mProxy: Proxy? = null                                             //OkHttp代理
    private var mHostnameVerifier: HostnameVerifier? = null                       //https的全局访问规则
    private var mConverterFactory: Converter.Factory? = null                      //Converter.Factory
    private var mCallAdapterFactory: CallAdapter.Factory? = null                  //CallAdapter.Factory
    private var mSslSocketFactory: SSLSocketFactory? = null                       //签名验证规则
    private var mTrustManager: X509TrustManager? = null
    private var mSslParams: SSLUtil.SSLParams? = null                             //https签名证书
    private var mCookieJar: CookieJar? = null                                     //Cookie管理
    private var mConnectionPool: ConnectionPool? = null                           //链接池管理
    private val mHeaders = HashMap<String, String>()                              //公共请求头
    protected var mHttpParams = HttpParamEntity()                                    //请求参数集合
    private var mHttpClient: OkHttpClient? = null                                //自定义OkHttpClient
    private var mReadTimeout: Long = 0                                             //读超时
    private var mWriteTimeout: Long = 0                                            //写超时
    private var mConnectTimeout: Long = 0                                          //链接超时
    protected var mRetryCount: Int = 0                                              //重试次数默认3次
    protected var mRetryDelay: Int = 0                                              //延迟xxms重试
    protected var mRetryIncreaseDelay: Int = 0                                      //叠加延迟
    private val mInterceptorList = ArrayList<Interceptor>()
    private val mNetworkInterceptorList = ArrayList<Interceptor>()
    protected var mContext: Context
    private var mBaseUrl: String? = null
    private var isSign = false                                                     //是否需要签名
    private var accessToken = false                                                //是否需要添加token
    internal var isSyncRequest = true
    internal lateinit var mApiManager: ApiManager
    private var mHeaderInterceptor: HeaderInterceptor? = null
    private var mRequestParamInterceptor: RequestParamInterceptor? = null          //参数拦截器
    private var mCancelEncryption = false


    init {
        val rxHttp = RxHttp.INSTANCE
        this.mContext = rxHttp.getContext()
        this.isSign = rxHttp.isSign
        this.accessToken = rxHttp.isAccessToken
        this.isSyncRequest = rxHttp.isSyncRequest
        this.mRetryCount = rxHttp.retryCount
        this.mRetryDelay = rxHttp.retryDelay
        this.mRetryIncreaseDelay = rxHttp.retryIncreaseDelay
        if (mBaseUrl == null && (mUrl.startsWith("http://") || mUrl.startsWith("https://"))) {
            val httpUrl = HttpUrl.parse(mUrl)
            if (httpUrl != null)
                mBaseUrl = httpUrl.url().protocol + "://" + httpUrl.url().host + "/"
        }
        this.mCancelEncryption = rxHttp.mCancelEncryption
        this.mRequestParamInterceptor = rxHttp.requestParamInterceptor
        this.mHeaderInterceptor = rxHttp.baseHeaderInterceptor
    }

    /**
     * 设置请求的BaseUrl，这里设置后会替换全局设置
     */
    fun baseUrl(baseUrl: String): R {
        this.mBaseUrl = Util.checkNotNull(baseUrl, "baseUrl is null")
        return this as R
    }

    /**
     * 设置本次请求是否添加Token
     */
    fun isAccessToken(accessToken: Boolean): R {
        this.accessToken = accessToken
        return this as R
    }

    /**
     * 设置本次请求是否是异步请求
     */
    fun isSyncRequest(isSyncRequest: Boolean): R {
        this.isSyncRequest = isSyncRequest
        return this as R
    }

    /**
     * 是否取消加密
     */
    fun cancelEncryption(flag: Boolean): R {
        this.mCancelEncryption = flag
        return this as R
    }

    /**
     * 设置缓存
     */
    fun cache(cache: Cache): R {
        this.mCache = Util.checkNotNull(cache, "cache is null")
        return this as R
    }

    fun cacheFile(cacheFile: File): R {
        this.mCacheFile = Util.checkNotNull(cacheFile, "cacheFile is null")
        return this as R
    }

    /**
     * 设置OkHttpClient的代理
     */
    fun okProxy(proxy: Proxy): R {
        this.mProxy = Util.checkNotNull(proxy, "proxy is null")
        return this as R
    }

    /**
     * 设置访问规则
     */
    fun hostnameVerifier(hostnameVerifier: HostnameVerifier): R {
        this.mHostnameVerifier = Util.checkNotNull(hostnameVerifier, "hostnameVerifier is null")
        return this as R
    }

    /**
     * 设置Converter.Factory
     */
    fun converterFactory(converterFactory: Converter.Factory): R {
        this.mConverterFactory = Util.checkNotNull(converterFactory, "converterFactory is null")
        return this as R
    }

    fun callAdapterFactory(callAdapterFactory: CallAdapter.Factory): R {
        this.mCallAdapterFactory = Util.checkNotNull(callAdapterFactory, "callAdapterFactory is null")
        return this as R
    }

    fun sslSocketFactory(sslSocketFactory: SSLSocketFactory): R {
        this.mSslSocketFactory = Util.checkNotNull(sslSocketFactory, "sslSocketFactory is null")
        return this as R
    }

    fun trustManager(trustManager: X509TrustManager): R {
        this.mTrustManager = Util.checkNotNull(trustManager, "trustManager is null")
        return this as R
    }

    /**
     * 全局cookie存取规则
     */
    fun cookieJar(cookieJar: CookieManager): R {
        this.mCookieJar = Util.checkNotNull(cookieJar, "cookieJar is null")
        return this as R
    }

    /**
     * 全局设置请求的连接池
     */
    fun connectionPool(connectionPool: ConnectionPool): R {
        this.mConnectionPool = Util.checkNotNull(connectionPool, "connectionPool is null")
        return this as R
    }

    /**
     * 添加请求头
     */
    fun addHeader(headers: Map<String, String>): R {
        this.mHeaders.putAll(Util.checkNotNull(headers, "headers is null"))
        return this as R
    }

    /**
     * 添加请求头
     */
    fun addHeader(key: String, value: String): R {
        this.mHeaders[key] = value
        return this as R
    }

    /**
     * 删除某个请求头
     */
    fun removeHeader(key: String): R {
        mHeaderInterceptor?.run {
            remove(key)
        }
        return this as R
    }

    /**
     * 清除所有请求头
     */
    fun clearHeaders(): R {
        mHeaderInterceptor?.run {
            clearAll()
        }
        return this as R
    }

    /**
     * 添加拦截器
     */
    fun addInterceptor(requestParamInterceptor: RequestParamInterceptor): R {
        mRequestParamInterceptor = requestParamInterceptor
        return this as R
    }

    /**
     * 添加请求参数
     */
    fun param(params: Map<String, String>): R {
        this.mHttpParams.param(Util.checkNotNull(params, "param is null"))
        return this as R
    }

    /**
     * 添加请求参数
     */
    fun param(key: String, value: String): R {
        this.mHttpParams.param(key, value)
        return this as R
    }

    /**
     * 添加请求参数
     */
    fun body(key: String, value: String): R {
        this.mHttpParams.body(key,value)
        return this as R
    }

    /**
     * 设置OkHttpClient
     */
    fun httpClient(httpClient: OkHttpClient): R {
        this.mHttpClient = Util.checkNotNull(httpClient, "OkHttpClient is null")
        return this as R
    }

    /**
     * 设置读取超时时间
     */
    fun readTimeOut(readTimeout: Long): R {
        if (mReadTimeout < 0)
            throw IllegalArgumentException("readTimeout must > 0")
        this.mReadTimeout = readTimeout
        return this as R
    }

    /**
     * 设置写入超时时间
     */
    fun writeTimeOut(writeTimeout: Long): R {
        if (mWriteTimeout < 0)
            throw IllegalArgumentException("writeTimeout must > 0")
        this.mWriteTimeout = writeTimeout
        return this as R
    }

    /**
     * 设置链接超时时间
     */
    fun connectTimeOut(connectTimeout: Long): R {
        if (mConnectTimeout < 0)
            throw IllegalArgumentException("connectTimeout must > 0")
        this.mConnectTimeout = connectTimeout
        return this as R
    }

    /**
     * 添加拦截器
     */
    fun interceptorList(interceptorList: List<Interceptor>): R {
        this.mInterceptorList.addAll(Util.checkNotNull(interceptorList, "Interceptor is null"))
        return this as R
    }

    /**
     * 添加拦截器
     */
    fun addInterceptor(interceptor: Interceptor): R {
        this.mInterceptorList.add(Util.checkNotNull(interceptor, "Interceptor is null"))
        return this as R
    }

    /**
     * 添加网络拦截器
     */
    fun networkInterceptorList(networkInterceptorList: List<Interceptor>): R {
        this.mNetworkInterceptorList.addAll(Util.checkNotNull(networkInterceptorList, "NetworkInterceptor is null"))
        return this as R
    }

    /**
     * 添加网络拦截器
     */
    fun addNetworkInterceptor(interceptor: Interceptor): R {
        this.mNetworkInterceptorList.add(Util.checkNotNull(interceptor, "NetworkInterceptor is null"))
        return this as R
    }

    /**
     * 是否需要签名
     */
    fun isSign(isSign: Boolean): R {
        this.isSign = isSign
        return this as R
    }

    /**
     * 设置重试次数
     */
    fun retryCount(mRetryCount: Int): R {
        if (mRetryCount < 0)
            throw IllegalArgumentException("retryIncreaseDelay must > 0")
        this.mRetryCount = mRetryCount
        return this as R
    }

    /**
     * 设置超时重试延迟时间
     */
    fun retryDelay(mRetryDelay: Int): R {
        if (mRetryDelay < 0)
            throw IllegalArgumentException("retryIncreaseDelay must > 0")
        this.mRetryDelay = mRetryDelay
        return this as R
    }

    /**
     * 设置超时重试延迟叠加时间
     */
    fun retryIncreaseDelay(mRetryIncreaseDelay: Int): R {
        if (mRetryIncreaseDelay < 0)
            throw IllegalArgumentException("retryIncreaseDelay must > 0")
        this.mRetryIncreaseDelay = mRetryIncreaseDelay
        return this as R
    }

    /**
     * 设置SslSocketFactory
     */
    fun certificates(vararg certificates: InputStream): R {
        certificates(null, null, *certificates)
        return this as R
    }

    /**
     * 设置SslSocketFactory
     */
    fun certificates(bksFile: InputStream?, password: String?, vararg certificates: InputStream): R {
        this.mSslParams = SSLUtil.getSslSocketFactory(bksFile, password, *certificates)
        return this as R
    }

    /**
     * 设置缓存的最大值
     */
    fun cacheMaxSize(cacheMaxSize: Long): R {
        this.mCacheMaxSize = cacheMaxSize
        return this as R
    }

    private fun generateOkHttpClientBuilder(): OkHttpClient.Builder {
        if (mReadTimeout <= 0 && mWriteTimeout <= 0 && mConnectTimeout <= 0 && mSslParams == null
                && mCookieJar == null && mCache == null && mCacheFile == null && mCacheMaxSize <= 0
                && mInterceptorList.size == 0 && mNetworkInterceptorList.size == 0 && mProxy == null
                && mSslSocketFactory == null && mTrustManager == null && mHostnameVerifier == null
                && mCallAdapterFactory == null && mConverterFactory == null && mHeaders.isEmpty()) {
            val builder = RxHttp.INSTANCE.getOkHttpClientBuilder()
            builder.interceptors()
                    .filterIsInstance<BaseDynamicInterceptor<*>>()
                    .forEach { it.sign(isSign).accessToken(accessToken) }
            return builder
        } else {
            val newBuilder = RxHttp.INSTANCE.okHttpClient.newBuilder()
            if (mReadTimeout > 0) {
                newBuilder.readTimeout(mReadTimeout, TimeUnit.SECONDS)
            }
            if (mWriteTimeout > 0) {
                newBuilder.writeTimeout(mWriteTimeout, TimeUnit.SECONDS)
            }
            if (mConnectTimeout > 0) {
                newBuilder.connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
            }
            mSslSocketFactory?.let { factory ->
                mTrustManager?.apply {
                    newBuilder.sslSocketFactory(factory, this)
                } ?: newBuilder.sslSocketFactory(factory)
            }
            mSslParams?.apply {
                newBuilder.sslSocketFactory(sSLSocketFactory, trustManager)
            }
            mHostnameVerifier?.let {
                newBuilder.hostnameVerifier(it)
            }

            if (mCacheFile == null) {
                mCacheFile = File(mContext.cacheDir, "retrofit_http_cache")
            }
            if (mCache == null) {
                mCacheFile?.let {
                    mCache = Cache(it, Math.max((5 * 1024 * 1024).toLong(), mCacheMaxSize))
                }
            }
            mCache?.apply {
                newBuilder.cache(this)
            }
            mConnectionPool?.let {
                newBuilder.connectionPool(it)
            }
            mProxy?.let {
                newBuilder.proxy(it)
            }
            mCookieJar?.let {
                newBuilder.cookieJar(it)
            }
            if (mHeaders.isNotEmpty()) {
                addHeaderInterceptor(newBuilder)
            }
            if (mInterceptorList.isNotEmpty()) {
                for (interceptor in mInterceptorList) {
                    newBuilder.addInterceptor(interceptor)
                }
            }
            newBuilder.interceptors()
                    .filterIsInstance<BaseDynamicInterceptor<*>>()
                    .forEach { it.sign(isSign).accessToken(accessToken) }
            if (mNetworkInterceptorList.isNotEmpty()) {
                for (interceptor in mNetworkInterceptorList) {
                    newBuilder.addNetworkInterceptor(interceptor)
                }
            }
            return newBuilder
        }

    }

    /**
     * 添加请求头，并保证在拦截器的第一位，以方便后面的拦截器使用到头信息
     */
    private fun addHeaderInterceptor(newBuilder: OkHttpClient.Builder) {
        mHeaderInterceptor?.run {
            addHeaderMap(mHeaders)
            return
        }
        if (mHeaders.size > 0) {
            mHeaderInterceptor = HeaderInterceptor(mHeaders)
            //将添加统一头内容的拦截器放在第一位方便后面的拦截器使用
            if (newBuilder.interceptors().size > 0) {
                newBuilder.interceptors().add(0, mHeaderInterceptor)
            } else {
                newBuilder.interceptors().add(mHeaderInterceptor)
            }
        }
    }

    private fun generateRetrofitBuilder(): Retrofit.Builder {
        val rxHttp = RxHttp.INSTANCE
        if (mBaseUrl == null || (mBaseUrl == rxHttp.baseUrl && mConverterFactory == null
                && mCallAdapterFactory == null && mHttpClient == null && rxHttp.httpClient == null)) {
            return rxHttp.retrofitBuilder
        } else {
            val builder = rxHttp.retrofit.newBuilder()
            mBaseUrl?.run {
                builder.baseUrl(this)
            }
            mCallAdapterFactory?.run {
                builder.addCallAdapterFactory(this)
            }
            mConverterFactory?.run {
                builder.addConverterFactory(this)
            }
            return builder
        }
    }

    protected fun build(): R {
        val okHttpClientBuilder = generateOkHttpClientBuilder()
        val retrofitBuilder = generateRetrofitBuilder()
        var mOkHttpClient = okHttpClientBuilder.build()
        RxHttp.INSTANCE.httpClient?.let {
            mOkHttpClient = it
        }
        mHttpClient?.let {
            mOkHttpClient = it
        }
        if (mOkHttpClient == null) {
            throw NullPointerException("OKHttpClient is null")
        }
        retrofitBuilder.client(mOkHttpClient)
        val mRetrofit = retrofitBuilder.build()
        mApiManager = mRetrofit.create(ApiManager::class.java)
        if (!mCancelEncryption) {
            mRequestParamInterceptor?.let {
                mHttpParams = it.intercept(mHttpParams)
            }
        }
        return this as R
    }


    protected abstract fun generateRequest(): Observable<ResponseBody>

}
