package cn.ygyg.cloudpayment.modular.nfc.presenter

import cn.ygyg.cloudpayment.modular.nfc.contract.NfcAffirmContract
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcWriteDataEntity
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.utils.NfcHeleper
import cn.ygyg.cloudpayment.utils.NfcUtil
import cn.ygyg.cloudpayment.utils.PosUtils
import cn.ygyg.cloudpayment.utils.StringUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import okhttp3.MediaType
import okhttp3.RequestBody

class NfcAffirmPresenter (view: NfcAffirmContract.View) : BasePresenterImpl<NfcAffirmContract.View>(view), NfcAffirmContract.Presenter {

    override fun sendOrder(data: ByteArray?) {
        val result = NfcHeleper.send(data)
        if(PosUtils.bcdToString(result)=="0000"||result==null){
            mvpView?.sendOrderError("请保持手机背面顶部靠近刷卡区\n系统重新充值")
        }else{
            mvpView?.sendOrderSuccess(NfcHeleper.orderCount-1,PosUtils.bcdToString(result))
        }
    }

    override fun loadWriteData(rechargeSeq: String) {
        var data: String = StringUtil.getWriteData(rechargeSeq)
        RequestManager.post(UrlConstants.nfcWriteEecAndVerify)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),data))
                .execute("", object : ResultCallback<NfcWriteDataEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onLoadDataError(e)
                    }

                    override fun onSuccess(tag: Any?, result: NfcWriteDataEntity?) {
                        mvpView?.onLoadDataSuccess(result!!)
                    }
                })

    }

    override fun loadPageData() {
        var data: String = StringUtil.getLoadPageData()
        RequestManager.post(UrlConstants.nfcRechargeQuery)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),data))
                .execute("", object : ResultCallback<NfcHistoryEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onLoadPageError(e)
                    }

                    override fun onSuccess(tag: Any?, result: NfcHistoryEntity?) {
                        if(result!!.list!!.size!=0){
                            mvpView?.onLoadPageSuccess(result.list!![0])
                        }else{
                            mvpView?.onLoadPageError(ApiThrowable(Throwable("数据获取异常，请联系客服"),"ER005","数据获取异常，请联系客服"))
                        }
                    }
                })
    }

    override fun updateStatus(data: String) {
        RequestManager.post(UrlConstants.updateStatus)
                .param("rechargeSeq",data)
                .param("cardState","1")
                .execute("", object : ResultCallback<String>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.showToast("写卡成功，网络异常，未成功上报")
                    }

                    override fun onSuccess(tag: Any?, result: String?) {
                        mvpView?.onUpdataSuccess(result)
                    }
                })
    }


}