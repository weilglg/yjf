package com.cn.lib.basic

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import java.util.ArrayList

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * Created by admin on 2016/4/1.
 */
abstract class BaseRecyclerAdapter<T> @JvmOverloads constructor(protected var context: Context, private val layoutResId: Int, private val isCancelSelectedState: Boolean = false) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {
    protected var data: MutableList<T> = mutableListOf()
    private var onClickPosition = -1
    private var onItemClickListener: OnItemClickListener<T>? = null
    private val mHeaderLayout: LinearLayout by lazy {
        LinearLayout(context)
    }
    private val mFooterLayout: LinearLayout by lazy {
        LinearLayout(context)
    }

    /**
     * @return 除去头布局和脚布局长度的真实数据长度
     */
    fun getRealItemCount(): Int {
        return data.size
    }

    fun getHeaderViewPosition(): Int {
        return 0
    }

    fun getHeaderLayoutCount(): Int {
        return if (mHeaderLayout.childCount == 0) 0 else 1
    }

    fun getFooterLayoutCount(): Int {
        return if (mFooterLayout.childCount == 0) 0 else 1
    }

    fun getFooterViewPosition(): Int {
        return getHeaderLayoutCount() + data.size
    }

    fun setNewList(data: MutableList<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun addAll(data: List<T>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }

    fun set(oldElem: T, newElem: T) {
        val index = data.indexOf(oldElem)
        set(index, newElem)
    }

    fun set(indexOf: Int = -1, newElem: T) {
        if (indexOf > 0) {
            this.data[indexOf] = newElem
        } else {
            this.data.add(newElem)
        }
        val position = indexOf + getHeaderLayoutCount()
        notifyItemChanged(position)
    }

    fun getItem(position: Int): T? {
        return data.let {
            if (it.size > position)
                it[position]
            else
                null
        }
    }

    /**
     * 根据下标删除数据，如果有头布局会加上头布局的下标
     */
    fun removeItem(index: Int = -1) {
        val position = index + getHeaderLayoutCount()
        if (position < itemCount && position > -1) {
            this.data.removeAt(index)
            notifyItemRemoved(position)
        }
    }

    /**
     * 删除某一条数据
     */
    fun removeItem(item: T) {
        val index = this.data.indexOf(item)
        if (index > -1) {
            removeItem(index)
        }
    }

    /**
     * 获取列表数据
     */
    fun getList(): MutableList<T> {
        return data
    }

    /**
     * 插入元素操作
     */
    fun insertItem(index: Int, newElem: T) {
        this.data.add(index, newElem)
        val position = index + getHeaderLayoutCount()
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        if (layoutResId <= 0) {
            throw RuntimeException("layout resource id is null")
        }
        if (viewType == ITEM_TYPE_HEADER) {
            return HeadHolder(mHeaderLayout, context)
        }
        if (viewType == ITEM_TYPE_FOOTER) {
            return FooterHolder(mFooterLayout, context)
        }
        val view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return BaseRecyclerViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }
        //除去头部的真实数据位置
        val realPosition = position - getHeaderLayoutCount()
        convertView(holder, data[realPosition], realPosition)
        if (holder.itemView != null && onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                if (onClickPosition != realPosition) {
                    onClickPosition = realPosition
                    notifyDataSetChanged()
                    onItemClickListener?.onItemClick(onClickPosition, data[realPosition])
                } else if (isCancelSelectedState) {
                    onClickPosition = -1
                    notifyDataSetChanged()
                    onItemClickListener?.onItemClick(onClickPosition, data[realPosition])
                }
            }
        }
    }

    private fun isHeaderViewPos(position: Int): Boolean {
        return position < getHeaderLayoutCount()
    }

    private fun isFooterViewPos(position: Int): Boolean {
        return position >= getHeaderLayoutCount() + getRealItemCount()
    }

    fun addHeaderView(header: View): Int {
        return this.addHeaderView(header, -1)
    }

    private fun addHeaderView(header: View, index: Int, orientation: Int = LinearLayout.VERTICAL): Int {
        var addIndex = index
        val width: Int
        val height: Int
        if (orientation == LinearLayout.VERTICAL) {
            mHeaderLayout.orientation = LinearLayout.VERTICAL
            width = MATCH_PARENT
            height = WRAP_CONTENT
        } else {
            mHeaderLayout.orientation = LinearLayout.HORIZONTAL
            width = WRAP_CONTENT
            height = MATCH_PARENT
        }
        if (mHeaderLayout.layoutParams == null) {
            mHeaderLayout.layoutParams = RecyclerView.LayoutParams(width, height)
        }
        val childCount = getHeaderLayoutCount()
        if (addIndex < 0 || addIndex > childCount) {
            addIndex = childCount
        }
        mHeaderLayout.addView(header, addIndex)
        if (childCount == 1) {
            val position = getFooterViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return addIndex
    }

    @JvmOverloads
    fun addFooterView(footer: View, index: Int = -1, orientation: Int = LinearLayout.VERTICAL): Int {
        var addIndex = index
        val width: Int
        val height: Int
        if (orientation == LinearLayout.VERTICAL) {
            mFooterLayout.orientation = LinearLayout.VERTICAL
            width = MATCH_PARENT
            height = WRAP_CONTENT
        } else {
            mFooterLayout.orientation = LinearLayout.HORIZONTAL
            width = WRAP_CONTENT
            height = MATCH_PARENT
        }
        if (mFooterLayout.layoutParams == null) {
            mFooterLayout.layoutParams = RecyclerView.LayoutParams(width, height)
        }
        val childCount = getFooterLayoutCount()
        if (addIndex < 0 || addIndex > childCount) {
            addIndex = childCount
        }
        mFooterLayout.addView(footer, addIndex)
        if (childCount == 1) {
            val position = getFooterViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return addIndex
    }

    /**
     * 清空尾布局
     */
    fun clearFooterViews() {
        mFooterLayout.removeAllViews()
    }

    /**
     * 清空头布局
     */
    fun clearHeaderViews() {
        mHeaderLayout.removeAllViews()
    }

    /**
     * 根据下标删除头部布局
     */
    fun removeHeaderAt(index: Int) {
        if (index < mHeaderLayout.childCount && index > -1) {
            mHeaderLayout.removeViewAt(index)
        }
    }

    /**
     * 根据下标删除尾部布局
     */
    fun removeFooterAt(index: Int) {
        if (index < mFooterLayout.childCount && index > -1) {
            mFooterLayout.removeViewAt(index)
        }
    }

    /*根据位置来返回不同的item类型*/
    override fun getItemViewType(position: Int): Int {
        if (isHeaderViewPos(position)) {
            return ITEM_TYPE_HEADER
        } else if (isFooterViewPos(position)) {
            return ITEM_TYPE_FOOTER
        }
        return position - getHeaderLayoutCount()
    }

    override fun getItemCount(): Int {
        return getHeaderLayoutCount() + getFooterLayoutCount() + getRealItemCount()
    }

    /**
     * 实现逻辑代码
     *
     * @param holder
     * @param t
     * @param realPosition
     */
    abstract fun convertView(holder: BaseRecyclerViewHolder, t: T, realPosition: Int)

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }

    fun setDefaultSelectPosition(onClickPosition: Int) {
        this.onClickPosition = onClickPosition
    }

    interface OnItemClickListener<T> {
        fun onItemClick(position: Int, obj: T)
    }

    private inner class HeadHolder(itemView: LinearLayout?, context: Context) : BaseRecyclerViewHolder(itemView, context)

    private inner class FooterHolder(itemView: View?, context: Context) : BaseRecyclerViewHolder(itemView, context)

    companion object {
        private val ITEM_TYPE_HEADER = 0x1000
        private val ITEM_TYPE_FOOTER = 0x2000
    }
}
