package cn.ygyg.cloudpayment.modular.login.entity

import java.io.Serializable

/**
 * Created by Admin on 2019/4/23.
 *  "mid": 6,
"cellPhone": "15001030281",
"userPassword": null,
"nickName": null,
"userName": null,
"gender": null,
"identityCard": null,
"email": null,
"address": null,
"openId": null,
"appId": null,
"validStatus": null,
"deleted": null,
"signInNum": null,
"createUser": null,
"postDate": "2019-04-19",
"updateUser": null,
"updateDate": null,
"token": null

 */
 class UserEntity : Serializable {
    var cellPhone: String? =null
    var mid: String? =null
    var userPassword: String? =null
    var nickName: String? =null
    var userName: String? =null
    var gender: String? =null
    var identityCard: String? =null
    var email: String? =null
    var address: String? =null
    var appId: String? =null
    var validStatus: String? =null
    var signInNum: String? =null
    var deleted: String? =null
    var createUser: String? =null
    var openId: String? =null
    var postDate: String? =null
    var updateUser: String? =null
    var token: String? =null
}