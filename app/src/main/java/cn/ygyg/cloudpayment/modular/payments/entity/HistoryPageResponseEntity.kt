package cn.ygyg.cloudpayment.modular.payments.entity

import cn.ygyg.cloudpayment.modular.payments.vm.HistoryVM
import java.io.Serializable

class HistoryPageResponseEntity : Serializable {
    var isFirstPage = true
    var isLastPage = true
    var list: ArrayList<PageData>? = null

    class PageData : Serializable, HistoryVM {
        var companyCode: String? = null
        var createDate: String? = null
        var customerName: String? = null
        var enabled: String? = null
        var meterCode: String? = null
        var paymentMethodCN: String? = null
        var rechargedSum: Double? = null
        var paymentStatus: String? = null

        override fun userName(): String {
            return if (customerName.isNullOrEmpty()) "" else customerName!!
        }

        override fun payAmount(): String {
            return if (rechargedSum == null) "0.00元" else rechargedSum!!.toString() + "元"
        }

        override fun payState(): String {
            return if (paymentStatus.isNullOrEmpty()) "" else paymentStatus!!
        }

        override fun accountCode(): String {
            return if (meterCode.isNullOrEmpty()) "" else meterCode!!

        }

        override fun payMode(): String {
            return if (paymentMethodCN.isNullOrEmpty()) "其他" else paymentMethodCN!!
        }

        override fun payTime(): String {
            return if (createDate.isNullOrEmpty()) "" else createDate!!

        }
    }
}
