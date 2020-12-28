package cn.ygyg.cloudpayment.utils

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import cn.ygyg.cloudpayment.app.Constants
import io.reactivex.exceptions.UndeliverableException
import java.io.IOException
import java.lang.reflect.InvocationTargetException

/**
 * 声明ISO-DEP协议的Tag操作实例
 */

object NfcHeleper {

    var isoDep: IsoDep? = null
    var errorCount = 0
    var orderCount = 0

    fun initNfc(isoDep: IsoDep){
        this.isoDep=isoDep
        isoDep.connect()
        orderCount=0
    }

    fun next(data: ByteArray?): ByteArray? {
        var result :ByteArray ?=null
        when (orderCount) {
            0 -> result= send(Constants.OrderKey.SELECT)
            1 -> {
                result= send(Constants.OrderKey.READ_UUID)
                Constants.OrderKey.UUID=result
            }
            2 -> result= send(Constants.OrderKey.GET_CHALLENGE)
            3 -> result= send(Constants.OrderKey.getExteranlAuthentication(data))
            4 -> result= send(Constants.OrderKey.SET_SECURE_CHANNEL_FIRST)
            5 ->{
                data?.let {
                    result= send(Constants.OrderKey.getSSC2(it))
                }
            }
            6 ->{
                data?.let {
                    result= send(Constants.OrderKey.getSSC3(it))
                }
            }
            7 -> result= send(Constants.OrderKey.READ_MZ)
            8 -> result= send(data)
            9 -> result= send(data)
            else -> {
                if(isoDep!!.isConnected){
                    isoDep!!.close()
                }
            }
        }
        return result
    }

    /**
     * 向Tag发送获取随机数的APDU并返回Tag响应
     * @return 十六进制随机数字符串
     */
    fun send(bytes: ByteArray?): ByteArray? {
        // 发送APDU命令
        try{
            if(isoDep!!.isConnected) {
                Log.i("sendOrderSuccess","发送 : "+PosUtils.bcdToString(bytes))
                val result = isoDep!!.transceive(bytes)
                Log.i("sendOrderSuccess","接收 : "+PosUtils.bcdToString(result))
                orderCount++
                if (getAnswer(result)) return getResult(result) else {
                    errorCount++
                    orderCount = 0
                    return null
                }
            }
        }catch (e: UndeliverableException){
            orderCount=0
            return PosUtils.stringToBcd("0000")
        } catch (e: InvocationTargetException){
            orderCount=0
            return PosUtils.stringToBcd("0000")
        } catch (err:IllegalStateException){
            orderCount=0
            return PosUtils.stringToBcd("0000")
        }catch (e: IOException){
            orderCount=0
            return PosUtils.stringToBcd("0000")
        }
        return null
    }

    fun getAnswer(result: ByteArray): Boolean {
        val bytes = BytesUtil.subBytes(result, result.size - 2, 2)
        return PosUtils.bytesToHexString(bytes).equals("9000")
    }

    fun getResult(result: ByteArray): ByteArray? {
        val bytes = BytesUtil.subBytes(result, 0, result.size - 2)
        if (bytes.size == 0|| bytes==null)return  result else
            return bytes
    }

}