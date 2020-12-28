package cn.ygyg.cloudpayment.widget

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.ygyg.cloudpayment.R

class EmptyView : RelativeLayout {

    private val imageView: ImageView
    private val textView: TextView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        View.inflate(context, R.layout.layout_empty, this)
        imageView = findViewById(R.id.image)
        textView = findViewById(R.id.text_view)
    }

    fun setEmptyText(text: String?) {
        textView.text = text
    }

    fun setEmptyText(@StringRes resId: Int) {
        textView.setText(resId)
    }

    fun setEmptyImageResource(@DrawableRes resId: Int) {
        imageView.setImageResource(resId)
    }

    fun setEmotyIamgeBitmap(bmp: Bitmap?) {
        imageView.setImageBitmap(bmp)
    }

    fun showNoHistory() {
        setEmptyText(R.string.no_history)
        setEmptyImageResource(R.mipmap.image_empty_history)
    }
    fun showNoData() {
        setEmptyText(R.string.no_data)
        setEmptyImageResource(R.mipmap.image_empty_data)
    }
}
