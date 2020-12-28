package com.cn.lib.retrofit.network.entity

import java.io.Serializable

open class ApiResultEntity<T>:Serializable {
    open var code: String = "-1"
    open var data: T? = null
    open var msg: String? = null

    open val isOk: Boolean
        get() = code == "0"
}
