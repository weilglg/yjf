package com.cn.lib.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan

object ResourceUtil {

    /**
     * 获取颜色
     *
     * @param context
     * @param color
     * @return
     */
    fun getColor(context: Context, color: Int): Int {
        return context.resources.getColor(color)

    }

    /**
     * 获取图片的方法
     *
     * @param context
     * @param res
     * @return
     */
    fun getDrawable(context: Context, res: Int): Drawable {
        return context.resources.getDrawable(res)
    }

    /**
     * 根据名字获取图片资源ID
     * @param context
     * @param resIdName
     * @return
     */
    fun getDrawableIdByResIdName(context: Context, resIdName: String): Int {
        // 根据表情字符串获得静态表情的资源ID
        val resources = context.resources
        var resId = resources.getIdentifier(resIdName, "drawable", context.packageName)
        if (resId == 0) {
            resId = resources.getIdentifier(resIdName, "mipmap", context.packageName)
        }
        return resId
    }


    /**
     * 获取字符串
     *
     * @param context
     * @param res
     * @return
     */
    fun getString(context: Context, res: Int): String {
        return context.resources.getString(res)
    }

    /**
     * 根据string.xml资源格式化字符串
     *
     * @param resource 目标字符串
     * @param args     替换的字符串
     */
    fun formatReplace(context: Context, resource: Int, vararg args: Any): String? {
        val str = context.resources.getString(resource)
        return if (TextUtils.isEmpty(str)) {
            null
        } else String.format(str, *args)
    }

    /**
     * 设置目标字符串中部分字符串的颜色
     *
     * @param changeStr  目标字符串
     * @param sourceStr  需要改变颜色的字符串
     * @param colorResId 颜色资源ID
     * @return SpannableString
     */
    fun changePartStringColor(context: Context, changeStr: String, sourceStr: String, colorResId: Int): SpannableString {
        val spanStr = SpannableString(sourceStr)
        if (TextUtils.isEmpty(changeStr)) {
            return spanStr
        }
        val start = sourceStr.indexOf(changeStr)
        if (start < 0) {
            return spanStr
        }
        val end = start + changeStr.length
        val colorSpan = ForegroundColorSpan(context.resources.getColor(colorResId))
        spanStr.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanStr
    }

    /**
     * 设置目标字符串中部分字符串的颜色
     */
    fun changePartStringColor(context: Context, changeStr: String, colorResId: Int): SpannableString {
        val spanStr = SpannableString(changeStr)
        if (TextUtils.isEmpty(changeStr)) {
            return spanStr
        }
        val start = 0
        val end = changeStr.length
        val colorSpan = ForegroundColorSpan(context.resources.getColor(colorResId))
        spanStr.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanStr
    }

    /**
     * 将目标字符串中的某一段字符串替换为图片
     *
     * @param context
     * @param changeStr
     * @param sourceStr
     * @param imageResId
     * @return
     */
    fun changePartStringImage(context: Context, changeStr: String, sourceStr: String, imageResId: Int): SpannableString {
        val spanStr = SpannableString(sourceStr)
        if (TextUtils.isEmpty(changeStr)) {
            return spanStr
        }
        val start = sourceStr.indexOf(changeStr)
        if (start < 0) {
            return spanStr
        }
        val end = start + changeStr.length
        val imageSpan = ImageSpan(context, imageResId, DynamicDrawableSpan.ALIGN_BOTTOM)
        spanStr.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanStr
    }

    /**
     * 将目标字符串中的某一段字符串替换为图片
     */
    fun changePartStringImage(context: Context, changeStr: String, imageResId: Int): SpannableString {
        val spanStr = SpannableString(changeStr)
        if (TextUtils.isEmpty(changeStr)) {
            return spanStr
        }
        val start = 0
        val end = changeStr.length
        val imageSpan = ImageSpan(context, imageResId, DynamicDrawableSpan.ALIGN_BOTTOM)
        spanStr.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanStr
    }

}
