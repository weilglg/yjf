package cn.ygyg.cloudpayment.widget

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import com.lcodecore.tkrefreshlayout.IHeaderView
import com.lcodecore.tkrefreshlayout.OnAnimEndListener

/**
 * 下拉刷新时显示的控件
 */
class ProgressHeaderView constructor(context: Context) : FrameLayout(context), IHeaderView {

    private var pullDownStr = "下拉刷新"
    private var releaseRefreshStr = "释放刷新"
    private var refreshingStr = "正在刷新"
    private var refreshArrow: ImageView
    private var loadingView: ImageView
    private var refreshTextView: TextView

    init {
        val rootView = View.inflate(getContext(), R.layout.layout_refresh_header, null)
        refreshArrow = rootView.findViewById(R.id.iv_arrow)
        refreshTextView = rootView.findViewById(R.id.tv_state)
        loadingView = rootView.findViewById(R.id.iv_loading)
        addView(rootView)
    }

    fun setArrowResource(@DrawableRes resId: Int): ProgressHeaderView {
        refreshArrow.setImageResource(resId)
        return this
    }

    fun setLoadingViewResource(@DrawableRes resId: Int): ProgressHeaderView {
        loadingView.setImageResource(resId)
        return this
    }

    fun setTextColor(@ColorInt color: Int): ProgressHeaderView {
        refreshTextView.setTextColor(color)
        return this
    }

    fun setPullDownStr(pullDownStr1: String): ProgressHeaderView {
        pullDownStr = pullDownStr1
        return this
    }

    fun setReleaseRefreshStr(releaseRefreshStr1: String): ProgressHeaderView {
        releaseRefreshStr = releaseRefreshStr1
        return this
    }

    fun setRefreshingStr(refreshingStr1: String): ProgressHeaderView {
        refreshingStr = refreshingStr1
        return this
    }

    fun setTextVisibility(isVisibility: Boolean = false): ProgressHeaderView {
        refreshTextView.visibility = if (isVisibility) View.VISIBLE else View.GONE
        return this
    }

    override fun getView(): View {
        return this
    }

    override fun onPullingDown(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        if (fraction < 1f) refreshTextView.text = pullDownStr
        if (fraction > 1f) refreshTextView.text = releaseRefreshStr
        refreshArrow.rotation = fraction * headHeight / maxHeadHeight * 180
    }

    override fun onPullReleasing(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        if (fraction < 1f) {
            refreshTextView.text = pullDownStr
            refreshArrow.rotation = fraction * headHeight / maxHeadHeight * 180
            if (refreshArrow.visibility == View.GONE) {
                refreshArrow.visibility = View.VISIBLE
                loadingView.clearAnimation()
                loadingView.visibility = View.GONE
            }
        }
    }

    override fun startAnim(maxHeadHeight: Float, headHeight: Float) {
        refreshTextView.text = refreshingStr
        refreshArrow.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        val animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 500
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = -1
        animation.repeatMode = Animation.RESTART
        loadingView.startAnimation(animation)
    }

    override fun onFinish(listener: OnAnimEndListener) {
        listener.onAnimEnd()
    }

    override fun reset() {
        refreshArrow.visibility = View.VISIBLE
        loadingView.clearAnimation()
        loadingView.visibility = View.GONE
        refreshTextView.text = pullDownStr
    }
}