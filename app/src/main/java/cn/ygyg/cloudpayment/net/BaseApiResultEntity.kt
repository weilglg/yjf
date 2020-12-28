package cn.ygyg.cloudpayment.net

import com.cn.lib.retrofit.network.entity.ApiResultEntity

/**
 * Created by Admin on 2019/4/18.
 */
class BaseApiResultEntity<T> : ApiResultEntity<T>() {

    override var code: String = "-1"
        get() = retCode
    override var data: T? = null
        set(value) {
            field = value
            result = value
        }
        get() = result
    override var msg: String? = ""
        get() = retMsg

    override val isOk: Boolean
        get() = "000000" == code

    var retCode: String = "-1"
    var retMsg: String? =null
    var result: T? = null
}
