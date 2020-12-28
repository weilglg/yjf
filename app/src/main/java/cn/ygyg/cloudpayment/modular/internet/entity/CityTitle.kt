package cn.ygyg.cloudpayment.modular.internet.entity

import cn.ygyg.cloudpayment.modular.internet.vm.CityVM


class CityTitle : CityVM {

    var cityTitle: String? = null
    var cityPinyin: String? = null

    var isLocationCity = false

    override fun cityShowName(): String {
        return if (cityTitle.isNullOrEmpty()) "" else cityTitle!!
    }

    override fun cityPinyin(): String {
        return if (cityPinyin.isNullOrEmpty()) "" else cityPinyin!!
    }

    override fun initCityPinyin(pinyin: String) {
        this.cityPinyin = pinyin
    }

    override fun isRealCity(): Boolean {
        return false
    }

    override fun getViewType(): CityVM.ViewType {
        return if (isLocationCity) CityVM.ViewType.LOCATION else CityVM.ViewType.TITLE
    }

    override fun getCityId(): String {
        return ""
    }
}
