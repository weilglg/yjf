package cn.ygyg.cloudpayment.modular.internet.entity

import cn.ygyg.cloudpayment.modular.internet.vm.CityVM
import java.io.Serializable

class CityListResponseEntity : Serializable {
    var total: Int? = 0
    var list: ArrayList<CityEntity>? = null

    open class CityEntity : CityVM, Serializable {
        var cityPinyin: String? = null

        var id: Long? = null
        var cityCode: String? = null
        var cityName: String? = null
        var remark: String? = null
        var createUser: String? = null
        var createDate: String? = null
        var modifiedUser: String? = null
        var modifiedDate: String? = null
        var deleted: Boolean? = null

        override fun cityShowName(): String {
            return if (cityName.isNullOrEmpty()) "" else cityName!!
        }

        override fun cityPinyin(): String {
            return if (cityPinyin.isNullOrEmpty()) "" else cityPinyin!!
        }

        override fun initCityPinyin(pinyin: String) {
            this.cityPinyin = pinyin
        }

        override fun isRealCity(): Boolean {
            return true
        }

        override fun getViewType(): CityVM.ViewType = CityVM.ViewType.CITY
        override fun getCityId(): String {
            return if (id == null) "" else id.toString()
        }
    }
}