package com.cn.lib.weight.indicator

/**
 * Created by admin on 2015/11/9.
 */

import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment

import java.io.Serializable
import java.lang.reflect.Constructor

/**
 * 单个选项卡类，每个选项卡包含名字，图标以及提示（可选，默认不显示）
 */
class TabInfo : Serializable {

    var id: Int = 0
        private set
    var normalIcon: Int = 0
        private set // 没有选中时的图标
    var selectedIcon: Int = 0
        private set // 选中时的图标
    var name: String? = null
        private set // tab的文本
    var fragment: Fragment? = null // 关联的Fragment
    var notifyChange = false
    var fragmentClass: Class<out Fragment>? = null// 关联的Fragment类

    constructor(id: Int, name: String, clazz: Class<out Fragment>) : this(id, name, 0, clazz)

    constructor(id: Int, name: String, iconId: Int, clazz: Class<out Fragment>) : this(id, name, iconId, iconId, clazz)

    constructor(id: Int, name: String, normalIcon: Int, selectedIcon: Int, clazz: Class<out Fragment>) {
        this.id = id
        this.normalIcon = normalIcon
        this.selectedIcon = selectedIcon
        if (selectedIcon == 0) {
            this.selectedIcon = normalIcon
        }
        this.name = name
        this.fragmentClass = clazz
    }

    fun createFragment(): Fragment? {
        if (fragment == null) {
            val constructor: Constructor<out Fragment>
            try {
                constructor = fragmentClass!!.getConstructor(*arrayOfNulls(0))
                fragment = constructor.newInstance(*arrayOfNulls(0))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return fragment
    }


}
