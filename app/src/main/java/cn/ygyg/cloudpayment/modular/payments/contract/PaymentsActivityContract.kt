package cn.ygyg.cloudpayment.modular.payments.contract

import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.modular.payments.entity.CreateOrderResponseEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

class PaymentsActivityContract {
    interface View : IBaseView {
        fun onLoadDeviceSuccess(response: DeviceVM)
        fun onCreateOrderSuccess(response: CreateOrderResponseEntity)

        fun onLoadDeviceError(err: ApiThrowable)
        fun onCreateOrderError(err: ApiThrowable)
        fun finish()
    }

    interface Presenter : IBasePresenter<View> {
        fun getBindDevice(deviceCode: String, companyCode: String)
        /**
         * 创建支付订单
         * @param amount 支付金额
         * @param contractCode 物联网表编号
         * @param phone 手机号/账户名
         * @param paymentMethod 支付方式
         * @param payType 拉起支付的平台类型 APP
         *
         */
        fun createOrder(amount: String, contractCode: String, phone: String, meterClassification: String, paymentMethod: Constants.PaymentMethod, payType: String)
    }
}
