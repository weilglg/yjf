package cn.ygyg.cloudpayment.modular.nfc.contract

import android.content.Context
import android.content.Intent
import cn.ygyg.cloudpayment.modular.nfc.entity.ReadDecResponseEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

class NfcHintActivityContract {

    interface View : IBaseView {
        fun sendOrderSuccess(orderCount:Int, request: String)
        fun sendOrderError()
        fun onFirstVerifyError(data: String, err: ApiThrowable)
        fun onSecondVerifyError(data: String, err: ApiThrowable)
        fun onInformationSuccess(result: ReadDecResponseEntity?)
        fun onInformationError(data: String, err: ApiThrowable)

    }

    interface Presenter : IBasePresenter<NfcHintActivityContract.View> {
        fun sendOrder(intent: Intent)
        fun sendOrder(data: ByteArray?)
        fun getFirstVerify(request: String?)
        fun getSecondVerify(request: String?)
        fun getContractInformation(byteString: String?)
    }
}
