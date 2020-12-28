package cn.ygyg.cloudpayment.utils

/**
 *
 *
 * RSA公钥/私钥/签名工具包
 *
 *
 *
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 *
 *
 *
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br></br>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br></br>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 *
 *
 * @author IceWee
 * @date 2012-4-26
 * @version 1.0
 */


import android.util.Base64
import android.util.Log

import javax.crypto.Cipher
import java.io.*
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec


object RSAUtils {

    private val CHAR_ENCODE = "UTF-8"
    private val CIPHER_PADDING = "RSA/ECB/PKCS1Padding"

    /**
     * 公钥加密
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: String, publicKey: RSAPublicKey): String {
        val cipher = Cipher.getInstance(CIPHER_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        // 模长
        val maxBlockSize = publicKey.modulus.bitLength() / 8 - 11
        // 加密数据长度 <= 模长-11, 如果明文长度大于模长-11则要分组加密
        val plantText = data.toByteArray(Charset.forName(CHAR_ENCODE))
        val inputLen = plantText.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var i = 0
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlockSize) {
                out.write(cipher.doFinal(plantText, offSet, maxBlockSize))
            } else {
                out.write(cipher.doFinal(plantText, offSet, inputLen - offSet))
            }
            i++
            offSet = i * maxBlockSize
        }
        return Base64Encoder(out.toByteArray())
    }

    /**
     * 从文件中输入流中加载公钥
     */
    @Throws(Exception::class)
    private fun loadPublicKeyFromFile(`in`: InputStream): String {
        try {
            val br = BufferedReader(InputStreamReader(`in`))
            var readLine: String
            val sb = StringBuilder()
            while (true) {
                readLine = br.readLine()
                if (readLine == null) {
                    break
                }
                if (readLine[0] == '-') {
                    continue
                } else {
                    sb.append(readLine)
                    sb.append('\r')
                }
            }
            return sb.toString()
        } catch (e: IOException) {
            throw Exception("公钥数据流读取错误")
        } catch (e: NullPointerException) {
            throw Exception("公钥输入流为空")
        }

    }


    /**
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串
     */
    @Throws(Exception::class)
    fun loadPublicKey(publicKeyStr: String): RSAPublicKey {
        try {
            val buffer = Base64Decoder(publicKeyStr)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = X509EncodedKeySpec(buffer)
            return keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("公钥非法")
        } catch (e: NullPointerException) {
            throw Exception("公钥数据为空")
        }

    }


    private fun Base64Encoder(data: ByteArray): String {
        return android.util.Base64.encodeToString(data, Base64.URL_SAFE)
    }

    private fun Base64Decoder(data: String): ByteArray? {
        return try {
            android.util.Base64.decode(data, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
    fun encode(text: String): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest:ByteArray = instance.digest(text.toByteArray())
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i :Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            Log.i("666",sb.toString())
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}


