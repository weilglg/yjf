package com.cn.lib.retrofit.network.util

import java.io.File
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.ArrayList
import java.util.Collections

import okhttp3.MediaType
import okhttp3.RequestBody

 object Util {

    val MULTIPART_FORM_DATA = "multipart/form-data;"
    val MULTIPART_IMAGE_DATA = "image/*; charset=utf-8"
    val MULTIPART_JSON_DATA = "application/json; charset=utf-8"
    val MULTIPART_TEXT_DATA = "text/plain"
    val MULTIPART_BYTE_DATE = "application/octet-stream"

    fun isEmpty(str: String?): Boolean {
        return str == null || "" == str || "null" == str
    }

     @JvmStatic fun <T> checkNotNull(data: T?, message: String): T {
        if (data == null) {
            throw NullPointerException(message)
        }
        return data
    }

    fun createBytes(bytes: ByteArray?): RequestBody {
        checkNotNull(bytes, "json not null!")
        return RequestBody.create(okhttp3.MediaType.parse(MULTIPART_BYTE_DATE), bytes)
    }

    fun createJson(jsonString: String?): RequestBody {
        checkNotNull(jsonString, "json not null!")
        return RequestBody.create(okhttp3.MediaType.parse(MULTIPART_JSON_DATA), jsonString)
    }


    fun createText(text: String): RequestBody {
        checkNotNull(text, "text not null!")
        return RequestBody.create(MediaType.parse(MULTIPART_TEXT_DATA), text)
    }

    fun createString(name: String): RequestBody {
        checkNotNull(name, "name not null!")
        return RequestBody.create(okhttp3.MediaType.parse("$MULTIPART_FORM_DATA; charset=utf-8"), name)
    }


    fun createFile(file: File): RequestBody {
        checkNotNull(file, "file not null!")
        return RequestBody.create(okhttp3.MediaType.parse("$MULTIPART_FORM_DATA; charset=utf-8"), file)
    }

    fun createImage(file: File): RequestBody {
        checkNotNull(file, "file not null!")
        return RequestBody.create(okhttp3.MediaType.parse(MULTIPART_IMAGE_DATA), file)
    }

    fun createPartFromString(descriptionString: String): RequestBody {
        checkNotNull(descriptionString, "description string not null!")
        return RequestBody.create(MediaType.parse("$MULTIPART_FORM_DATA; charset=utf-8"), descriptionString)
    }

    /**
     * 普通类反射获取泛型方式，获取需要实际解析的类型
     *
     * @param <T>
     * @return
    </T> */
    fun <T> findNeedClass(cls: Class<T>): Type {
        //以下代码是通过泛型解析实际参数,泛型必须传
        val genType = cls.genericSuperclass
        val params = (genType as ParameterizedType).actualTypeArguments
        val type = params[0]
        val finalNeedType: Type
        if (params.size > 1) {//这个类似是：CacheResult<SkinTestResult> 2层
            if (type !is ParameterizedType) throw IllegalStateException("没有填写泛型参数")
            finalNeedType = type.actualTypeArguments[0]
            //Type rawType = ((ParameterizedType) type).getRawType();
        } else {//这个类似是:SkinTestResult  1层
            finalNeedType = type
        }
        return finalNeedType
    }

    /**
     * 普通类反射获取泛型方式，获取最顶层的类型
     */
    fun <T> findRawType(cls: Class<T>): Type {
        val genType = cls.genericSuperclass
        return getGenericType(genType as ParameterizedType, 0)
    }

    /**
     * find the type by interfaces
     *
     * @param cls
     * @param <R>
     * @return
    </R> */
    fun <R> findNeedType(cls: Class<R>): Type {
        val typeList = getMethodTypes(cls)
        return if (typeList == null || typeList.isEmpty()) {
            RequestBody::class.java
        } else typeList[0]
    }

    /**
     * MethodHandler
     */
    fun <T> getMethodTypes(cls: Class<T>): List<Type>? {
        val typeOri = cls.genericSuperclass
        var needtypes: MutableList<Type>? = null
        // if Type is T
        if (typeOri is ParameterizedType) {
            needtypes = ArrayList()
            val parentypes = typeOri.actualTypeArguments
            for (childtype in parentypes) {
                needtypes.add(childtype)
                if (childtype is ParameterizedType) {
                    val childtypes = childtype.actualTypeArguments
                    Collections.addAll(needtypes, *childtypes)
                }
            }
        }
        return needtypes
    }

    fun getClass(type: Type, i: Int): Class<*> {
        return if (type is ParameterizedType) { // 处理泛型类型
            getGenericClass(type, i)
        } else if (type is TypeVariable<*>) {
            getClass(type.bounds[0], 0) // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            type as Class<*>
        }
    }

    fun getType(type: Type, i: Int): Type? {
        return if (type is ParameterizedType) { // 处理泛型类型
            getGenericType(type, i)
        } else if (type is TypeVariable<*>) {
            getType(type.bounds[0], 0) // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            type
        }
    }

    fun getParameterizedType(type: Type, i: Int): Type? {
        return if (type is ParameterizedType) { // 处理泛型类型
            type.actualTypeArguments[i]
        } else if (type is TypeVariable<*>) {
            getType(type.bounds[0], 0) // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            type
        }
    }

    fun getGenericClass(parameterizedType: ParameterizedType, i: Int): Class<*> {
        val genericClass = parameterizedType.actualTypeArguments[i]
        return if (genericClass is ParameterizedType) { // 处理多级泛型
            genericClass.rawType as Class<*>
        } else if (genericClass is GenericArrayType) { // 处理数组泛型
            genericClass.genericComponentType as Class<*>
        } else if (genericClass is TypeVariable<*>) { // 处理泛型擦拭对象
            getClass(genericClass.bounds[0], 0)
        } else {
            genericClass as Class<*>
        }
    }

    fun getGenericType(parameterizedType: ParameterizedType, i: Int): Type {
        val genericType = parameterizedType.actualTypeArguments[i]
        return if (genericType is ParameterizedType) { // 处理多级泛型
            genericType.rawType
        } else if (genericType is GenericArrayType) { // 处理数组泛型
            genericType.genericComponentType
        } else if (genericType is TypeVariable<*>) { // 处理泛型擦拭对象
            getClass(genericType.bounds[0], 0)
        } else {
            genericType
        }
    }

}
