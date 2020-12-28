package cn.ygyg.cloudpayment.utils

import android.content.Context

object AppUtil {
    /**
     * 获取应用程序名称
     */
    fun getAppName(context: Context): String {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            return context.resources.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}