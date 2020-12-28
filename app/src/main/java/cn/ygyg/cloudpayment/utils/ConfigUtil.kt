package cn.ygyg.cloudpayment.utils

import cn.ygyg.cloudpayment.BuildConfig
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.home.entity.CompanyEntity
import java.io.Serializable


object ConfigUtil {
    var userIsNfc : Boolean? = null
    private var configEntity: ConfigEntity? = null

    fun saveConfig(configEntity: ConfigEntity) {
        SharePreUtil.saveBeanByFastJson(Constants.IntentKey.COMPANY_KEY, configEntity)
        this.configEntity = configEntity
    }

    fun getConfigEntity(): ConfigEntity? {
        checkEmpty()
        return configEntity
    }

    fun getCompanyCode(): String {
        checkEmpty()
        return configEntity?.companyCode ?: BuildConfig.COMPANY_CODE
    }

    fun getCompanyName(): String {
        checkEmpty()
        return this.configEntity?.companyName ?: ""
    }

    private fun checkEmpty() {
        if (configEntity == null) {
            configEntity = SharePreUtil.getBeanByFastJson<ConfigEntity>(Constants.IntentKey.COMPANY_KEY)
        }
    }

    /**
     * 获取微信APPID
     */
    fun getWXAppId(): String {
        checkEmpty()
        val paymentDetails = configEntity?.paymentDetails
        val list = paymentDetails?.filter { it.paymentMethod == "Q" && it.paymentType == "APP" }
        list?.let {
            if (it.isNotEmpty()) {
                return it[0].appId ?: ""
            }
        }
        return Constants.DEF_WEI_XIN_APP_ID
    }

    /**
     * 获取支付宝APPID
     */
    fun getAlyPayAppId(): String {
        checkEmpty()
        val paymentDetails = configEntity?.paymentDetails
        val list = paymentDetails?.filter { it.paymentMethod == "A" && it.paymentType == "APP" }
        list?.let {
            if (it.isNotEmpty()) {
                return it[0].appId ?: ""
            }
        }
        return ""
    }

    fun getApplicationId(): String {
        checkEmpty()
        return configEntity?.applicationId ?: ""
    }

    fun isNotEmpty(): Boolean {
        checkEmpty()
        if (configEntity == null) {
            return false
        } else {
            val paymentDetails = configEntity?.paymentDetails
            if (paymentDetails != null && paymentDetails.size > 0) {
                return true
            }
        }
        return false
    }

    fun clear() {
        configEntity = null
    }

    fun setCompanyInfo(entity: CompanyEntity) {
        configEntity?.companyEntity = entity
        configEntity?.let {
            saveConfig(it)
        }
    }

    fun getCompanyInfo(): CompanyEntity? {
        checkEmpty()
        return configEntity?.companyEntity
    }

    fun isExitNfc(isExistNFC: Boolean) {
        configEntity!!.isExistNFC=isExistNFC
    }

    fun exitNfc(): Boolean? {
        return configEntity?.isExistNFC
    }

    fun userIsNfc(userIsNfc: Boolean) {
        this.userIsNfc=userIsNfc
    }

    fun isUserNfc(): Boolean? {
        return userIsNfc
    }
}

class ConfigEntity : Serializable {
    var companyCode: String? = null
    var appId: String? = null
    var applicationName: String? = null
    var applicationType: String? = null
    var applicationId: String? = null
    var companyId: String? = null
    var companyName: String? = null
    var groupName: String? = null
    var hotline: String? = null
    var paymentDetails: MutableList<PaymentEntity> = mutableListOf()
    var companyEntity: CompanyEntity? = null
    var isExistNFC : Boolean? = null


}

class PaymentEntity : Serializable {
    var appId: String? = null
    var paymentType: String? = null
    var paymentMethod: String? = null
}
