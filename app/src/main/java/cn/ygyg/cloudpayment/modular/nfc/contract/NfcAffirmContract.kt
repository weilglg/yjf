package cn.ygyg.cloudpayment.modular.nfc.contract

import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcWriteDataEntity
import cn.ygyg.cloudpayment.modular.payments.entity.HistoryPageResponseEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

class NfcAffirmContract {
    interface View : IBaseView {
        fun sendOrderSuccess(orderCount:Int, request: String?)
        fun sendOrderError(result: String)
        fun onLoadDataError(e: ApiThrowable)
        fun onLoadPageError(e: ApiThrowable)
        fun onLoadDataSuccess(result: NfcWriteDataEntity)
        fun onLoadPageSuccess(data: NfcHistoryEntity.ListBean)
        fun onUpdataSuccess(result: String?)
    }

    interface Presenter : IBasePresenter<NfcAffirmContract.View> {
        fun sendOrder(data: ByteArray?)
        fun loadWriteData(rechargeSeq: String)
        fun loadPageData()
        fun updateStatus(data:String)
    }
}