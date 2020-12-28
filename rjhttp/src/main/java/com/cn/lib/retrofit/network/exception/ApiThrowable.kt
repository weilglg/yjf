package com.cn.lib.retrofit.network.exception

class ApiThrowable : Exception {
    var code: String = "-100"
     override var message: String? = null

    constructor(throwable: Throwable, code: String) : super(throwable) {
        this.code = code
    }

    constructor(throwable: Throwable, code: String, message: String) : super(throwable) {
        this.code = code
        this.message = message
    }
}
