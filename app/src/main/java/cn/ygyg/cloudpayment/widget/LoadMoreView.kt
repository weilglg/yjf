package cn.ygyg.cloudpayment.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView

import com.lcodecore.tkrefreshlayout.IBottomView
import com.lcodecore.tkrefreshlayout.utils.DensityUtil

import cn.ygyg.cloudpayment.R

/**
 * 上拉加载更多时显示的控件
 */

@SuppressLint("AppCompatCustomView")
class LoadMoreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr), IBottomView {

    init {
        val size = DensityUtil.dp2px(context, 24f)
        val params = FrameLayout.LayoutParams(size, size)
        params.gravity = Gravity.CENTER
        layoutParams = params
        setImageResource(R.mipmap.refresh)
    }

    override fun getView(): View {
        return this
    }

    override fun onPullingUp(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        rotation = fraction * headHeight / maxHeadHeight * 180
    }

    override fun startAnim(maxHeadHeight: Float, headHeight: Float) {
        val animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 500
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = -1
        animation.repeatMode = Animation.RESTART
        startAnimation(animation)
    }

    override fun onPullReleasing(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        rotation = fraction * headHeight / maxHeadHeight * 180
    }

    override fun onFinish() {
        clearAnimation()
    }

    override fun reset() {
        clearAnimation()
    }
}
