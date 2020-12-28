package cn.ygyg.cloudpayment.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.support.annotation.IdRes
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseViewBuilder(parent: ViewGroup) {
    /**
     * 跟布局
     */
    private var root: View

    private var tag: Any? = null
    private var tagMap: SparseArray<Any>? = null

    companion object {
        private fun getDefParent(context: Context): ViewGroup {
            return if (context is Activity) {
                context.findViewById<View>(android.R.id.content) as ViewGroup
            } else {
                throw ClassCastException("can not cast to Activity")
            }
        }
    }

    constructor(context: Context) : this(getDefParent(context))

    init {
        this.root = this.onCreateView(LayoutInflater.from(parent.context), parent)
        initView()
    }

    protected abstract fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View

    abstract fun initView()

    /**
     * findViewById
     *
     * @param resId id
     * @param <T>   View 具体类型
     * @return 查找结果
    </T> */
    fun <T : View> findViewById(@IdRes resId: Int): T {
        return root.findViewById(resId)
    }


    fun getRoot(): View {
        return root
    }

    fun getContext(): Context {
        return root.context
    }

    protected fun getResources(): Resources {
        return root.resources
    }


    fun setTag(tag: Any) {
        this.tag = tag
    }

    fun setTag(id: Int, tag: Any) {
        if (tagMap == null) {
            tagMap = SparseArray()
        }
        tagMap?.put(id, tag)
    }

    fun getTag(): Any? {
        return tag
    }

    fun getTag(id: Int): Any? {
        return tagMap?.get(id)
    }
}
