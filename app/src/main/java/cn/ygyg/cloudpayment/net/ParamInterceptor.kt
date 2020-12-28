package cn.ygyg.cloudpayment.net

import cn.ygyg.cloudpayment.BuildConfig
import cn.ygyg.cloudpayment.utils.RSAUtils
import com.cn.lib.retrofit.network.entity.HttpParamEntity
import com.cn.lib.retrofit.network.interceptor.RequestParamInterceptor

class ParamInterceptor : RequestParamInterceptor {

    private val publicKey = BuildConfig.PUBLIC_KEY
    override fun intercept(params: HttpParamEntity): HttpParamEntity {
        if (!params.isParamsEmpty()) {
            val paramMap = params.paramMap
            for (mutableEntry in paramMap) {
                val value = mutableEntry.value
                val key = mutableEntry.key
                paramMap[key] = encrypt(value, publicKey)
            }
        }
        return params
    }

    /**
     * RSA公钥加密
     *
     * @param str 加密字符串
     * @param publicKey 公钥
     */
    private fun encrypt(str: String, publicKey: String): String {
        return RSAUtils.encryptByPublicKey(str, RSAUtils.loadPublicKey(publicKey))
    }

}