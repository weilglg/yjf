package com.cn.lib.widget.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.TextView

import com.cn.lib.R
import com.cn.lib.util.DensityUtil
import com.cn.lib.weight.indicator.TabInfo


/**
 * **选项卡式控件**<br></br>
 * 当绑定ViewPager后可以实现下滑线跟随ViewPager的滑动而滑动<br></br>
 * 当不需要绑定ViewPager时，[.init]第三个置为空即可。此时可以重写
 * [.setOnTabChangeListener]方法实现对选项卡切换的监听
 */
class TabIndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), View.OnClickListener, OnFocusChangeListener, OnTouchListener {

    /**
     * 记录当前滑动的距离(以X轴左边为参照)
     */
    private var mCurrentScroll = 0

    /**
     * 选项卡列表
     */
    private var mTabs: List<TabInfo> = mutableListOf()

    /**
     * 选项卡所依赖的ViewPager
     */
    private var mViewPager: ViewPager? = null

    /**
     * 选项卡普通状态下的字体颜色
     */
    private var mTextColorNormal: Int = 0

    /**
     * 选项卡被选中状态下的字体颜色
     */
    private var mTextColorSelected: Int = 0

    /**
     * 普通状态和选中状态下的字体大小
     */
    private var mTextSizeNormal: Int = 0
    private var mTextSizeSelected: Int = 0

    private lateinit var mPaintFooterLine: Paint

    private lateinit var mPaintTabtip: Paint

    /**
     * 滚动条的高度
     */
    private var mFooterLineHeight: Float = 0.toFloat()

    /**
     * 滚动条所占的Tab宽度的比值
     */
    private var mFooterLineWidthRatio = 1

    /**
     * 是否显示滑动条
     */
    private var mIsShowFooterLine = false

    /**
     * 是否显示下划线
     */
    private var mIsShowUnderline = false

    /**
     * 滑动条的颜色
     */
    private var footerColor = TRANSPARENT_COLOR

    /**
     * 滑动条的颜色
     */
    private var underlineColor = TRANSPARENT_COLOR

    /**
     * 点击时Tab的背景颜色
     */
    private var mOnTouchBackgroundColor = -1

    /**
     * 是否显示tab之间的竖线
     */
    private var mIsShowVerticalLine: Boolean = false

    /**
     * Tab之间的数显的宽度
     */
    private var mVerticalLineWidth = 0f

    /**
     * Tab之间竖线的高度占总布局高度的比值
     */
    private var mVerticalLineHeightRatio = 1

    /**
     * Tab之间竖线的颜色
     */
    private var mVerticalLineColor: Int = 0

    /**
     * 当前选项卡的下标，从0开始
     */
    private var mSelectedTab = 0

    private lateinit var mContext: Context

    private var mCurrID = 0

    /**
     * 表示选项卡总共有几个
     */
    private var mTotal = 1

    private var mInflater: LayoutInflater? = null

    private var mPaddingBottom: Int = 0

    private var mPaddingTop: Int = 0

    private var mTabSelectedColor: Int = 0

    var onTabChangeListener: OnTabChangeListener? = null

    private val tabCount: Int
        get() = childCount

    /**
     * 选项卡切换时的监听(当不绑定ViewPager时添加该监听)
     */
    interface OnTabChangeListener {
        /**
         * 被选中的选项卡
         */
        fun onTabChange(index: Int)
    }

