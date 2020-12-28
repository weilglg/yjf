package cn.ygyg.cloudpayment.app

import android.annotation.SuppressLint
import android.app.Application
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.net.ParamInterceptor
import cn.ygyg.cloudpayment.net.TokenInterceptor
import cn.ygyg.cloudpayment.utils.SharePreUtil
import cn.ygyg.cloudpayment.widget.LoadMoreView
import cn.ygyg.cloudpayment.widget.ProgressHeaderView
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
import com.cn.lib.retrofit.network.RxHttp
import com.cn.lib.util.ToastUtil
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * Created by Admin on 2019/4/13.
 */
open class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mApp: MyApplication

        fun getApplication(): MyApplication {
            return mApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        initRequestBase()
        ToastUtil.initResId(R.mipmap.toast_success, 0)
        SharePreUtil.init(context = baseContext)
        TwinklingRefreshLayout.setDefaultHeader(ProgressHeaderView::class.java.name)
        TwinklingRefreshLayout.setDefaultFooter(LoadMoreView::class.java.name)
    }

    private fun initRequestBase() {
        RxHttp.INSTANCE
                .init(context = baseContext)
//                .baseUrl("http://app.yunfuw.cn/")//域名 prod
                .baseUrl("http://10.38.64.79:8130/")//dev
//                .baseUrl("http://10.240.0.105:8130/")//beta
//                .baseUrl("http://10.38.77.50:8130/")//alpha
                .isLog(true)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .connectTimeout(60 * 1000)
                .retryCount(3)
                .retryDelay(500)
                .retryIncreaseDelay(500)
                .cancelEncryption(true)
                .addNetworkInterceptor(TokenInterceptor())
                .addInterceptor(ParamInterceptor())
                .callAdapterFactory(RxJava2CallAdapterFactory.create())
                .converterFactory(Retrofit2ConverterFactory())
    }
}