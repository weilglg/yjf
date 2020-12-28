package cn.ygyg.cloudpayment.modular.internet.vm

interface DeviceVM {

    fun userName(): String
    fun deviceCompany(): String
    fun contractCode(): String
    fun deviceCode(): String
    fun deviceAddress(): String
    fun deviceBalance(): String
    fun companyCode(): String
    fun meterClassification(): String

    fun geticCardType() : String

}