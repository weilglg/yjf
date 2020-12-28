package cn.ygyg.cloudpayment.app

import cn.ygyg.cloudpayment.utils.AESHelper
import cn.ygyg.cloudpayment.utils.BytesUtil

object Constants {
    const val DEF_WEI_XIN_APP_ID: String = "wx14581fdc873a1818"

    //7a8810e4f048e7fca97d7beb1ee6cef4
    object IntentKey {
        const val IS_SUCCESS = "isSuccess"
        const val AMOUNT = "amount"
        const val FOR_RESULT = "forResult"
        const val USER_INFO = "userInfo"
        const val IS_LOGIN = "isLogin"
        const val DEVICE_CODE = "deviceCode"
        const val METER_CLASS = "meterClassification"
        const val CONTRACT_CODE = "contractCode"
        const val COMPANY_KEY = "companyCode"
        const val TOKEN_KEY = "Authorization"
        const val OPEN_ID = "openId"
        const val IC_TYPE = "icType"
        const val READ_DEC = "ReadDecResponseEntity"
    }

    object OrderKey{

        val isBefore=false
        var UUID:ByteArray?=null
        //选卡
        val SELECT = byteArrayOf(0x00, 0xA4.toByte(), 0x00, 0x00, 0x02,0xdf.toByte(),0x01) //选择应用
        
        //卡鉴权
        val READ_UUID= byteArrayOf(0x00,0xB0.toByte(), 0x83.toByte(),0x00,0x10) // Read UUID
        val GET_CHALLENGE= byteArrayOf(0x00,0x84.toByte(), 0x00,0x00,0X10) // Get Challenge
        private val EXTERANL_AUTHENTICATION= byteArrayOf(0x00,0x82.toByte(), 0x00,0x03,0x10)// EXTERANL_AUTHENTICATION

        fun getExteranlAuthentication(data: ByteArray?): ByteArray? {
            val bytes = AESHelper.aesEncryptECB16(Constants.OrderKey.APP_MK, Constants.OrderKey.UUID, 0, 16)
            val decrypt: ByteArray = AESHelper.aesEncryptECB16(FK, data,0, 16)
            return BytesUtil.mergerArray(EXTERANL_AUTHENTICATION, decrypt)
        }
        val FK = byteArrayOf(0x57,0x41,0x54,0x43,0x48,0x44,0x41,0x54,0x41,0x54,0x69,0x6D,0x65,0x43,0x4F,0x53)

        val APP_MK = byteArrayOf(0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte()
                ,0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte(),0xff.toByte())

        // 安全通道建立
        val SET_SECURE_CHANNEL_FIRST = byteArrayOf(0x00, 0x8A.toByte(), 0x00, 0x00, 0x21) // SSSC1

        private val SET_SECURE_CHANNEL_SECOND = byteArrayOf(0x00, 0x8B.toByte(), 0x00, 0x00, 0x20) //SSSC2  + data  +dade.length

        private val SET_SECURE_CHANNEL_THIRD = byteArrayOf(0x00, 0x8C.toByte(), 0x00, 0x00, 0x10) //SSSC3  + data

        fun getSSC2(data: ByteArray): ByteArray? {
            val bytes: ByteArray = BytesUtil.mergerArray(SET_SECURE_CHANNEL_SECOND, data)
            return BytesUtil.mergerArray(bytes, 0x10.toByte())
        }

        fun getSSC3(data: ByteArray): ByteArray? {
            return BytesUtil.mergerArray(SET_SECURE_CHANNEL_THIRD, data)
        }

        val READ_MZ=getMZ()

        private fun getMZ(): ByteArray =  if(isBefore) byteArrayOf(0x00,0xB0.toByte(), 0x81.toByte(),0x20, 0xe0.toByte()) else byteArrayOf(0x04,0xB0.toByte(), 0x81.toByte(),0x20,0xe0.toByte())

        val READ_MZ_BEFORE_SSC= byteArrayOf(0x00,0xB0.toByte(), 0x81.toByte(),0x20, 0xe0.toByte()) // Read UUID

        val READ_MZ_AFTER_SSC= byteArrayOf(0x04,0xB0.toByte(), 0x81.toByte(),0x20, 0xe8.toByte()) // Read UUID

        var GET_AUTH_ERR_COUNTER = byteArrayOf(0x00, 0x86.toByte(), 0x00, 0x03, 0x01) // 鉴权次数

        val RANDOM_UUID= byteArrayOf(0x00, 0xD6.toByte(), 0x83.toByte(),0x00,0x10,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f)
        val WRITE_KEY_FIRST= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x00,0x10,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00)
        val WRITE_KEY_SECOND= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x01,0x10,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01)
        val WRITE_KEY_THIRD= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x02,0x10,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02)
        val WRITE_KEY_FOUR= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x03,0x10,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03)
        val WRITE_KEY_FIVE= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x04,0x10,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04,0X04)
        val WRITE_KEY_SIX= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x05,0x10,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05,0X05)
        val WRITE_KEY_SEVEN= byteArrayOf(0x00, 0xD4.toByte(), 0x01,0x06,0x10,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06,0x06)
    }

    enum class PaymentMethod(private val type: String) {
        ALI_PAY("A"),
        WX_PAY("Q");

        fun string(): String {
            return type
        }
    }
    enum class PaymentMethods(private val type: String) {

        //支付方式
        METHOD_CASH("E"),
        METHOD_WECHAT("W"),
        METHOD_ALIPAY("Q");

        fun string(): String {
            return type
        }
    }
}
