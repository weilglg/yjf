package cn.ygyg.cloudpayment.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import cn.ygyg.cloudpayment.acp.Acp
import cn.ygyg.cloudpayment.acp.AcpListener
import cn.ygyg.cloudpayment.acp.AcpOptions

import java.util.ArrayList

/**
 * 手机权限的判断工具
 * Created by Exile on 2016/6/27.
 */
object PermissionUtil {
    /**
     * 读写SDcard的权限
     */
    val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    /**
     * 写外部存储的权限
     */
    val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    /**
     * 读取手机状态的权限
     */
    val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
    /**
     * 访问网络的权限
     */
    val INTERNET = Manifest.permission.INTERNET
    /**
     * 相机的权限
     */
    val CAMERA = Manifest.permission.CAMERA
    /**
     * 录音的权限
     */
    val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    /**
     * 拨号权限
     */
    val CALL_PHONE = Manifest.permission.CALL_PHONE
    /**
     * 基于网络定位
     */
    val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    /**
     * 基于GPS
     */
    val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    /**
     * @param context        上下文
     * @param permissionName 权限名称 省略 android.permission.
     * @return permission t 有权限 f 无权限
     */
    fun isHasPermission(context: Context, permissionName: String): Boolean {
        val pm = context.packageManager
        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, context.packageName)
    }

    fun isHasPermission(context: Context, vararg permissionName: String): Boolean {
        var flag = true
        for (aPermissionName in permissionName) {
            if (!isHasPermission(context, aPermissionName)) {
                flag = false
            }
        }
        return flag
    }

    /**
     * 检查权限问题
     */
    fun checkPermission(context: Context, callback: CheckPermissionCallback, vararg permissionName: String) {
        if (!PermissionUtil.isHasPermission(context, *permissionName)) {
            //6.0权限处理
            Acp.getInstance(context).request(AcpOptions.Builder().setPermissions(*permissionName).build(), object : AcpListener {
                override fun onGranted() {
                    callback.onGranted()
                }

                override fun onDenied(permissions: List<String>) {
                    callback.onDenied(permissions)
                }
            })

        } else {
            callback.onGranted()
        }
    }

    /**
     * 调用相机
     * @param context
     * @return
     */
    fun intentToCamera(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        //6.0权限处理
        Acp.getInstance(context).request(AcpOptions.Builder().setPermissions(
                Manifest.permission.CAMERA).build(), object : AcpListener {
            //提供权限时调用

            override fun onGranted() {

            }

            //授权失败时调用
            override fun onDenied(permissions: List<String>) {

            }
        })

        return true
    }


    class Builder {
        private var context: Context? = null
        private var permissionName: ArrayList<String> = arrayListOf()
        private var callback: CheckPermissionCallback? = null

        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun addPermissionName(aPermissionName: String): Builder {
            this.permissionName.add(aPermissionName)
            return this
        }

        fun setCallback(callback: CheckPermissionCallback): Builder {
            this.callback = callback
            return this
        }

        fun build() {
            val array = permissionName.toArray(Array(permissionName.size) { index -> permissionName[index] })
            val options = AcpOptions.Builder().setPermissions(*array).build()
            Acp.getInstance(context!!).request(options, object : AcpListener {
                override fun onGranted() {
                    callback?.onGranted()
                }

                override fun onDenied(permissions: List<String>) {
                    callback?.onDenied(permissions)
                }
            })
        }
    }

    interface CheckPermissionCallback {

        fun onGranted()

        fun onDenied(permissions: List<String>)
    }

}
