package cn.ygyg.cloudpayment.modular.payments.presenter

import android.util.Log
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.internet.entity.DeviceResponseEntity
import cn.ygyg.cloudpayment.modular.payments.contract.PaymentsActivityContract
import cn.ygyg.cloudpayment.modular.payments.entity.CreateOrderResponseEntity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.ProgressUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.lcodecore.tkrefreshlayout.utils.LogUtil

class PaymentsActivityPresenter(view: PaymentsActivityContract.View) :
        BasePresenterImpl<PaymentsActivityContract.View>(view),
        PaymentsActivityContract.Presenter {
    override fun getBindDevice(deviceCode: String, companyCode: String) {
        RequestManager.post(UrlConstants.getDevice)
                .param("meterCode", deviceCode)
                .param("companyCode", companyCode)
                .execute("", object : ResultCallback<DeviceResponseEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.showProgressDialog(it, "加载中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.dismissProgressDialog()
                        }
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        LogUtil.i(e.message)
                        mvpView?.onLoadDeviceError(e)
                    }

                    override fun onSuccess(tag: Any?, result: DeviceResponseEntity?) {
                        result?.let {
                            mvpView?.onLoadDeviceSuccess(it)
                        }
                    }
                })
    }

    override fun createOrder(amount: String, contractCode: String, phone: String,meterClassification: String, paymentMethod: Constants.PaymentMethod, payType: String) {
        RequestManager.post(UrlConstants.createOrder)
                .param("amount", amount)
                .param("applicationId", ConfigUtil.getApplicationId())
                .param("companyCode", ConfigUtil.getCompanyCode())
                .param("contractCode", contractCode)
                .param("mobile", phone)
                .param("meterClassification", meterClassification)
                .param("paymentMethod", paymentMethod.string())
                .param("paymentType", payType)
                .execute("", object : ResultCallback<CreateOrderResponseEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.showProgressDialog(it, "加载中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.dismissProgressDialog()
                        }
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onCreateOrderError(e)
                    }

                    override fun onSuccess(tag: Any?, result: CreateOrderResponseEntity?) {
                        result?.let {
                            mvpView?.onCreateOrderSuccess(result)
                        }
                    }
                })
    }
}
