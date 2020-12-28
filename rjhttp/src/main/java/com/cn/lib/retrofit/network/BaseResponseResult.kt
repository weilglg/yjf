package com.cn.lib.retrofit.network

class BaseResponseResult<T> {

    var code: String? = null
    var retCode: String? = null
    var rspCode: String? = null
    var error: String? = null
    var message: String? = null
    var msg: String? = null
    var retDesc: String? = null
    var rspDesc: String? = null
    var data: T? = null
    var result: T? = null
    var rspBody: T? = null
}
