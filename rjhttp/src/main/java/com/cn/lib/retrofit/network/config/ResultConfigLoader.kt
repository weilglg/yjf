package com.cn.lib.retrofit.network.config

import android.content.Context
import android.text.TextUtils

import com.alibaba.fastjson.JSON

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

object ResultConfigLoader {

    private var config: Config? = null

    private val CONFIG_NAME = "result-config.json"

    /**
     * 获取自定义失败对应的说明信息
     */
    private val errorConfig: HashMap<String, String>?
        get() = config?.errorInfo

    /**
     * 返回消息对应的键
     */
    val msgKey: String?
        get() = if (config == null) {
            "msg"
        } else config?.msgKey

    /**
     * 获取状态码对应的键
     */
    val codeKey: String?
        get() = if (config == null) {
            "code"
        } else config?.codeKey

    /**
     * 数据对应的键
     */
    val dataKey: List<String>?
        get() {
            val dataKeyList = ArrayList<String>()
            dataKeyList.add("data")
            return config?.dataKey ?:  dataKeyList
        }

    /**
     * 初始化
     */
    fun init(context: Context) {
        loadConfig(context)
    }

    private fun loadConfig(context: Context) {
        if (config != null) {
            return
        }
        var jsonStr = loadFromAssets(context, CONFIG_NAME)
        if (TextUtils.isEmpty(jsonStr)) {
            return
        }
        jsonStr = jsonStr.replace("\r\n", "")
        config = JSON.parseObject<Config>(jsonStr, Config::class.java)
    }

    fun checkErrorCode(errorCode: String): Boolean {
        return errorConfig?.containsKey(errorCode) ?: false
    }

    fun errorDesc(errorCode: String): String {
        if (checkErrorCode(errorCode)) {
            return errorConfig?.get(errorCode) ?: ""
        }
        return ""
    }

    /**
     * 判断是否请求成功
     */
    fun checkSuccess(code: String): Boolean {
        return config?.successCode?.contains(code) ?: false
    }


    private fun loadFromAssets(context: Context, fileName: String): String {
        var reader: BufferedReader? = null
        try {
            val `in` = context.resources.assets.open(fileName)
            reader = BufferedReader(InputStreamReader(`in`))

            val buf = CharArray(1024)
            var count = reader.read(buf)
            val sb = StringBuilder(`in`.available())
            while (count != -1) {
                val readData = String(buf, 0, count)
                sb.append(readData)
                count = reader.read(buf)
            }

            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return ""
    }

    class Config : Serializable {
        var successCode: MutableList<String> = mutableListOf()
        var codeKey: String = ""
        var dataKey: MutableList<String> = mutableListOf()
        var msgKey: String = ""
        var errorInfo: HashMap<String, String> = hashMapOf()
    }

}
