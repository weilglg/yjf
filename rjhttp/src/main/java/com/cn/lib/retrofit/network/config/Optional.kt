package com.cn.lib.retrofit.network.config

import io.reactivex.Observable

/**
 * 当返回的数据data为空时，使用该类进行包装
 *
 * Created by Admin on 2019/4/22.
 */
class Optional<out M>(private val optional: M? ) {


    /**
     * 判断返回的数据是否为空
     */
    fun isEmpty(): Boolean {
        return optional == null
    }

    /**
     * 获取不能为空的返回结果，如果为null则抛出一样
     */
    fun get(): M {
        if (optional == null) {
            Observable.just("").blockingSingle()
            throw NoSuchElementException("No value present")
        }
        return optional
    }

    /**
     * 获取可以为null的返回结果
     */
    fun getIncludeNull(): M? {
        return optional
    }

}