package cn.ygyg.cloudpayment.acp

/**
 * 回调
 */
interface AcpListener {
    /**
     * 同意
     */
    fun onGranted()

    /**
     * 拒绝
     */
    fun onDenied(permissions: List<String>)
}
