package cn.ygyg.cloudpayment.utils

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views = SparseArray<View>()

    @Suppress("UNCHECKED_CAST")
    fun <V : View> findViewById(@IdRes id: Int): V {
        var v: View? = views.get(id)
        if (v == null) {
            v = itemView.findViewById(id)
            views.put(id, v)
        }
        return v!! as V
    }
}
