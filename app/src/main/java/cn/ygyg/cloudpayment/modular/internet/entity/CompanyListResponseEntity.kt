package cn.ygyg.cloudpayment.modular.internet.entity

import cn.ygyg.cloudpayment.modular.internet.vm.CompanyVM
import java.io.Serializable

class CompanyListResponseEntity : Serializable {
    var list: ArrayList<CompanyEntity>? = null

    open class CompanyEntity : CompanyVM, Serializable {
        var cityCode: String? = null
        var cityId: Int? = null
        var cityName: String? = null
        var companyCode: String? = null
        var companyName: String? = null
        var createDate: String? = null
        var createUser: String? = null
        var deleted: Boolean? = null
        var enabled: Boolean? = null
        var groupCode: String? = null
        var groupId: Int? = null
        var groupName: String? = null
        var id: Long? = null
        var modifiedDate: String? = null
        var modifiedUser: String? = null
        var remark: String? = null

        override fun companyName(): String {
            return if (companyName.isNullOrEmpty()) "" else companyName!!
        }

        override fun companyId(): Long {
            return if (id == null) 0L else id!!
        }
    }
}

