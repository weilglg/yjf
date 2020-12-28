package cn.ygyg.cloudpayment.modular.home.contract

import android.content.Context
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

/**
 * Created by Admin on 2019/4/17.
 */
class WelcomeContract {
    interface View : IBaseView {
        fun loaderConfigError()
        fun jumpPage()
        fun completed()

    }

    interface Presenter : IBasePresenter<View> {
        fun loaderPaymentConfig(context: Context)

    }
}