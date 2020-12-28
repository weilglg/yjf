package cn.ygyg.cloudpayment.modular.payments.vm

interface HistoryVM {
    fun userName():String
    fun payAmount():String
    fun payState():String
    fun accountCode():String
    fun payMode():String
    fun payTime():String
}