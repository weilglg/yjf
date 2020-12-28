package cn.ygyg.cloudpayment.modular.payments.contract

import cn.ygyg.cloudpayment.modular.payments.entity.HistoryPageResponseEntity
import cn.ygyg.cloudpayment.modular.payments.vm.HistoryVM
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

class PaymentsHistoryActivityContract {
    interface View : IBaseView {
        /**
         * 加载结束
         */
        fun onLoadCompleted()

        fun onLoadSuccess(result: HistoryPageResponseEntity?)

    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 加载指定页面
         */
        fun loadPage(pageNum: Int, pageSize: Int)
    }
}