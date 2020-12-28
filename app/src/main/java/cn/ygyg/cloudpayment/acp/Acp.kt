package cn.ygyg.cloudpayment.acp

import android.content.Context

/**
 * 动态权限检查
 */
class Acp private constructor(context: Context) {
    internal val acpManager: AcpManager

    init {
        acpManager = AcpManager(context.applicationContext)
    }

    /**
     * 开始请求
     *
     * @param options     权限检查配置
     * @param acpListener 检查结果回调
     */
    fun request(options: AcpOptions?, acpListener: AcpListener?) {
        if (options == null) throw NullPointerException("AcpOptions is null...")
        if (acpListener == null) throw NullPointerException("AcpListener is null...")
        acpManager.request(options, acpListener)
    }

    companion object {

        private var mInstance: Acp? = null

        fun getInstance(context: Context): Acp {
            if (mInstance == null)
                synchronized(Acp::class.java) {
                    if (mInstance == null) {
                        mInstance = Acp(context)
                    }
                }
            return mInstance!!
        }
    }
}
