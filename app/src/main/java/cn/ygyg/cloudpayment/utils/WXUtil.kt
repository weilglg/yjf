package cn.ygyg.cloudpayment.utils

import android.content.Context
import android.content.pm.PackageInfo
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.MyApplication
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * Created by Admin on 2019/4/19.
 */
object WXUtil {

    internal lateinit  var mWxApi: IWXAPI

    /**
     * 初始化配置信息
     */
    fun registerToWX() {
        val wxAppId = ConfigUtil.getWXAppId()
        //第二个参数是指你应用在微信开放平台上的AppID
        mWxApi = WXAPIFactory.createWXAPI(MyApplication.getApplication(), wxAppId, false)
        // 将该app注册到微信
        mWxApi.registerApp(wxAppId)
    }

    /**
     * 判断是否安装微信
     */
    fun isWXAppInstalled(context: Context): Boolean {
        if (mWxApi.isWXAppInstalled) {
            return true
        } else {
            val packageManager = context.packageManager
            val pInfo: List<PackageInfo>? = packageManager.getInstalledPackages(0)
            pInfo?.forEach {
                if ("com.tencent.mm" == it.packageName) {
                    return true
                }
            }
            return false
        }
    }
}