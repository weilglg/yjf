package cn.ygyg.cloudpayment.modular.nfc.contract

import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

class NfcPaymentsHistoryActivityContract {
    interface View : IBaseView {
        /**
         * 加载结束
         */
        fun onLoadCompleted()

        fun onLoadSuccess(result: NfcHistoryEntity?)

    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 加载指定页面
         */
        fun loadPage(pageNum: Int, pageSize: Int)
    }
}