package com.cn.lib.retrofit.network.entity

class CommResultEntity<T> : ApiResultEntity<T>() {

    var retDesc: String? = null
    var retCode: String = "-1"
    var rspBody: T? = null

    override val isOk: Boolean
        get() = retCode == "0"

    override var code: String = "-1"
        get() = retCode

    override var data: T? = null
        get() = rspBody

    override var msg: String? = null
        get() = retDesc

}
