package cn.ygyg.cloudpayment.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.ygyg.cloudpayment.BuildConfig
import cn.ygyg.cloudpayment.R

class SideBarView : View {
    private var sideGravity: Int
    private var textSize: Int
    private var textColor: Int
    private var barWidth: Int

    private var previewSize: Int
    private var previewTextSize: Int
    private var previewTextColor: Int

    private var previewRadius: Int
    private var previewBackgroundColor: Int

    private var charPaint: Paint
    private val charRect: Rect by lazy { Rect() }
    private var itemHeight = 0
    private val textRect = Rect()
    private val previewPaint: Paint
    private val previewRect = Rect()
    var onSideBarTouchListener: OnSideBarTouchListener? = null
    var itemData: Array<String>? = null
        set(value) {
            field = value
            postInvalidate()
        }

    private var selectIndex = -1
    private var isTouching = false
    private var touchDown: Boolean = false


    constructor(context: Context) : super(context) {
        initAttrs(null)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initAttrs(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(attrs)
    }


    init {
        sideGravity = 0
        textSize = 12
        textColor = Color.BLACK

        barWidth = (dp2Px(context, textSize.toFloat()) * 1.5).toInt()

        previewSize = dp2Px(this.context, 100f)
        previewBackgroundColor = Color.WHITE

        previewTextColor = Color.BLACK
        previewTextSize = 36
        previewRadius = dp2Px(context, 5f)

        charPaint = Paint()
        previewPaint = Paint()
        if (BuildConfig.DEBUG) {
            itemData = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SideBarView)

            sideGravity = a.getInt(R.styleable.SideBarView_sideGravity, sideGravity)
            textSize = a.getDimension(R.styleable.SideBarView_sideTextSize, textSize.toFloat()).toInt()
            textColor = a.getColor(R.styleable.SideBarView_sideTextColor, textColor)

            barWidth = a.getDimensionPixelOffset(R.styleable.SideBarView_sideBarWidth, barWidth)

            previewSize = a.getDimension(R.styleable.SideBarView_sidePreviewSize, previewSize.toFloat()).toInt()
            previewBackgroundColor = a.getColor(R.styleable.SideBarView_sidePreviewBackgroundColor, previewBackgroundColor)

            previewTextSize = a.getDimensionPixelOffset(R.styleable.SideBarView_sidePreviewTextSize, previewTextSize)
            previewTextColor = a.getColor(R.styleable.SideBarView_sidePreviewTextColor, previewTextColor)
            previewRadius = a.getDimensionPixelOffset(R.styleable.SideBarView_sidePreviewRadius, previewRadius)
            a.recycle()
        }
        charPaint.color = textColor
        charPaint.isAntiAlias = true
        charPaint.textSize = textSize.toFloat()
    }

