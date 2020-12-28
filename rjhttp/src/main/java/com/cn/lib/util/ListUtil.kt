package com.cn.lib.util

import java.util.ArrayList

/**
 * Created by admin on 2018/1/4/004.
 */

object ListUtil {

    /**
     * 判断是否为空.
     */
    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    /**
     * 返回a与b的交集的新List.
     */
    fun <T> intersection(a: Collection<T>, b: Collection<T>): List<T>? {
        if (isEmpty(a) || isEmpty(b)) {
            return null
        }

        val list = ArrayList<T>()

        for (element in a) {
            if (b.contains(element)) {
                list.add(element)
            }
        }
        return list
    }

    fun <T> contains(list: List<T>, t: T): Boolean {
        return if (!isEmpty(list)) {
            list.contains(t)
        } else false
    }

    fun <T> containsAll(list: List<T>, targetList: List<T>): Boolean {
        return if (!isEmpty(list)) {
            list.containsAll(targetList)
        } else false
    }

}
