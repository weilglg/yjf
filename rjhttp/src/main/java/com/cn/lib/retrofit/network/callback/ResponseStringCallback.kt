package com.cn.lib.retrofit.network.callback


import okhttp3.ResponseBody

/**
 * 直接以字符串形式返回
 */
abstract class ResponseStringCallback : ResponseCallback<String>() {

    @Throws(Exception::class)
    override fun onTransformationResponse(body: ResponseBody): String {
        val jsonStr = ""
        try {
            body.string()
        } finally {
            body.close()
        }
        return jsonStr
    }

}
