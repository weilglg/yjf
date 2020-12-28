package cn.ygyg.cloudpayment.modular.payments.presenter

import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.modular.payments.contract.PaymentsHistoryActivityContract
import cn.ygyg.cloudpayment.modular.payments.entity.HistoryPageResponseEntity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.ProgressUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable

class PaymentsHistoryActivityPresenter(view: PaymentsHistoryActivityContract.View) :
        BasePresenterImpl<PaymentsHistoryActivityContract.View>(view),
        PaymentsHistoryActivityContract.Presenter {
    override fun loadPage(pageNum: Int, pageSize: Int) {
        RequestManager.post(UrlConstants.rechargeQuery)
                .param("mobile", UserUtil.getUserName())
                .param("companyCode", ConfigUtil.getCompanyCode())
                .param("pageNum", pageNum.toString())
                .param("pageSize", pageSize.toString())
                .execute("", object : ResultCallback<HistoryPageResponseEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.let {
                            ProgressUtil.showProgressDialog(it.getViewContext(), "加载中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        ProgressUtil.dismissProgressDialog()
                        mvpView?.onLoadCompleted()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                    }

                    override fun onSuccess(tag: Any?, result: HistoryPageResponseEntity?) {
                        result?.let {
                            mvpView?.onLoadSuccess(result)
                        }
                    }

                })
    }
}