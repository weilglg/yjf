package cn.ygyg.cloudpayment.modular.home.entity

import java.io.Serializable

class ConfigEntity : Serializable {

    //    var applicationId: String? = null
//    var applicationName: String? = null
//    var applicationType: String? = null
//    var companyCode: String? = null
//    var companyId: String? = null
//    var companyName: String? = null
//    var groupCode: String? = null
//    var groupId: Int = 0
//    var groupName: String? = null
//    var paymentDetails: MutableList<PaymentEntity> = mutableListOf()
//
//    var appId: String? = null
//    var hotline: String? = null
    var companyEntity: CompanyEntity? = null
//}

//class PaymentEntity : Serializable {
//    var appId: String? = null
//    var paymentType: String? = null
//    var paymentMethod: String? = null
//

    var id: Int = 0
    var companyId: Any? = null
    var callbackDomain: String? = null
    var partner: Any? = null
    var paymentMethod: String? = null
    var callbackPort: String? = null
    var enabled: Any? = null
    var remark: Any? = null
    var createUser: String? = null
    var createDate: String? = null
    var modifiedDate: Any? = null
    var alipayReturnAddress: Any? = null
    var appId: String? = null
    var appSecret: String? = null
    var commercialCode: String? = null
    var partnerKey: String? = null
    var paymentType: String? = null
    var modifiedUser: Any? = null
    var subject: String? = null
    var sellerId: Any? = null
    var companyName: String? = null
    var certificate1Path: String? = null
    var messageDecryptionKey: String? = null
    var applicationId: String? = null
    var applicationName: Any? = null
    var companyCode: String? = null
    var apiSecret: Any? = null
    var decryptionKey: Any? = null
    var pageNum: Int = 0
    var pageSize: Int = 0
}
