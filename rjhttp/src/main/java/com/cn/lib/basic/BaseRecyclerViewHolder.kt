package com.cn.lib.basic

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.cn.lib.util.GlideUtil


/**
 * Created by admin on 2016/4/1.
 */
open class BaseRecyclerViewHolder(itemView: View?, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val views: SparseArray<View> = SparseArray()


    protected fun <T : View> retrieveView(viewId: Int): T? {
        if (itemView == null) {
            return null
        }
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T?
    }

    fun <T : View> getChildView(viewId: Int): T? {
        return retrieveView(viewId)
    }

    fun setTextView(viewId: Int, value: SpannableStringBuilder): BaseRecyclerViewHolder {
        val textView = retrieveView<TextView>(viewId)
        if (textView != null) {
            textView.text = value
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
        return this
    }


    fun setTextView(viewId: Int, value: String): BaseRecyclerViewHolder {
        val textView = retrieveView<TextView>(viewId)
        if (textView != null)
            textView.text = value
        return this
    }

    fun setTextColorResource(viewId: Int, textColorRes: Int): BaseRecyclerViewHolder {
        val textView = retrieveView<TextView>(viewId)
        textView?.setTextColor(context.resources.getColor(textColorRes))
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        view?.setBackgroundColor(color)
        return this
    }

    fun setImageUrl(viewId: Int, imgUrl: String): BaseRecyclerViewHolder {
        val imageView = retrieveView<ImageView>(viewId)
        if (imageView != null) {
            GlideUtil.loadImage(context, imgUrl, imageView, GlideUtil.GlideEnum.SMALL_IMAGE)
        }
        return this
    }

    fun setBackgroundResource(viewId: Int, resId: Int): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        view?.setBackgroundResource(resId)
        return this
    }


    fun setFocusable(viewId: Int, flag: Boolean): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        if (view != null)
            view.isFocusable = flag
        return this
    }

    fun setClickable(viewId: Int, flag: Boolean): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        if (view != null)
            view.isClickable = flag
        return this
    }


    fun setVisible(viewId: Int, visible: Boolean): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        if (view != null)
            view.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setEnabled(viewId: Int, enabled: Boolean): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        if (view != null)
            view.isEnabled = enabled
        return this
    }

    fun setImageResource(viewId: Int, imageRes: Int): BaseRecyclerViewHolder {
        val imageView = retrieveView<ImageView>(viewId)
        imageView?.setImageResource(imageRes)
        return this
    }

    fun setOnFocusChangeListener(viewId: Int, listener: View.OnFocusChangeListener): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        if (view != null)
            view.onFocusChangeListener = listener
        return this
    }

    fun addTextChangedListener(viewId: Int, textWatcher: TextWatcher): BaseRecyclerViewHolder {
        val view = retrieveView<TextView>(viewId)
        view?.addTextChangedListener(textWatcher)
        return this
    }

    fun setOnClickListener(viewId: Int, onClickListener: View.OnClickListener): BaseRecyclerViewHolder {
        val view = retrieveView<View>(viewId)
        view?.setOnClickListener(onClickListener)
        return this
    }

}
