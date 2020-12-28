package cn.ygyg.cloudpayment.modular.home.presenter

import android.content.Context
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.modular.home.contract.HomeContract
import cn.ygyg.cloudpayment.modular.home.entity.DeviceListResponseEntity
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.utils.*
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable

/**
 * Created by Admin on 2019/4/17.
 */
class HomePresenter(view: HomeContract.View) : BasePresenterImpl<HomeContract.View>(view), HomeContract.Presenter {

    override fun loaderData(pageNum: Int, pageSize: Int) {
        RequestManager.post(UrlConstants.deviceList)
                .param("companyCode", ConfigUtil.getCompanyCode())
                .param("username", UserUtil.getUserName())
                .param("pageNum", pageNum.toString())
                .param("pageSize", pageSize.toString())
                .execute("", object : ResultCallback<DeviceListResponseEntity>() {
                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.loaderCompleted()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let { mvpView?.showToast(it) }
                    }

                    override fun onSuccess(tag: Any?, result: DeviceListResponseEntity?) {
                        ConfigUtil.isExitNfc(false)
                        result!!.list!!.forEach {
                            if(it.icCardType=="02"){
                                ConfigUtil.isExitNfc(true)
                            }
                        }
                        mvpView?.loaderSuccess(result)
                    }
                })
    }

    override fun unBindDevice(position: Int, device: DeviceVM) {
        RequestManager.post(UrlConstants.unbind)
                .param("userName", UserUtil.getUserName())
                .param("companyCode", ConfigUtil.getCompanyCode())
                .param("meterCode", device.deviceCode())
                .execute("", object : ResultCallback<String>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.let {
                            ProgressUtil.showProgressDialog(it.getViewContext(), "加载中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.let {
                            ProgressUtil.dismissProgressDialog()
                        }
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let { mvpView?.showToast(it) }
                    }

                    override fun onSuccess(tag: Any?, result: String?) {
                        mvpView?.unbindSuccess(position, device)
                    }
                })
    }
}