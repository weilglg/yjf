package cn.ygyg.cloudpayment.modular.nfc.presenter

import cn.ygyg.cloudpayment.modular.nfc.contract.NfcPaymentsHistoryActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.utils.ProgressUtil
import cn.ygyg.cloudpayment.utils.StringUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import okhttp3.MediaType
import okhttp3.RequestBody

class NfcPaymentsHistoryPresenter(view: NfcPaymentsHistoryActivityContract.View) :
        BasePresenterImpl<NfcPaymentsHistoryActivityContract.View>(view),
        NfcPaymentsHistoryActivityContract.Presenter {
    override fun loadPage(pageNum: Int, pageSize: Int) {
        val data=StringUtil.getNfcHistoryData(pageNum,pageSize)
        RequestManager.post(UrlConstants.nfcRechargeQuery)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),data))
                .execute("", object : ResultCallback<NfcHistoryEntity>() {
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

                    override fun onSuccess(tag: Any?, result: NfcHistoryEntity?) {
                        result?.let {
                            mvpView?.onLoadSuccess(result)
                        }
                    }

                })
    }
}