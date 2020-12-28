package cn.ygyg.cloudpayment.acp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker

/**
 * 向系服务统请求权限
 */
internal class AcpService {

    /**
     * 检查权限授权状态
     *
     * @param context    上下文
     * @param permission 权限 code
     * @return 检查结果
     */
    fun checkSelfPermission(context: Context, permission: String): Int {
        try {
            val info = context.packageManager.getPackageInfo(
                    context.packageName, 0)
            val targetSdkVersion = info.applicationInfo.targetSdkVersion
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    ContextCompat.checkSelfPermission(context, permission)
                } else {
                    PermissionChecker.checkSelfPermission(context, permission)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return ContextCompat.checkSelfPermission(context, permission)
    }

    /**
     * 向系统请求权限
     *
     * @param activity    启动请求的Activity
     * @param permissions 权限code
     * @param requestCode 返回码
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * 检查权限是否存在拒绝不再提示
     *
     * @param activity   启动请求的Activity
     * @param permission 权限code
     * @return 是否 不再提醒
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}
