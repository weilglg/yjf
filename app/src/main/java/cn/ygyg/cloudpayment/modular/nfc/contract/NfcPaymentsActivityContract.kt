package cn.ygyg.cloudpayment.modular.nfc.contract

import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.modular.nfc.entity.ConversionEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcPaymentEntity
import cn.ygyg.cloudpayment.modular.payments.entity.CreateOrderResponseEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

class NfcPaymentsActivityContract {

    interface View : IBaseView {
        fun getCalculateSuccsee(result: ConversionEntity)
        fun getCalculateError(e: ApiThrowable)
        fun onCreateOrderSuccsee(result: NfcPaymentEntity)
        fun onCreateOrderError(e: ApiThrowable)
    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 创建支付订单
         * @param amount 支付金额
         * @param contractCode 物联网表编号
         * @param phone 手机号/账户名
         * @param paymentMethod 支付方式
         * @param payType 拉起支付的平台类型 APP
         *
         */
        fun createOrder(buyGasMoney: String, buyGasSize:String, cardNo: String, cardRemark: String, chargeTime: String, contractCode: String, contractId: String,
                        currentBalance: String, issueTime: String, payWayType: String, preDepositBalance: String, realMoneyReceive: String, companyCode: String)

        fun getCalculate(cardNo: String? , cardRemark: String? , chargeTime: String? , chargeGas: String? ,
                         issueTime: String? ,contractId: String? , ifAmount: Boolean , prestored: String?)
    }
}