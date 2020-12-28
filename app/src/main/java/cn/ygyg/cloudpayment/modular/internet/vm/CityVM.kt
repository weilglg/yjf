package cn.ygyg.cloudpayment.modular.internet.vm

interface CityVM {
    enum class ViewType {
        CITY,
        TITLE,
        LOCATION
    }


    fun cityShowName(): String

    fun cityPinyin(): String

    fun initCityPinyin(pinyin: String)

    fun isRealCity(): Boolean

    fun getViewType(): ViewType

    fun getCityId(): String
}