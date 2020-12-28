package cn.ygyg.cloudpayment.modular.nfc.entity

import java.io.Serializable

class ReadDecResponseEntity: Serializable{
    /**
     * customerId : 411122
     * customerName : 阿凡达
     * credentialNo : 110101199003078718
     * credentialType : 01
     * houseId : 410142
     * houseAddress : 石家庄-锦绣花城-锦绣花城-A1栋-14-12
     * contractNo : 3000835674
     * contractId : 773528
     * realSteelGrade : fjc0005
     * validStatus : 1
     * balance : 0
     * createDate : 2019-12-05 17:34:19
     * modifiedDate : null
     * companyId : 91275
     * lockStatus : 0
     * createUser : fengjuncan321
     * recordChargeTime : 1
     * cardNo : 00000162
     * cardRemark : 1
     * ifUsedByCard : false
     * issueTime : null
     * prestored : 0.0
     * chargeTime : null
     * meterTypeDesc : JY卡表
     * meterTypeId : 885
     * meterId : 806941
     * meterReadResult1 : null
     * meterClassificationItem : 01
     * meterClassificationItemDes : JY卡表
     * operator : null
     * name : null
     * dllFileName : null
     * remark : 阿斯顿发达
     * cardHistoryAmount : null
     * balanceFlag : false
     * writeCardFlag : false
     * onMeterFlag : true
     * commands : null
     */
    var customerId: Int? = null
    var customerName: String? = null
    var credentialNo: String? = null
    var credentialType: String? = null
    var houseId: Int? = null
    var houseAddress: String? = null
    var contractNo: String? = null
    var contractId: Int? = null
    var realSteelGrade: String? = null
    var validStatus: Int? = null
    var balance: Int? = null
    var createDate: String? = null
    var modifiedDate: Any? = null
    var companyId: Int? = null
    var lockStatus: Int? = null
    var createUser: String? = null
    var recordChargeTime: Int? = null
    var cardNo: String? = null
    var cardRemark: String? = null
    var isIfUsedByCard = false
    var issueTime: Int? = 1
    var prestored = 0.0
    var chargeTime: Int? = 0
    var meterTypeDesc: String? = null
    var meterTypeId: Int? = null
    var meterId: Int? = null
    var meterReadResult1: Any? = null
    var meterClassificationItem: String? = null
    var meterClassificationItemDes: String? = null
    var operator: Any? = null
    var name: Any? = null
    var dllFileName: Any? = null
    var remark: String? = null
    var cardHistoryAmount: Any? = null
    var isBalanceFlag = false
    var isWriteCardFlag = false
    var isOnMeterFlag = false
    var commands: Any? = null
}