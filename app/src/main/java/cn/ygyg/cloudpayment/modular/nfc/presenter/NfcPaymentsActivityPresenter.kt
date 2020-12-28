package cn.ygyg.cloudpayment.modular.nfc.presenter

import cn.ygyg.cloudpayment.modular.nfc.contract.NfcPaymentsActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.ConversionEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcPaymentEntity
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import okhttp3.MediaType
import okhttp3.RequestBody

class NfcPaymentsActivityPresenter(view: NfcPaymentsActivityContract.View) :
        BasePresenterImpl<NfcPaymentsActivityContract.View>(view),
        NfcPaymentsActivityContract.Presenter {

    override fun createOrder(buyGasMoney: String, buyGasSize:String, cardNo: String, cardRemark: String, chargeTime: String, contractCode: String, contractId: String,
                             currentBalance: String, issueTime: String, payWayType: String, preDepositBalance: String, realMoneyReceive: String, companyCode: String){
        RequestManager.post(UrlConstants.nfcPayments)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"{\n" +
                        "\t \"buyGasMoney\":$buyGasMoney,\n" +
                        "    \"buyGasSize\": \"$buyGasSize\",\n" +
                        "    \"cardNo\": \"$cardNo\",\n" +
                        "    \"cardRemark\": \"$cardRemark\",\n" +
                        "    \"chargeTime\": $chargeTime,\n" +
                        "    \"contractCode\": \"$contractCode\",\n" +
                        "    \"contractId\": $contractId,\n" +
                        "    \"currentBalance\": $currentBalance,\n" +
                        "    \"issueTime\": $issueTime,\n" +
                        "    \"payWayType\": \"$payWayType\",\n" +
                        "    \"preDepositBalance\": $preDepositBalance,\n" +
                        "    \"realMoneyReceive\": $realMoneyReceive,\n" +
                        "    \"createUser\": \"${UserUtil.getUserName()}\",\n" +
                        "    \"companyCode\": \"$companyCode\"\n" +
                        "}"))
                .execute("", object : ResultCallback<NfcPaymentEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.onCreateOrderError(e)
                    }

                    override fun onSuccess(tag: Any?, result: NfcPaymentEntity?) {
                        mvpView?.onCreateOrderSuccsee(result!!)
                    }
                })
    }

    override fun getCalculate(cardNo: String? , cardRemark: String? , chargeTime: String? , chargeGas: String? ,issueTime: String? ,contractId: String? , ifAmount: Boolean , prestored: String?) {
        RequestManager.get(UrlConstants.calculate)
                .param("cardNo", cardNo!!)
                .param("cardRemark",cardRemark!!)
                .param("chargeTime", chargeTime!!)
                .param("chargeGas", chargeGas!!)
                .param("issueTime",issueTime!!)
                .param("contractId", contractId!!)
                .param("ifAmount", ifAmount.toString())
                .param("prestored", prestored!!)
                .param("companyCode", ConfigUtil.getCompanyCode())
                .param("companyId",  ConfigUtil.getConfigEntity()!!.companyId.toString())
                .execute("", object : ResultCallback<ConversionEntity>() {
                    override fun onStart(tag: Any?) {
                    }

                    override fun onCompleted(tag: Any?) {
//                        mvpView?.loaderCompleted()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        mvpView?.getCalculateError(e)
                    }

                    override fun onSuccess(tag: Any?, result: ConversionEntity?) {
                        mvpView?.getCalculateSuccsee(result!!)
                    }
                })
    }

}