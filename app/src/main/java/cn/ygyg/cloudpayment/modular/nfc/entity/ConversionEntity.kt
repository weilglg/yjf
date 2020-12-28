package cn.ygyg.cloudpayment.modular.nfc.entity

import java.io.Serializable

class ConversionEntity: Serializable{
    /**
     * amount : 7.66
     * gas : 200
     * realAmount : 7.64
     * prestored : 0.02
     * currentPrestored : 0.0
     * priceList : [{"level":0,"price":"0.01","gas":"","amount":"","ladder":"基准量","totalGas":""},{"level":1,"price":"0.03","gas":"34.00","amount":"1.02","ladder":"阶梯1","totalGas":"34.00"},{"level":2,"price":"0.04","gas":"166.00","amount":"6.64","ladder":"阶梯2","totalGas":"99999899"}]
     */
    var amount = 0.00
    var gas = 0
    var realAmount = 0.00
    var prestored = 0.0
    var currentPrestored = 0.0
    var priceList: List<PriceListBean>? = null

    class PriceListBean: Serializable{
        /**
         * level : 0
         * price : 0.01
         * gas :
         * amount :
         * ladder : 基准量
         * totalGas :
         */
        var level = 0
        var price: String? = null
        var gas: String? = null
        var amount: String? = null
        var ladder: String? = null
        var totalGas: String? = null
    }
}