    companion object {
        private fun dp2Px(context: Context, dp: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = setSpecMode(widthMeasureSpec)
        val height = setSpecMode(heightMeasureSpec)
        super.onMeasure(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            if (sideGravity == 0) {
                charRect.set(
                        Math.max(measuredWidth - paddingLeft - paddingRight - barWidth, paddingLeft),
                        paddingTop,
                        measuredWidth - paddingLeft - paddingRight,
                        measuredHeight - paddingTop - paddingBottom)
            } else {
                charRect.set(paddingLeft,
                        paddingTop,
                        Math.min(paddingLeft + barWidth, measuredWidth - paddingLeft - paddingRight),
                        measuredHeight - paddingTop - paddingBottom)
            }
            itemHeight = if (itemData != null && itemData!!.isNotEmpty()) {
                (charRect.height() * 1.0f / itemData!!.size).toInt()
            } else {
                0
            }
            val maxSize = Math.min(itemHeight, charRect.width())
            charPaint.textSize = Math.min(textSize, maxSize).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (itemHeight != 0 && itemData != null && itemData!!.isNotEmpty()) {
            canvas?.save()
            drawCharacters(canvas)
            canvas?.restore()
        }
        if (isTouching) {
            canvas?.save()
            drawPreview(canvas)
            canvas?.restore()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (itemData != null && itemData!!.isNotEmpty()) {
            val action = event.action
            val touchY = event.y
            val oldChoose = selectIndex
            val newChoose = (touchY / itemHeight).toInt()
            when (action) {
                MotionEvent.ACTION_UP -> {
                    selectIndex = -1
                    if (onSideBarTouchListener != null) {
                        isTouching = false
                        onSideBarTouchListener?.onTouching(isTouching)
                    }
                }
                else -> {
                    //如果是cancel也要调用onLetterUpListener 通知
                    if (event.action == MotionEvent.ACTION_CANCEL) {
                        isTouching = false
                        onSideBarTouchListener?.onTouching(isTouching)
                    } else if (event.action == MotionEvent.ACTION_DOWN) {//按下调用 onLetterDownListener
                        touchDown = event.x > charRect.left && event.x < charRect.right
                        isTouching = touchDown
                        onSideBarTouchListener?.onTouching(isTouching)
                    }
                    if (oldChoose != newChoose) {
                        if (newChoose >= 0 && newChoose < itemData!!.size) {
                            selectIndex = newChoose
                            if (touchDown) {
                                onSideBarTouchListener?.onTouchChanged(itemData!![newChoose], newChoose)
                            }
                        }
                    }
                }
            }
        }

        invalidate()
        return isTouching && touchDown
    }

    /**
     * 绘制 预览
     */
    private fun drawPreview(canvas: Canvas?) {
        itemData?.let {
            if (selectIndex >= 0 && selectIndex < it.size) {
                previewPaint.color = previewBackgroundColor
                val left = (paddingLeft + (width - paddingLeft - paddingRight) / 2 - previewSize / 2).toFloat()
                val top = (paddingTop + (height - paddingTop - paddingBottom) / 2 - previewSize / 2).toFloat()
                val rectF = RectF(left, top, left + previewSize, top + previewSize)
                canvas?.drawRoundRect(rectF, previewRadius.toFloat(), previewRadius.toFloat(), previewPaint)


                val chars = it[selectIndex]
                previewPaint.color = previewTextColor
                previewPaint.textSize = previewTextSize.toFloat()
                previewPaint.getTextBounds(chars, 0, chars.length, previewRect)
                val x = (paddingLeft + ((width - paddingLeft - paddingRight) - previewRect.width()) / 2).toFloat()
                val y = paddingTop + (height - paddingTop - paddingBottom) / 2 - (previewPaint.fontMetrics.top + previewPaint.fontMetrics.bottom) / 2
                canvas?.drawText(chars, x, y, previewPaint)
            }
        }
    }

    /**
     * 绘制侧边条
     */
    private fun drawCharacters(canvas: Canvas?) {
        itemData?.let {
            if (charRect.width() == 0 || charRect.height() == 0) {
                return
            }
            charPaint.color = textColor
            for (index in it.indices) {
                val chars = it[index]
                charPaint.getTextBounds(chars, 0, chars.length, textRect)
                val x = (charRect.left + (Math.max(charRect.width() - textRect.width(), 0) / 2)).toFloat()
                val y = (itemHeight * index + textRect.height()).toFloat()
                canvas?.drawText(chars, x, y, charPaint)
            }
        }
    }

    /**
     * 重设测量模式
     */
    private fun setSpecMode(measureSpec: Int): Int {
        var size = measureSpec
        val mode = MeasureSpec.getMode(size)
        if (mode == MeasureSpec.AT_MOST) {
            size = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(size), MeasureSpec.EXACTLY)
        }
        return size
    }

    interface OnSideBarTouchListener {
        fun onTouchChanged(char: String, position: Int)

        fun onTouching(touching: Boolean)
    }
}