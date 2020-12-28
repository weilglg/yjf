package cn.ygyg.cloudpayment.utils

import android.app.Activity
import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.ygyg.cloudpayment.R

class HeaderBuilder(context: Context) : BaseViewBuilder(context) {
    private lateinit var title: TextView
    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private lateinit var ivLeft: ImageView
    private lateinit var ivRight: ImageView
    private lateinit var divBottom: View

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return parent.findViewById(R.id.include_header_layout)
    }

    override fun initView() {
        title = findViewById(R.id.tv_title)

        tvLeft = findViewById(R.id.tv_left)
        ivLeft = findViewById(R.id.iv_left)

        tvRight = findViewById(R.id.tv_right)
        ivRight = findViewById(R.id.iv_right)
        divBottom = findViewById(R.id.div_bottom)
        var leftOnClick = View.OnClickListener {
            if (getContext() is Activity) {
                var activity = getContext() as Activity
                activity.finish()
            }
        }
        tvLeft.setOnClickListener(leftOnClick)
        ivLeft.setOnClickListener(leftOnClick)
    }

    fun setRightOnclickListener(l: View.OnClickListener): HeaderBuilder {
        tvRight.setOnClickListener(l)
        ivRight.setOnClickListener(l)
        return this
    }

    fun setRightOnclickListener(l: (v: View) -> Unit): HeaderBuilder {
        tvRight.setOnClickListener(l)
        ivRight.setOnClickListener(l)
        return this
    }
//    fun setRightOnclickListener(l: View.OnClickListener): HeaderBuilder {
//        tvRight.setOnClickListener(l)
//        ivRight.setOnClickListener(l)
//        return this
//    }

    fun setLeftImageRes(@DrawableRes resId: Int): HeaderBuilder {
        ivLeft.visibility = if (resId == 0) View.GONE else View.VISIBLE
        ivLeft.setImageResource(resId)
        return this
    }

    fun setTitle(text: String) {
        title.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        title.text = text
    }

    fun setTitle(@StringRes resId: Int) {
        title.visibility = if (resId == 0) View.GONE else View.VISIBLE
        title.setText(resId)
    }

    fun setRightText(text: String): HeaderBuilder {
        tvRight.text = text
        title.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        return this
    }

    fun setRightTextColor(color: Int): HeaderBuilder {
        tvRight.setTextColor(color)
        return this
    }

    fun setBottomLine(b: Boolean): HeaderBuilder {
        if (b) {
            divBottom.visibility = View.VISIBLE
        } else {
            divBottom.visibility = View.INVISIBLE
        }
        return this
    }


}