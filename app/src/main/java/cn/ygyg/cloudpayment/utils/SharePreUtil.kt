package cn.ygyg.cloudpayment.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import com.alibaba.fastjson.JSON

import java.util.HashMap

object SharePreUtil {

    private val intMap: MutableMap<String, Int>by lazy {
        mutableMapOf<String, Int>()
    }
    private val booleanMap: MutableMap<String, Boolean> by lazy {
        mutableMapOf<String, Boolean>()
    }
    private val stringMap: MutableMap<String, String> by lazy {
        mutableMapOf<String, String>()
    }
    private val longMap: MutableMap<String, Long>by lazy {
        mutableMapOf<String, Long>()
    }
    lateinit var sp: SharedPreferences

    /**
     * 初始化Sp文件
     *
     * @param context
     */
    fun init(context: Context) {
        sp = context.getSharedPreferences("Circle", Context.MODE_PRIVATE)
    }

    /**
     * 从Sp中获取对应KEY的boolean
     *
     * @param key
     */
    fun getBoolean(key: String): Boolean {
        var bool: Boolean? = booleanMap[key]
        if (bool == null) {
            bool = sp.getBoolean(key, false)
            booleanMap.put(key, bool)
        }
        return bool
    }

    /**
     * 把boolean保存到Sp中
     *
     * @param key
     * @param value
     */
    fun putBoolean(key: String, value: Boolean) {
        booleanMap.put(key, value)
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * 从Sp中获取对应KEY的int
     *
     * @param key
     */
    fun getInt(key: String): Int {
        var i: Int? = intMap[key]
        if (i == null) {
            i = sp.getInt(key, -1)
            intMap.put(key, i)
        }
        return i
    }

    /**
     * 把int保存到Sp中
     *
     * @param key
     * @param value
     */
    fun putInt(key: String, value: Int) {
        intMap.put(key, value)
        sp.edit().putInt(key, value).apply()
    }


    /**
     * 把String保存到Sp中
     *
     * @param key
     * @param value
     */
    fun putString(key: String, value: String) {
        stringMap.put(key, value)
        sp.edit().putString(key, value).apply()
    }

    /**
     * 从Sp中获取对应KEY的String
     *
     * @param key
     * @return
     */
    fun getString(key: String): String? {
        var str: String? = stringMap[key]
        if (TextUtils.isEmpty(str)) {
            str = sp.getString(key, "")
            stringMap.put(key, str)
        }
        return str
    }


    /**
     * 把String[]保存到Sp文件里
     *
     * @param key
     * @param values
     */
    fun putStringArray(key: String, values: Array<String>?) {
        if (values == null) {
            return
        }
        var stringBuilder = StringBuilder()
        for (i in values.indices) {
            stringBuilder = if (stringBuilder.length < 1) stringBuilder.append(values[i]) else stringBuilder.append("#_#").append(values[i])
        }
        putString(key, stringBuilder.toString())
    }

    /**
     * 获取SP中对应key的内容并转化为String[]
     *
     * @param key
     * @return
     */
    fun getStringArray(key: String): Array<String>? {
        val value = getString(key)?.trim {   it <= ' ' }
        return if (TextUtils.isEmpty(value)) {
            null
        } else value?.split("#_#".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
    }


    /**
     * 把Long类型的值放入SP文件里
     *
     * @param key
     * @param value
     */
    fun putLong(key: String, value: Long) {
        longMap.put(key, value)
        sp.edit().putLong(key, value).apply()
    }

    /**
     * 获取SP中key对应的Long类型的值
     *
     * @param key
     * @return
     */
    fun getLong(key: String): Long? {
        var l: Long? = longMap[key]
        if (l == null) {
            l = sp.getLong(key, 0L)
            longMap.put(key, l)
        }
        return l
    }

    /**
     * 把javaBean转化为json并保存在SP中
     *
     * @param key
     * @param obj
     */
    fun saveBeanByFastJson(key: String, obj: Any) {
        sp.edit().putString(key, JSON.toJSONString(obj)).apply()
    }

    /**
     * @param key
     * @param clazz 这里传入一个类就是我们所需要的实体类(obj)
     * @return 返回我们封装好的该实体类(obj)
     */
    inline fun <reified T> getBeanByFastJson(key: String): T? {
        val objString = sp.getString(key, "")
        return if (TextUtils.isEmpty(objString) || !objString.startsWith("{") || !objString.endsWith("}")) {
            null
        } else JSON.parseObject(objString, T::class.java)
    }

    /**
     * @param key
     * @param clazz 这里传入一个类就是我们所需要的实体类(obj)
     * @return 返回我们封装好的该实体类(obj)
     */
    inline fun <reified T> getListByFastJson(key: String): List<T>? {
        val objString = sp.getString(key, "")
        return if (TextUtils.isEmpty(objString) || !objString.startsWith("[") || !objString.endsWith("]")) {
            null
        } else JSON.parseArray(objString, T::class.java)
    }


    fun clear(key: String) {
        stringMap.remove(key)
        intMap.remove(key)
        longMap.remove(key)
        booleanMap.remove(key)
        sp.edit().remove(key).apply()
    }

}
