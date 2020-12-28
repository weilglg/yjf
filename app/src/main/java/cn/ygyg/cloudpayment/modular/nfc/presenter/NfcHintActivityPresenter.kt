package cn.ygyg.cloudpayment.modular.nfc.presenter

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.nfc.contract.NfcHintActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.ReadDecResponseEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.VerifyEntity
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.NfcHeleper
import cn.ygyg.cloudpayment.utils.PosUtils
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException

class NfcHintActivityPresenter (view: NfcHintActivityContract.View) : BasePresenterImpl<NfcHintActivityContract.View>(view), NfcHintActivityContract.Presenter {
    private var data: String? = null

    /**
     * 发送指令
     */
    override fun sendOrder(intent: Intent) {
        val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag
        for (tech in tag.techList) {
            if (tech == "android.nfc.tech.IsoDep") {
                var isodep = IsoDep.get(tag)
                NfcHeleper.initNfc(isodep)
                val result = NfcHeleper.next(null)
                if(PosUtils.bcdToString(result)=="0000"|| result==null) mvpView?.sendOrderError() else
                mvpView?.sendOrderSuccess(NfcHeleper.orderCount-1,PosUtils.bcdToString(result))
                return
            }
        }
        mvpView?.sendOrderError()
    }


    override fun sendOrder(data: ByteArray?) {
        if(NfcHeleper.orderCount!=8){
            val result = NfcHeleper.next(data)
            if(PosUtils.bcdToString(result)=="0000"|| result==null){
                mvpView?.sendOrderError()
            }else
                mvpView?.sendOrderSuccess(NfcHeleper.orderCount-1,PosUtils.bcdToString(result))
        }
    }

    override fun getFirstVerify(request: String?) {

        data=request
        RequestManager.post(UrlConstants.firstVerify)//  ResultPostRequest
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"{\"byteString\":\"$request\"}"))
                .execute("", object : ResultCallback<VerifyEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onFirstVerifyError("",e)
                    }

                    override fun onSuccess(tag: Any?, result: VerifyEntity?) {
                        result.let {
                            mvpView?.sendOrderSuccess(1001,result?.byteString+"")
                        }
                    }

                })
    }

    override fun getSecondVerify(request: String?) {
        RequestManager.post(UrlConstants.secondVerify)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"{\"byteString\":\"$request\"}"))
                .execute("", object : ResultCallback<VerifyEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        data?.let { mvpView?.onSecondVerifyError(it,e) }
                    }

                    override fun onSuccess(tag: Any?, result: VerifyEntity?) {
                        result.let {
                            mvpView?.sendOrderSuccess(1002,result?.byteString+"")
                        }
                    }

                })
    }

    /**
     *  g根据 MZ区的数据换取合约信息
     */
    override fun getContractInformation(byteString: String?) {
        val groupId = ConfigUtil.getConfigEntity()!!.companyEntity!!.groupId
        val companyId = ConfigUtil.getConfigEntity()!!.companyId
        RequestManager.post(UrlConstants.case2ReadDec)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        "{\"secretflag\":\"${Constants.OrderKey.isBefore}\",\"companyId\":\"$companyId\",\"groupId\":\"$groupId\",\"byteString\":\"$byteString\"}"))
                .execute("", object : ResultCallback<ReadDecResponseEntity>() {
                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        data?.let { mvpView?.onInformationError("",e)
                        }
                    }

                    override fun onSuccess(tag: Any?, result: ReadDecResponseEntity?) {
                        result.let {
                            mvpView?.onInformationSuccess(result!!)
                        }
                    }
                })
    }
}