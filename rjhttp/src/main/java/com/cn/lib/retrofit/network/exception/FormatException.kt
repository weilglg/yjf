package com.cn.lib.retrofit.network.exception

class FormatException : Exception() {
    var code = "-200"
    override var message = "服务器返回数据格式异常"
}
