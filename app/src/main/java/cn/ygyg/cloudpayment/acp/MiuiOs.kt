package cn.ygyg.cloudpayment.acp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 参考 http://blog.csdn.net/tgbus18990140382/article/details/47058043
 * 适配兼容
 */
object MiuiOs {
    private const val UNKNOWN = "UNKNOWN"

    /**
     * 获取 V5/V6 后面的数字
     *
     * @return 取 V5/V6 后面的数字
     */
    private val systemVersionCode: Int
        get() {
            val systemProperty = systemProperty
            if (!TextUtils.isEmpty(systemProperty) && systemProperty != UNKNOWN
                    && systemProperty.length == 2 && systemProperty.toUpperCase().startsWith("V")) {
                var code: Int? = 0
                try {
                    code = Integer.valueOf(systemProperty.substring(1))
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                return code!!
            }
            return 0
        }

    /**
     * 判断V5/V6
     *
     * @return V5 、V6 、V7 字符
     */
    private val systemProperty: String
        get() {
            var line = UNKNOWN
            var reader: BufferedReader? = null
            try {
                val p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name")
                reader = BufferedReader(InputStreamReader(p.inputStream), 1024)
                line = reader.readLine()
                return line
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (reader != null)
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

            }
            return line
        }

    /**
     * 检查手机是否是miui
     *
     * @return 检查手机是否是miui
     * http://dev.xiaomi.com/doc/p=254/index.html
     */
    val isMIUI: Boolean
        get() {
            val device = Build.MANUFACTURER
            return device == "Xiaomi"
        }

    /**
     * 获取应用权限设置 Intent <br></br>
     * http://blog.csdn.net/dawanganban/article/details/41749911
     *
     * @param context 上下文
     */
    fun getSettingIntent(context: Context): Intent? {
        // 之兼容miui v5/v6/v7  的应用权限设置页面
        if (systemVersionCode >= 6) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
            intent.putExtra("extra_pkgname", context.packageName)
            return intent
        }
        return null
    }

    /**
     * 判断 activity 是否存在
     *
     * @param context 上下文
     * @param intent  跳转意图
     * @return 是否存在
     */
    fun isIntentAvailable(context: Context, intent: Intent?): Boolean {
        return intent != null && context.packageManager.queryIntentActivities(intent, 0).size > 0
    }
}
