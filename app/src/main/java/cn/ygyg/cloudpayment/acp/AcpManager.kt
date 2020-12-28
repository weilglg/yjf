package cn.ygyg.cloudpayment.acp

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.util.*

/**
 * 请求权限管理类
 */
internal class AcpManager(private val mContext: Context) {
    private val mService: AcpService = AcpService()
    private var mOptions: AcpOptions? = null
    private var mCallback: AcpListener? = null
    private val mDeniedPermissions = LinkedList<String>()
    private val mManifestPermissions = HashSet<String>(1)

    init {
        getManifestPermissions()
    }

    @Synchronized
    private fun getManifestPermissions() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.packageManager.getPackageInfo(
                    mContext.packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (packageInfo != null) {
            val permissions = packageInfo.requestedPermissions
            if (permissions != null) {
                mManifestPermissions.addAll(Arrays.asList(*permissions))
            }
        }
    }

    /**
     * 开始请求
     *
     * @param options     权限检查配置
     * @param acpListener 检查结果回调
     */
    @Synchronized
    fun request(options: AcpOptions, acpListener: AcpListener) {
        mCallback = acpListener
        mOptions = options
        checkSelfPermission(null)
    }

    /**
     * 检查权限
     */
    @Synchronized
    private fun checkSelfPermission(activity: Activity?) {
        mDeniedPermissions.clear()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mCallback != null)
                mCallback!!.onGranted()
            onDestroy(null)
            return
        }
        mOptions?.permissions?.let {
            for (permission in it) {
                //检查申请的权限是否在 AndroidManifest.xml 中
                if (mManifestPermissions.contains(permission)) {
                    val checkSelfPermission = mService.checkSelfPermission(mContext, permission)
                    //如果是拒绝状态则装入拒绝集合中
                    if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                        mDeniedPermissions.add(permission)
                    }
                }
            }
        }
        //检查如果没有一个拒绝响应 onGranted 回调
        if (mDeniedPermissions.isEmpty()) {
            if (mCallback != null)
                mCallback!!.onGranted()
            onDestroy(activity)
            return
        }
        startAcpActivity()
    }

    /**
     * 启动处理权限过程的 Activity
     */
    @Synchronized
    private fun startAcpActivity() {
        val intent = Intent(mContext, AcpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext.startActivity(intent)
    }

    /**
     * 检查权限是否存在拒绝不再提示
     *
     * @param activity 检查权限依赖的Activity
     */
    @Synchronized
    fun checkRequestPermissionRationale(activity: Activity) {
        var rationale = false
        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
        for (permission in mDeniedPermissions) {
            rationale = rationale || mService.shouldShowRequestPermissionRationale(activity, permission)
        }
        val permissions = mDeniedPermissions.toTypedArray()
        if (rationale)
            showRationalDialog(activity, permissions)
        else
            requestPermissions(activity, permissions)
    }

    /**
     * 申请理由对话框
     */
    @Synchronized
    private fun showRationalDialog(activity: Activity, permissions: Array<String>) {
        val alertDialog = AlertDialog.Builder(activity)
                .setMessage(mOptions!!.rationalMessage)
                .setPositiveButton(mOptions!!.rationalBtnText) { _, _ -> requestPermissions(activity, permissions) }.create()
        alertDialog.setCancelable(mOptions!!.isDialogCancelable)
        alertDialog.setCanceledOnTouchOutside(mOptions!!.isDialogCanceledOnTouchOutside)
        alertDialog.show()
    }

    /**
     * 向系统请求权限
     *
     * @param permissions 申请的权限列表
     */
    @Synchronized
    private fun requestPermissions(activity: Activity, permissions: Array<String>) {
        mService.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSION)
    }

    /**
     * 响应向系统请求权限结果
     *
     * @param requestCode  请求码
     * @param permissions  申请的权限列表
     * @param grantResults 授权结果
     */
    @Synchronized
    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                val grantedPermissions = LinkedList<String>()
                val deniedPermissions = LinkedList<String>()
                for (i in permissions.indices) {
                    val permission = permissions[i]
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        grantedPermissions.add(permission)
                    else
                        deniedPermissions.add(permission)
                }
                //全部允许才回调 onGranted 否则只要有一个拒绝都回调 onDenied
                if (!grantedPermissions.isEmpty() && deniedPermissions.isEmpty()) {
                    if (mCallback != null)
                        mCallback!!.onGranted()
                    onDestroy(activity)
                } else if (!deniedPermissions.isEmpty()) showDeniedDialog(activity, deniedPermissions)
            }
        }
    }

    /**
     * 拒绝权限提示框
     *
     * @param permissions 申请的权限列表
     */
    @Synchronized
    private fun showDeniedDialog(activity: Activity, permissions: List<String>) {
        val alertDialog = AlertDialog.Builder(activity)
                .setMessage(mOptions!!.deniedMessage)
                .setNegativeButton(mOptions!!.deniedCloseBtn) { _, _ ->
                    if (mCallback != null)
                        mCallback!!.onDenied(permissions)
                    onDestroy(activity)
                }
                .setPositiveButton(mOptions!!.deniedSettingBtn) { _, _ -> startSetting(activity) }.create()
        alertDialog.setCancelable(mOptions!!.isDialogCancelable)
        alertDialog.setCanceledOnTouchOutside(mOptions!!.isDialogCanceledOnTouchOutside)
        alertDialog.show()
    }

    /**
     * 摧毁本库的 回调
     */
    private fun onDestroy(activity: Activity?) {
        mCallback = null
        activity?.finish()
    }

    /**
     * 跳转到设置界面
     */
    private fun startSetting(activity: Activity) {
        if (MiuiOs.isMIUI) {
            val intent = MiuiOs.getSettingIntent(activity)
            if (MiuiOs.isIntentAvailable(activity, intent)) {
                activity.startActivityForResult(intent, REQUEST_CODE_SETTING)
                return
            }
        }
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + activity.packageName))
            activity.startActivityForResult(intent, REQUEST_CODE_SETTING)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                activity.startActivityForResult(intent, REQUEST_CODE_SETTING)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

        }

    }

    /**
     * 响应设置权限返回结果
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    @Suppress("UNUSED_PARAMETER")
    @Synchronized
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent) {
        if (mCallback == null || mOptions == null
                || requestCode != REQUEST_CODE_SETTING) {
            onDestroy(activity)
            return
        }
        checkSelfPermission(activity)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 0x38
        private const val REQUEST_CODE_SETTING = 0x39
    }
}
