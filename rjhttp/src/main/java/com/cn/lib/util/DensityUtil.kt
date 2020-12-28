package com.cn.lib.util

import android.content.Context
import com.cn.lib.util.DensityUtil.px2dip

/**
 * Created by admin on 2017/12/27/027.
 */

object DensityUtil {

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为dp值，保证尺寸大小不变
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }


    fun getHeightInPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getWidthInPx(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getHeightInDp(context: Context): Int {
        val height = context.resources.displayMetrics.heightPixels.toFloat()
        return px2dip(context, height)
    }

    fun getWidthInDp(context: Context): Int {
        val width = context.resources.displayMetrics.widthPixels.toFloat()
        return px2dip(context, width)
    }

}
