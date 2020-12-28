package cn.ygyg.cloudpayment.modular.home.entity

import cn.ygyg.cloudpayment.modular.internet.entity.DeviceResponseEntity
import java.io.Serializable

class DeviceListResponseEntity : Serializable {


    var total: Int = 0
    var list: ArrayList<DeviceResponseEntity>? = null

    var pageNum: Int = 0
    var pageSize: Int = 0
    var size: Int = 0
    var startRow: Int = 0
    var endRow: Int = 0
    var pages: Int = 0
    var prePage: Int = 0
    var nextPage: Int = 0
    var isFirstPage: Boolean = false
    var isLastPage: Boolean = false
    var hasPreviousPage: Boolean = false
    var hasNextPage: Boolean = false
    var navigatePages: Int = 0
    var navigateFirstPage: Int = 0
    var navigateLastPage: Int = 0

}