    init {
        isFocusable = true
        onFocusChangeListener = this
        initAttrs(context, attrs)
        initDraw(mFooterLineHeight, footerColor)

    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        mContext = context
        val a = context.obtainStyledAttributes(attrs, R.styleable.TabIndicatorView)

        // 字体颜色
        mTextColorNormal = a.getColor(R.styleable.TabIndicatorView_indicator_textColorNormal, BLACK_COLOR)
        mTextColorSelected = a.getColor(R.styleable.TabIndicatorView_indicator_textColorSelected, BLACK_COLOR)

        // 获得滑动条的颜色
        footerColor = a.getColor(R.styleable.TabIndicatorView_indicator_footerColor, FOOTER_COLOR)

        // 获得下划线的颜色
        underlineColor = a.getColor(R.styleable.TabIndicatorView_indicator_underlineColor, UNDERLINE_COLOR)

        // 是否显示下划线
        mIsShowUnderline = a.getBoolean(R.styleable.TabIndicatorView_indicator_underlineVisibility, false)

        // 字体大小
        mTextSizeNormal = a.getDimensionPixelSize(R.styleable.TabIndicatorView_indicator_textSizeNormal, 14)
        mTextSizeSelected = a.getDimensionPixelSize(R.styleable.TabIndicatorView_indicator_textSizeSelected, mTextSizeNormal)

        // 滚动条相关
        mFooterLineHeight = a.getDimension(R.styleable.TabIndicatorView_indicator_footerLineHeight, FOOTER_LINE_HEIGHT)
        mFooterLineWidthRatio = a.getInt(R.styleable.TabIndicatorView_indicator_footerLineWidthRatio, 1)
        mIsShowFooterLine = a.getBoolean(R.styleable.TabIndicatorView_indicator_footerLineVisibility, false)

        // 点击时背景的颜色
        mOnTouchBackgroundColor = a.getColor(R.styleable.TabIndicatorView_indicator_onTouchBackgroundColor, 0)

        mTabSelectedColor = a.getColor(R.styleable.TabIndicatorView_indicator_tabSelectedColor, 0)

        // 竖线相关
        mVerticalLineHeightRatio = a.getInt(R.styleable.TabIndicatorView_indicator_verticalLineHeightRatio, 1)
        mVerticalLineColor = a.getColor(R.styleable.TabIndicatorView_indicator_verticalLineColor, 0)
        mVerticalLineWidth = a.getDimension(R.styleable.TabIndicatorView_indicator_verticalLineWidth, 0f)
        mIsShowVerticalLine = a.getBoolean(R.styleable.TabIndicatorView_indicator_verticalLineVisibility, false)

        mPaddingTop = a.getDimension(R.styleable.TabIndicatorView_indicator_textPaddingTop, 0f).toInt()
        mPaddingBottom = a.getDimension(R.styleable.TabIndicatorView_indicator_textPaddingBottom, 0f).toInt()

        a.recycle()
    }

    /**
     * Initialize draw objects
     */
    private fun initDraw(footerLineHeight: Float, footerColor: Int) {
        // 标题下面的指示线
        mPaintFooterLine = Paint()
        mPaintFooterLine.style = Paint.Style.FILL_AND_STROKE
        mPaintFooterLine.strokeWidth = footerLineHeight
        mPaintFooterLine.color = footerColor
        // 设置抗锯齿
        mPaintFooterLine.isAntiAlias = true
        mPaintFooterLine.style = Style.FILL

        // Tab之间竖线
        mPaintTabtip = Paint()
        mPaintTabtip.style = Style.FILL_AND_STROKE
        mPaintTabtip.strokeWidth = mVerticalLineWidth
        mPaintTabtip.color = mVerticalLineColor

        mInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * 这个是核心函数，选项卡是用canvas画出来的。所有的invalidate方法均会触发onDraw
     * 大意是这样的：当页面滚动的时候，会有一个滚动距离，然后onDraw被触发后， 就会在新位置重新画上滚动条（其实就是画线）
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mTabs.isEmpty()) {
            return
        }
        // 单个选项卡的宽度
        val mPerItemWidth: Float
        val tabID: Int
        val scrollX: Float

        if (mViewPager != null) {
            mPerItemWidth = (width + mViewPager!!.pageMargin) * 1f / mTotal
            tabID = mSelectedTab
            // 下面是计算本次滑动的距离
            scrollX = ((mCurrentScroll - tabID * (width + mViewPager!!
                    .pageMargin)) / mTotal).toFloat()
        } else {
            mPerItemWidth = width * 1f / mTotal
            tabID = mSelectedTab
            // 下面是计算本次滑动的距离
            scrollX = ((mCurrentScroll - tabID * width) / mTotal).toFloat()
        }

        if (mIsShowUnderline) {
            // 绘制默认的下划线
            mPaintFooterLine.color = underlineColor
            canvas.drawRect(0f, height - mFooterLineHeight, width.toFloat(),
                    height.toFloat(), mPaintFooterLine)
        }

        // 如果需要才进行下划线的绘制
        if (mIsShowFooterLine) {
            // 下面就是如何画线了

            // 计算滑动条的宽度
            val footerLineWidth = if (mFooterLineWidthRatio > 0)
                mPerItemWidth / mFooterLineWidthRatio
            else
                mPerItemWidth
            // 计算滑动条离左右的间距
            val offset = (mPerItemWidth - footerLineWidth) / 2
            val leftX: Float
            val rightX: Float
            // 根据选中的Tab的位置计算下划线绘制时的开始与结束位置
            if (mSelectedTab != 0 && mSelectedTab < mTabs.size - 1) {
                // 当选中的Tab在中间时
                leftX = (mSelectedTab * mPerItemWidth + scrollX
                        + mVerticalLineWidth / 2 + offset)
                rightX = ((mSelectedTab + 1) * mPerItemWidth + scrollX
                        - mVerticalLineWidth / 2 - offset)
            } else if (mSelectedTab == mTabs.size - 1) {
                // 当选中的Tag在最后一个时
                leftX = (mSelectedTab * mPerItemWidth + scrollX
                        + mVerticalLineWidth + offset)
                rightX = (mSelectedTab + 1) * mPerItemWidth + scrollX - offset
            } else {
                // 当选中的Tab是第一个时，X轴绘制的终点是一个tab的长度
                leftX = mSelectedTab * mPerItemWidth + scrollX + offset
                rightX = ((mSelectedTab + 1) * mPerItemWidth + scrollX
                        - mVerticalLineWidth / 2 - offset)
            }

            // 计算下滑线在顶部坐标
            val topY = height - mFooterLineHeight
            // 计算下滑线的底部坐标
            val bottomY = height.toFloat()
            // 因为绘制的下滑线可以设置高度所以这边绘制一个实心矩形

            mPaintFooterLine.strokeWidth = mFooterLineHeight
            mPaintFooterLine.color = footerColor

            canvas.drawRect(leftX, topY, rightX, bottomY, mPaintFooterLine)
        }

