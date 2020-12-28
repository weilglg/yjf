package com.cn.lib.retrofit.network.util

import com.cn.lib.retrofit.network.entity.ApiResultEntity

class TestApi<T> : ApiResultEntity<T>() {

    var results: T? = null
    var status: String = "0"

    override val isOk: Boolean
        get() = status == "0"

    override var code: String = "-1"
        get() = status

    override var data: T? = null
        get() = results

}
