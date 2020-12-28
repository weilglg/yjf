package cn.ygyg.cloudpayment.modular.home.presenter

import android.content.Context
import cn.ygyg.cloudpayment.BuildConfig
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.modular.home.contract.WelcomeContract
import cn.ygyg.cloudpayment.modular.home.entity.CompanyEntity
import cn.ygyg.cloudpayment.utils.ConfigEntity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable

class WelcomePresenter(view: WelcomeContract.View) : BasePresenterImpl<WelcomeContract.View>(view), WelcomeContract.Presenter {
    /**
     * 加载厂家配置
     */
    override fun loaderPaymentConfig(context: Context) {
        RequestManager.get(UrlConstants.getPaymentInformation)
                .param("configurationName", BuildConfig.APPLICATION_ID)
                .execute("getCompany", object : ResultCallback<ConfigEntity>() {

                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.completed()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.loaderConfigError()
                    }

                    override fun onSuccess(tag: Any?, result: ConfigEntity?) {
                        result?.apply {
                            ConfigUtil.saveConfig(this)
                            if (ConfigUtil.isNotEmpty()) {
                                loaderCompanyTelephone()
                            } else {
                                mvpView?.loaderConfigError()
                            }
                        } ?: mvpView?.loaderConfigError()
                    }
                })
    }

    // 加载厂家电话号码
    fun loaderCompanyTelephone() {
        RequestManager.get(UrlConstants.getTelephone)
                .param("companyCode", ConfigUtil.getCompanyCode())
                .execute("getCompanyTelephone", object : ResultCallback<CompanyEntity>() {
                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {

                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {

                    }

                    override fun onSuccess(tag: Any?, result: CompanyEntity?) {
                        result?.apply {
                            ConfigUtil.setCompanyInfo(this)
                            mvpView?.jumpPage()
                        } ?: mvpView?.loaderConfigError()
                    }
                })
    }
}