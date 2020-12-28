package cn.ygyg.cloudpayment.modular.login.entity

import java.io.Serializable

class  TokenEntity:Serializable {
    var access_token: String? =null
    var refresh_token: String? =null
    var unionid: String? =null
    var scope: String? =null
    var expires_in: String? =null
    var openid: String? =null
}