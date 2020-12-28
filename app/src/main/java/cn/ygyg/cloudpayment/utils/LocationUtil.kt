package cn.ygyg.cloudpayment.utils

import cn.ygyg.cloudpayment.app.MyApplication
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption

object LocationUtil {
    /**
     * 定位配置
     */
    private val locationOption: AMapLocationClientOption by lazy {
        AMapLocationClientOption().apply {
            isNeedAddress = true
            isOnceLocation = true
            isLocationCacheEnable = false
        }
    }
    /**
     * 定位链接
     */
    val locationClient: AMapLocationClient by lazy {
        AMapLocationClient(MyApplication.getApplication()).apply {
            setLocationOption(locationOption)
        }
    }

    fun startLocation(callback: Callback) {
        callback.onStart()
        PermissionUtil.Builder()
                .setContext(MyApplication.getApplication())
                .addPermissionName(PermissionUtil.ACCESS_COARSE_LOCATION)
                .addPermissionName(PermissionUtil.ACCESS_FINE_LOCATION)
                .setCallback(object : PermissionUtil.CheckPermissionCallback {
                    override fun onGranted() {
                        locationClient.setLocationListener { location ->
                            if (location.errorCode == 0) {
                                callback.onSuccess(location)
                            } else {
                                callback.onFailed(location.errorCode)
                            }
                        }
                        locationClient.startLocation()
                    }

                    override fun onDenied(permissions: List<String>) {
                        callback.onFailed(99)
                    }
                })
                .build()

    }

    interface Callback {
        fun onStart()
        fun onSuccess(location: AMapLocation)
        //99 无定位权限 其他参照高德错误码
        fun onFailed(errCode: Int)
    }
}