        // 判断是否显示Tab中间的竖线
        if (mIsShowVerticalLine) {
            // 计算绘制竖线Y轴的开始位置
            val startY = (if (mVerticalLineHeightRatio > 0)
                (height - height / mVerticalLineHeightRatio) / 2
            else
                0).toFloat()
            // 计算绘制竖线Y轴的结束位置
            val stopY = height.toFloat() - startY - mFooterLineHeight
            mTabs.indices
                    .map { it + 1 }
                    .filter { it != mTabs.size }
                    .map {
                        mPerItemWidth * it - 0.5f
                        // 计算分割线X轴的开始位置
                    }
                    .forEach { canvas.drawLine(it, startY, it, stopY, mPaintTabtip) }
        }
    }

    /**
     * 获取指定下标的选项卡的标题
     */
    private fun getTab(pos: Int): String {
        // Set the default Tab
        var tab = "Tab $pos"
        // If the TabProvider exist
        mTabs.let {
            tab = it[pos].name ?: "tab"
        }
        return tab
    }

    /**
     * 获取指定下标的选项卡的图标资源id（如果设置了图标的话）
     */
    private fun getSelectedIcon(pos: Int): Int {
        var ret = 0
        if (mTabs.size > pos) {
            ret = mTabs[pos].selectedIcon
        }
        return ret
    }

    /**
     * 获取指定下标的选项卡的图标资源id（如果设置了图标的话）
     */
    private fun getNormalIcon(pos: Int): Int {
        var ret = 0
        if (mTabs.size > pos) {
            ret = mTabs[pos].normalIcon
        }
        return ret
    }

    // 当页面滚动的时候，重新绘制滚动条
    fun onScrolled(h: Int) {
        mCurrentScroll = if (mViewPager != null) {
            h * width / DensityUtil.getWidthInPx(context)
        } else {
            h
        }
        invalidate()
    }

    // 当页面切换的时候，重新绘制滚动条
    @Synchronized
    fun onSwitched(position: Int) {
        if (mSelectedTab == position) {
            return
        }
        setCurrentTab(position)
        // invalidate();
    }

    // 初始化选项卡
    fun init(startPos: Int, tabs: List<TabInfo>, mViewPager: ViewPager?) {
        removeAllViews()
        this.mViewPager = mViewPager
        this.mTabs = tabs
        if (mTabs.isNotEmpty()) {
            this.mTotal = tabs.size
        }
        var i = 0
        while (mTotal > i) {
            add(getTab(i), getNormalIcon(i))
            i++
        }
        setCurrentTab(startPos)
        // invalidate();
    }

    // 添加选项卡
    fun add(label: String, icon: Int) {
        /* 加载Tab布局 */
        val tabIndicator = mInflater!!.inflate(R.layout.tab_indicator_item,
                this, false)
        // 获得Tab的TextView控件
        val tv = tabIndicator
                .findViewById<TextView>(R.id.tab_title)
        // 设置默认的字体颜色
        tv.setTextColor(mTextColorNormal)
        // 设置上下间距
        tv.setPadding(0, mPaddingTop, 0, mPaddingBottom)
        // 如果使用了自定义个字体大小属性就进行设置字体大小的设置
        if (mTextSizeNormal > 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal.toFloat())
        }
        // 设置文本
        tv.text = label
        // 如果需要显示图片的话就进行图片的显示
        if (icon != 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
        }
        // 给每个Tab布局设置一个ID
        tabIndicator.id = BASE_ID + mCurrID++
        // 设置Tab的点击事件
        tabIndicator.setOnClickListener(this)
        tabIndicator.setOnTouchListener(this)

        // 当有下滑线时设置下离下边距的距离，这样文字和滑动条就不会重叠
        val lP = tabIndicator.layoutParams as LinearLayout.LayoutParams
        lP.gravity = Gravity.CENTER_VERTICAL
        if (mIsShowFooterLine) {
            lP.bottomMargin = mFooterLineHeight.toInt()
        }

        // 将当前Tab加入到自定义的控件中
        addView(tabIndicator)
    }

    override fun onClick(v: View) {
        // 根据ID算出当前点击的Tab是第几个
        val position = v.id - BASE_ID
        setCurrentTab(position)
    }

    // 设置当前选中的选项卡
    @Synchronized
    fun setCurrentTab(index: Int) {
        if (index < 0 || index >= tabCount) {
            return
        }
        // 先将上一个Tab的属性还原
        val oldTab = getChildAt(mSelectedTab)
        oldTab.isSelected = false
        setTabTextSize(oldTab, false, mSelectedTab)

        // 将当前选中的选项卡的下标保存起来
        mSelectedTab = index
        // 将当前选中的Tab的属性设置为选中
        val newTab = getChildAt(mSelectedTab)
        newTab.isSelected = true
        setTabTextSize(newTab, true, index)

        if (mViewPager != null) {
            // 将跟指示器中相对应的Fragment设置为显示状态
            mViewPager!!.setCurrentItem(mSelectedTab, false)
        } else if (onTabChangeListener != null) {
            onTabChangeListener!!.onTabChange(mSelectedTab)
        }

        invalidate()
    }

    /**
     * 根据选项卡的状态设置选项卡的属性
     *
     * @param tab      选项卡布局
     * @param selected 选项卡的状态
     * @param index    选项卡的下标
     */
    private fun setTabTextSize(tab: View, selected: Boolean, index: Int) {
        val tv = tab.findViewById<TextView>(R.id.tab_title)
        // 根据选项卡是否被选中设置不同的属性
        if (selected) {
            // 设置选中时的图片
            tv.setCompoundDrawablesWithIntrinsicBounds(0,
                    getSelectedIcon(index), 0, 0)
            // 设置选中时字体的大小
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeSelected.toFloat())
            // 设置选中时的字体颜色
            tv.setTextColor(mTextColorSelected)
            // 设置选中时的背景颜色
            tab.setBackgroundColor(mTabSelectedColor)
        } else {
            // 设置未选中时的图片，如果有资源的话才进行设置
            if (getNormalIcon(index) > 0) {
                tv.setCompoundDrawablesWithIntrinsicBounds(0,
                        getNormalIcon(index), 0, 0)
            }
            // 设置未选中时字体的大小，如果进行了字体大小的设置
            if (mTextSizeNormal > 0) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal.toFloat())
            }
            // 设置未选中时字体的颜色
            tv.setTextColor(mTextColorNormal)
            // 设置未选中时背景的颜色为透明
            tab.setBackgroundColor(TRANSPARENT_COLOR)
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (v === this && hasFocus && tabCount > 0) {
            getChildAt(mSelectedTab).requestFocus()
            return
        }
        if (hasFocus) {
            var i = 0
            val numTabs = tabCount
            while (i < numTabs) {
                if (getChildAt(i) === v) {
                    setCurrentTab(i)
                    break
                }
                i++
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mIsShowVerticalLine) {
            // 设置每个Tab的偏移量
            val count = childCount
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                val lP = view.layoutParams as LinearLayout.LayoutParams
                when (i) {
                    0 -> lP.rightMargin = mVerticalLineWidth.toInt()
                    count - 1 -> lP.leftMargin = mVerticalLineWidth.toInt()
                    else -> {
                        lP.rightMargin = mVerticalLineWidth.toInt() / 2
                        lP.leftMargin = mVerticalLineWidth.toInt() / 2
                    }
                }

            }
        }
        if (mViewPager != null) {
            if (mCurrentScroll == 0 && mSelectedTab != 0) {
                mCurrentScroll = (width + mViewPager!!.pageMargin) * mSelectedTab
            }
        } else {
            mCurrentScroll = width * mSelectedTab
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && mOnTouchBackgroundColor != -1) {
            v.setBackgroundColor(mOnTouchBackgroundColor)
            // 更改为按下时的背景图片
        } else if (event.action == MotionEvent.ACTION_UP) {
            // 改为抬起时的图片
            v.setBackgroundColor(TRANSPARENT_COLOR)
        }
        return false
    }

    companion object {

        private const val FOOTER_LINE_HEIGHT = 4.0f
        private const val FOOTER_COLOR = -0x3bbb
        private const val UNDERLINE_COLOR = 0x1A000000
        private val TRANSPARENT_COLOR = Color.parseColor("#00000000")

        private val BLACK_COLOR = Color.parseColor("#000000")
        private const val BASE_ID = 0xffff00
    }
}
