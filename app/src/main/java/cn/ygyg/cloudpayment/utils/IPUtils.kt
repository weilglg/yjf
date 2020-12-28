package cn.ygyg.cloudpayment.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object IPUtils {
    @SuppressLint("WifiManagerPotentialLeak")
    fun getIPAddress(context: Context): String {
        val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val networkInterface = en.nextElement()
                        val enumIpAddress = networkInterface.inetAddresses
                        while (enumIpAddress.hasMoreElements()) {
                            val address = enumIpAddress.nextElement()
                            if (!address.isLoopbackAddress && address is Inet4Address) {
                                return address.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }

            } else if (info.type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return ipInt2String(wifiInfo.ipAddress)
            }
        }
        return "0.0.0.0"
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip 172.0.0.1
     * @return 172.0.0.1
     */
    private fun ipInt2String(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}
