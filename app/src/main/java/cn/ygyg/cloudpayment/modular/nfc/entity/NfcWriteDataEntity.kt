package cn.ygyg.cloudpayment.modular.nfc.entity

import java.io.Serializable

class NfcWriteDataEntity : Serializable {
    var customerId: String? = null
    var customerName: String? = null
    var credentialNo: String? = null
    var credentialType: String? = null
    var houseId: String? = null
    var houseAddress: String? = null
    var contractNo: String? = null
    var contractId: String? = null
    var realSteelGrade: String? = null
    var validStatus: String? = null
    var balance: String? = null
    var createDate: String? = null
    var modifiedDate: String? = null
    var companyId: String? = null
    var groupId: String? = null
    var lockStatus: String? = null
    var createUser: String? = null
    var recordChargeTime: String? = null
    var cardNo: String? = null
    var cardRemark: String? = null
    var isIfUsedByCard = false
    var issueTime: String? = null
    var prestored: String? = null
    var chargeTime: String? = null
    var meterTypeDesc: String? = null
    var meterTypeId: String? = null
    var meterId: String? = null
    var meterReadResult1: String? = null
    var meterClassificationItem: String? = null
    var meterClassificationItemDes: String? = null
    var operator: String? = null
    var name: String? = null
    var dllFileName: String? = null
    var remark: String? = null
    var cardHistoryAmount: String? = null
    var balanceFlag: String? = null
    var writeCardFlag: String? = null
    var onMeterFlag: String? = null
    var pszKeyInst: String? = null
    var pszGasInst: String? = null

}