package cn.ygyg.cloudpayment.modular.internet.contract

import cn.ygyg.cloudpayment.modular.internet.entity.DeviceResponseEntity
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

class NewAccountActivityContract {
    interface View : IBaseView {
        /**
         * 查询 物联网表数据
         */
        fun onLoadDeviceSuccess(result: DeviceVM, deviceCode: String)

        /**
         * 绑定 物联网表
         */
        fun onBindDeviceSuccess(result: DeviceResponseEntity)

        /**
         * 绑定物联网表失败
         */
        fun onBindDeviceError(e: ApiThrowable)
    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 获取物联网表 信息
         */
        fun getDevice(deviceCode: String,companyCode:String)

        /**
         * 绑定物联网表
         */
        fun bindDevice(deviceCode: String,companyCode:String)


    }
}
