package cn.ygyg.cloudpayment.modular.nfc.entity

import java.io.Serializable

class NfcHistoryEntity : Serializable{
    var total = 0
    var pageNum = 0
    var pageSize = 0
    var size = 0
    var startRow = 0
    var endRow = 0
    var pages = 0
    var prePage = 0
    var nextPage = 0
    var isFirstPage = false
    var isLastPage = false
    var isHasPreviousPage = false
    var isHasNextPage = false
    var navigatePages = 0
    var navigateFirstPage = 0
    var navigateLastPage = 0
    var list: ArrayList<ListBean>? = null
    var navigatepageNums: ArrayList<Int>? = null

    class ListBean: Serializable {
        /**
         * id : 1322
         * rechargeSeq : 20191212190634106330
         * allinoneTradeNo : null
         * realSteelGrade : fjc0005
         * customerName : 阿凡达
         * customerTypeCode : 10000051
         * customerTypeName : 民用户
         * cellPhone : 15702154854
         * houseAddress : 石家庄-锦绣花城-锦绣花城-A1栋-14-12
         * tradeClass : T1
         * buyGasSize : 10.00
         * buyGasMoney : 0.10
         * realMoneyReceive : 0.10
         * preDepositBalance : 0.00
         * priceDetail : [{"amount":"0.10","gas":"10.00","ladder":"基准量","level":0,"price":"0.01"}]
         * paymentMethod : Q
         * operationFrom : A
         * contractCode : 3000835674
         * parentId : null
         * createDate : 2019-12-12 19:06:35
         * createUser : 15711059504
         * syncRecharge : 0
         * state : 11
         * cardNo : 00000162
         * cardRemark : 1
         * cardState : 0
         * groupName : null
         * groupId : 110
         * companyName : null
         * companyId : 91275
         * companyCode : 321
         * payOrderNo : 16761882429882458094
         * contractId : 773528
         * issueTime : 5
         * chargeTime : 4
         * operateMachineNo : null
         * updateDate : 2019-12-12 19:06:50
         * meterTypeCode : null
         * meterTypeId : 885
         * communityId : 1866
         * refundNo : null
         * currentBalance : 0.0
         * departmentId : null
         * departmentName : null
         * departmentCode : null
         * tradeClassCN : null
         * paymentMethodCN : null
         * operationFromCN : null
         * parentPayOrderNo : null
         * openCard : null
         * meterId : null
         * evidence : null
         * payWayType : null
         * pageNum : null
         * pageSize : null
         * startTime : null
         * endTime : null
         * tradeClassDesc : 购气
         * paymentMethodDesc : 支付宝
         * meterTypeName : JY卡表
         * checkState : null
         * printingCode : null
         * priceList : [{"level":0,"price":"0.01","gas":"10.00","amount":"0.10","ladder":"基准量","totalGas":null}]
         * cardType : null
         * cardTypeDesc : null
         * meterTypeDesc : null
         * condition : null
         * globalDTOResquest : null
         * contractInfo : null
         * name : null
         * realMoneyReceiveDesc : 零元壹角
         * cardHistoryAmount : 1.41
         * ifCardPayment : null
         * tradeClassList : null
         * makeCardNo : null
         * parentRechargeSeq : null
         * icCardType : null
         * contractIctypeNo : null
         * alipayAppRes : null
         * payRespInfoDTO : null
         * weChatAppRes : null
         */
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
        var cardState: String? = null
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
        var updateDate: String? = null
        var meterTypeCode: Any? = null
        var meterTypeId = 0
        var communityId = 0
        var refundNo: Any? = null
        var currentBalance = 0.0
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
        var payWayType: Any? = null
        var pageNum: Any? = null
        var pageSize: Any? = null
        var startTime: Any? = null
        var endTime: Any? = null
        var tradeClassDesc: String? = null
        var paymentMethodDesc: String? = null
        var meterTypeName: String? = null
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
        var cardHistoryAmount = 0.0
        var ifCardPayment: Any? = null
        var tradeClassList: Any? = null
        var makeCardNo: Any? = null
        var parentRechargeSeq: Any? = null
        var icCardType: Any? = null
        var contractIctypeNo: Any? = null
        var alipayAppRes: Any? = null
        var payRespInfoDTO: Any? = null
        var weChatAppRes: Any? = null
        var priceList: List<PriceListBean>? = null

        class PriceListBean: Serializable {
            /**
             * level : 0
             * price : 0.01
             * gas : 10.00
             * amount : 0.10
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
}