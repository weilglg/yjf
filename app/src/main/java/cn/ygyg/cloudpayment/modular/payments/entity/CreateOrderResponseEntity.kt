package cn.ygyg.cloudpayment.modular.payments.entity

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class CreateOrderResponseEntity : Serializable {
    var uid: Int = 0
    var unitPrice: Any? = null
    var isEnabled: Boolean = false
    var createDateStr: Any? = null
    var operationFromCN: Any? = null
    var tradeClassCN: Any? = null
    var paymentMethodCN: Any? = null
    var payStatus: Any? = null
    var paymentTypeCN: Any? = null
    var isSyncAllinone: Boolean = false
    var houseAddress: String? = null
    var applicationId: Any? = null
    var rechargedSumTotal: Any? = null
    var paramName: Any? = null
    var groupId: Any? = null
    var createDateString: Any? = null
    var modifiedDateString: Any? = null
    var orgRechargeSeq: Any? = null
    var parentSumStr: Any? = null
    var rechargedSumStr: Any? = null
    var lastSumStr: Any? = null
    var rechargedSumChineseStr: String? = null

    var allinoneTradeNo: String? = null
    var amount: Double = 0.0
    var balance: String? = null
    var communityCode: String? = null
    var communityId: String? = null
    var companyCode: String? = null
    var companyId: String? = null
    var companyName: String? = null
    var contractCode: String? = null
    var contractId: Int = 0
    var createDate: String? = null
    var createUser: String? = null
    var customerCode: String? = null
    var customerCredentialNo: String? = null
    var customerId: String? = null
    var customerMobile: String? = null
    var customerName: String? = null
    var customerTel: String? = null
    var customerTypeCode: String? = null
    var customerTypeId: String? = null
    var customerTypeName: String? = null
    var departmentCode: String? = null
    var enabled: String? = null
    var hostName: String? = null
    var housePropertyCode: String? = null
    var housePropertyName: String? = null
    var invoiceNo: String? = null
    var lastSum: String? = null
    var memberIdentifier: String? = null
    var meterCode: String? = null
    var meterGas: String? = null
    var meterId: String? = null
    var meterNo: String? = null
    var meterType: String? = null
    var metersTypeCode: String? = null
    var metersTypeId: String? = null
    var modifiedDate: String? = null
    var modifiedUser: String? = null
    var openId: String? = null
    var operationFrom: String? = null
    var orderBalance: String? = null
    var orderCode: String? = null
    var orderSeq: String? = null
    var orderStatus: String? = null
    var parentId: String? = null
    var parentSum: String? = null
    var paymentMethod: String? = null
    var paymentSeq: String? = null
    var paymentStatus: String? = null
    var paymentType: String? = null
    var porderSeq: String? = null
    var ppaymentSeq: String? = null
    var premiseCode: String? = null
    var premiseName: String? = null
    var ptradeClass: String? = null
    var rechargeSeq: String? = null
    var rechargeTradeId: String? = null
    var rechargedSum: String? = null
    var refundAmount: String? = null
    var refundReason: String? = null
    var remark: String? = null
    var settleDate: String? = null
    var spbillCreateIp: String? = null
    var syncAllinone: String? = null
    var syncRecharge: String? = null
    var syncStatus: String? = null
    var thirdPartyAuthCode: String? = null
    var thirdPartyPaymentSeq: String? = null
    var tradeClass: String? = null
    var tradeType: String? = null
    var usedGas: String? = null
    var alipayAppRes: String? = null
    var weChatRes: WeChatResBean? = null
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

}
