package cn.ygyg.cloudpayment.modular.internet.presenter

import cn.ygyg.cloudpayment.modular.internet.contract.NewAccountActivityContract
import cn.ygyg.cloudpayment.modular.internet.entity.DeviceResponseEntity
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.utils.ProgressUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.hwangjr.rxbus.RxBus

class NewAccountActivityPresenter(view: NewAccountActivityContract.View) :
        BasePresenterImpl<NewAccountActivityContract.View>(view),
        NewAccountActivityContract.Presenter {
    override fun getDevice(deviceCode: String, companyCode: String) {
        RequestManager.post(UrlConstants.getDevice)
                .param("meterCode", deviceCode)
                .param("companyCode", companyCode)
                .param("userName", UserUtil.getUserName())
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
                        mvpView?.onBindDeviceError(e)
                    }

                    override fun onSuccess(tag: Any?, result: DeviceResponseEntity?) {
                        result?.let {
                            mvpView?.onLoadDeviceSuccess(it, deviceCode)
                        }
                    }
                })
    }

    override fun bindDevice(deviceCode: String, companyCode: String) {
        RequestManager.post(UrlConstants.bindDevice)
                .param("meterCode", deviceCode)
                .param("companyCode", companyCode)
                .param("username", UserUtil.getUserName())
                .execute("", object : ResultCallback<DeviceResponseEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.showProgressDialog(it, "绑定中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.dismissProgressDialog()
                        }
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onBindDeviceError(e)
                    }

                    override fun onSuccess(tag: Any?, result: DeviceResponseEntity?) {
                        mvpView?.onBindDeviceSuccess(result!!)
                        RxBus.get().post("refreshDevice", "")
                    }
                })
    }
}
