package com.cn.lib.retrofit.network.exception

class ServerException : RuntimeException {
    var code: String = "-2"
    var msg: String? = null

    constructor() {}

    constructor(code: String) {
        this.code = code
    }

    constructor(code: String, message: String?) : super(message) {
        this.code = code
        this.msg = message
    }
}
