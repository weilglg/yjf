package com.cn.lib.retrofit.network.callback

import java.lang.reflect.Type

interface IResultType<T> {

    /**
     * 获取需要解析的泛型T类型
     */
    fun getType(): Type

}
