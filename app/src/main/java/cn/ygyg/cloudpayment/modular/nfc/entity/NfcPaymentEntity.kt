package cn.ygyg.cloudpayment.modular.nfc.entity

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class NfcPaymentEntity : Serializable {

    var id = 0
    var rechargeSeq: String? = null
    var allinoneTradeNo: Any? = null
    var realSteelGrade: String? = null
    var customerName: String? = null
    var customerTypeCode: String? = null
    var customerTypeName: String? = null
    var cellPhone: String? = null
    var houseAddress: String? = null
    var tradeClass: String? = null
    var buyGasSize: String? = null
    var buyGasMoney: String? = null
    var realMoneyReceive: String? = null
    var preDepositBalance: String? = null
    var priceDetail: String? = null
    var paymentMethod: String? = null
    var operationFrom: String? = null
    var contractCode: String? = null
    var parentId: Any? = null
    var createDate: String? = null
    var createUser: String? = null
    var syncRecharge = 0
    var state: String? = null
    var cardNo: String? = null
    var cardRemark: String? = null
    var cardState = 0
    var groupName: Any? = null
    var groupId = 0
    var companyName: Any? = null
    var companyId = 0
    var companyCode: String? = null
    var payOrderNo: String? = null
    var contractId = 0
    var issueTime = 0
    var chargeTime = 0
    var operateMachineNo: Any? = null
    var updateDate: Any? = null
    var meterTypeCode: Any? = null
    var meterTypeId = 0
    var communityId = 0
    var refundNo: Any? = null
    var currentBalance = 0
    var departmentId: Any? = null
    var departmentName: Any? = null
    var departmentCode: Any? = null
    var tradeClassCN: Any? = null
    var paymentMethodCN: Any? = null
    var operationFromCN: Any? = null
    var parentPayOrderNo: Any? = null
    var openCard: Any? = null
    var meterId: Any? = null
    var evidence: Any? = null
    var payWayType: String? = null
    var pageNum: Any? = null
    var pageSize: Any? = null
    var startTime: Any? = null
    var endTime: Any? = null
    var tradeClassDesc: String? = null
    var paymentMethodDesc: Any? = null
    var meterTypeName: Any? = null
    var checkState: Any? = null
    var printingCode: Any? = null
    var cardType: Any? = null
    var cardTypeDesc: Any? = null
    var meterTypeDesc: Any? = null
    var condition: Any? = null
    var globalDTOResquest: Any? = null
    var contractInfo: Any? = null
    var name: Any? = null
    var realMoneyReceiveDesc: String? = null
    var cardHistoryAmount: Any? = null
    var ifCardPayment: Any? = null
    var tradeClassList: Any? = null
    var makeCardNo: Any? = null
    var parentRechargeSeq: Any? = null
    var icCardType: Any? = null
    var contractIctypeNo: Any? = null
    var alipayAppRes: String? = null
    var payRespInfoDTO: Any? = null
    var weChatAppRes: WeChatResBean? = null
    class WeChatResBean {
        @JSONField(name="package")
        var packageX: String? = null

        var appid: String? = null
        var sign: String? = null
        var partnerid: String? = null
        var prepayid: String? = null
        var noncestr: String? = null
        var timestamp: Int = 0
    }
    var priceList: List<PriceListBean>? = null

    class PriceListBean: Serializable {
        /**
         * level : 0
         * price : 1.00
         * gas : 10.00
         * amount : 10.00
         * ladder : 基准量
         * totalGas : null
         */
        var level = 0
        var price: String? = null
        var gas: String? = null
        var amount: String? = null
        var ladder: String? = null
        var totalGas: Any? = null

    }
}