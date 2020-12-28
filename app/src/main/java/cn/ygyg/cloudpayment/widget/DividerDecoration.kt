package cn.ygyg.cloudpayment.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import java.util.ArrayList

import cn.ygyg.cloudpayment.app.MyApplication


/**
 * Created time：2018/12/13 13:34
 * recycler 分割线
 * @author lee
 */
class DividerDecoration private constructor() : RecyclerView.ItemDecoration() {
    private val paint: Paint?
    /**
     * 分割线高度，默认为1px
     */
    private var dividerSize: Int = 0
    /**
     * 分割线颜色  m默认 黑色
     */
    private var dividerColor: Int = 0
    /**
     * 分割线 margin  默认 0
     */
    private var margin: Int = 0
    /**
     * 列表方向 默认 垂直
     */
    private var orientation: Int = 0
    /**
     * 跳过下标的拦截器
     */
    private var continueIntercepts: ArrayList<ContinueIntercept>? = null

    init {
        this.paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
    }

    //获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = parent.getChildAdapterPosition(view)
        if (continueIntercepts == null) {
            continueIntercepts = ArrayList()
            continueIntercepts!!.add(object : ContinueIntercept {
                override fun isContinue(position: Int): Boolean {
                    return position == 0
                }
            })
        }
        if (!isContinue(adapterPosition)) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, dividerSize, 0, 0)
            } else {
                outRect.set(dividerSize, 0, 0, 0)
            }
        }
    }

    private fun isContinue(position: Int): Boolean {
        for (intercept in continueIntercepts!!) {
            if (intercept.isContinue(position)) return true
        }
        return false
    }

    //绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        paint!!.color = dividerColor
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawHorizontal(c, parent)
        } else {
            drawVertical(c, parent)
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + margin
        val right = parent.measuredWidth - parent.paddingRight - margin
        val childSize = parent.childCount
        for (i in 1 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val bottom = child.top - layoutParams.topMargin
            val top = bottom - dividerSize
            if (paint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }

    //绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + margin
        val bottom = parent.measuredHeight - parent.paddingBottom - margin
        val childSize = parent.childCount
        for (i in 1 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val right = child.left - layoutParams.leftMargin
            val left = right - dividerSize
            if (paint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }

    class Builder {
        /**
         * 分割线颜色  m默认 黑色
         */
        private var dividerColor = -0xc0c0d
        /**
         * 分割线高度，默认为1px
         */
        private var dividerSize = dp2Px(0.5f)
        /**
         * 分割线 margin  默认 0
         */
        private var margin = 0
        /**
         * 列表方向 默认 垂直
         */
        private var orientation = LinearLayoutManager.VERTICAL
        /**
         * 跳过下标的拦截器
         */
        private var continueIntercepts: ArrayList<ContinueIntercept>? = null

        fun setDividerSize(dividerSize: Int): Builder {
            this.dividerSize = dividerSize
            return this
        }

        fun setDividerSizeDp(dividerSize: Int): Builder {
            this.dividerSize = dp2Px(dividerSize.toFloat())
            return this
        }

        fun setDividerColor(dividerColor: Int): Builder {
            this.dividerColor = dividerColor
            return this
        }

        fun setMargin(margin: Int): Builder {
            this.margin = margin
            return this
        }

        fun setMarginDp(margin: Int): Builder {
            this.margin = dp2Px(margin.toFloat())
            return this
        }

        fun setOrientation(orientation: Int): Builder {
            this.orientation = orientation
            return this
        }

        fun addContinueIntercept(intercept: ContinueIntercept): Builder {
            if (continueIntercepts == null) {
                continueIntercepts = ArrayList()
            }
            continueIntercepts!!.add(intercept)
            return this
        }

        fun build(): DividerDecoration {
            val d = DividerDecoration()
            d.orientation = this.orientation
            d.dividerSize = this.dividerSize
            d.dividerColor = this.dividerColor
            d.margin = this.margin
            d.continueIntercepts = this.continueIntercepts
            return d
        }
    }

    interface ContinueIntercept {
        fun isContinue(position: Int): Boolean
    }

    companion object {

        fun dividerDefault(): DividerDecoration {
            return Builder().build()
        }


        private fun dp2Px(dp: Float): Int {
            val scale = MyApplication.getApplication().resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }
}
