package com.cn.lib.util

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.cn.lib.R


/**
 * @author jianglh-a
 */
object ToastUtil {

    private var isShow = true
    private var oldMsg: String? = null
    private var toast: Toast? = null
    private var oneTime: Long = 0
    private var twoTime: Long = 0
    private var successImageResId = 0
    private var errorImageResId = 0

    fun initResId(@DrawableRes successImageResId: Int, @DrawableRes errorImageResId: Int) {
        this.successImageResId = successImageResId
        this.errorImageResId = errorImageResId
    }

    /**
     * 弹一个Toast
     *
     * @param msgResId 要展示的信息	资源ID
     */
    fun showToast(context: Context, msgResId: Int) {
        val msg = context.getString(msgResId)
        showToast(context, msg)
    }

    /**
     * 设置Toast是否显示
     *
     * @param isBoolean
     */
    fun setShowToast(isBoolean: Boolean) {
        isShow = isBoolean
    }

    /**
     * 可以带图片的Toast
     *
     * @param imageResId 要显示的图片资源ID
     * @param msgResId   要展示信息资源ID
     */
    fun showImageToast(context: Context, imageResId: Int, msgResId: Int) {
        if (isShow) {
            val msg = context.getString(msgResId)
            showImageToast(context, imageResId, msg)
        }
    }

    /**
     * 操作成功后弹出的Toast
     *
     * @param msgResId 要展示信息资源ID
     */
    fun showSuccessToast(context: Context, msgResId: Int) {
        showImageToast(context, successImageResId, msgResId)
    }

    /**
     * 操作成功后弹出的Toast
     *
     * @param msg 要展示信息
     */
    fun showSuccessToast(context: Context, msg: String) {
        showImageToast(context, successImageResId, msg)
    }

    /**
     * 操作失败后弹出的Toast
     *
     * @param msgResId 要展示信息资源ID
     */
    fun showErrorToast(context: Context, msgResId: Int) {
        showImageToast(context, errorImageResId, msgResId)
    }

    /**
     * 操作失败后弹出的Toast
     *
     * @param msg 要展示信息
     */
    fun showErrorToast(context: Context, msg: String) {
        showImageToast(context, errorImageResId, msg)
    }

    /**
     * 可以带图片的Toast
     *
     * @param imageResId 要显示的图片资源ID
     * @param msg        要展示信息
     */
    fun showImageToast(context: Context, imageResId: Int, msg: String) {
        if (isShow) {
            ImageToastUtil.showImageToast(context, imageResId, msg)
        }
    }

    /**
     * 快速点击  防止多次Toast
     *
     * @param msg
     */
    fun showToast(context: Context, msg: String?) {
        if (msg == null || "" == msg) {
            return
        }
        toast?.also {
            twoTime = System.currentTimeMillis()
            if (msg == oldMsg) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    it.show()
                }
            } else {
                oldMsg = msg
                it.setText(msg)
                it.show()
            }
        } ?: also {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast?.let {
                val view = it.view as LinearLayout
                val childAt = view.getChildAt(0) as TextView
                childAt.setTextColor(ResourceUtil.getColor(context, android.R.color.white))
                //设置背景颜色
                view.setBackgroundResource(R.drawable.shape_stroke_black)
                val padding = DensityUtil.dip2px(context, 10f)
                view.setPadding(padding, padding, padding, padding)
                //设置透明度
//                view.background.alpha = 153
                //设置Toast提示消息在屏幕上的位置
                it.setGravity(Gravity.CENTER, 0, 0)
                it.show()
                oneTime = System.currentTimeMillis()
            }
        }
        oneTime = twoTime
    }

    private object ImageToastUtil {
        private var imgToast: Toast? = null
        private var oneClickTime: Long = 0
        private var twoClickTime: Long = 0
        private var imgResId: Int = 0
        private var oldMessage: String? = null

        /**
         * 可以带图片的Toast
         *
         * @param imageResId 要显示的图片资源ID
         * @param msg        要展示信息
         */
        fun showImageToast(context: Context, imageResId: Int, msg: String) {
            if (isShow) {
                // toast为空标示没有进行过Toast的显示
                if (imgToast == null) {
                    imgToast = imageToast(context, imageResId, msg)
                    oneClickTime = System.currentTimeMillis()
                } else {
                    twoClickTime = System.currentTimeMillis()
                    if (msg == oldMessage && imgResId == imageResId) {
                        if (twoClickTime - oneClickTime > Toast.LENGTH_SHORT + 1000 && imgToast != null) {
                            imgToast?.show()
                        }
                    } else {
                        imgToast = imageToast(context, imageResId, msg)
                    }
                }
                oneClickTime = twoClickTime
            }
        }


        /**
         * 可以带图片的Toast
         *
         * @param context         上下文
         * @param imageResourceId 要显示的图片资源ID
         * @param message         要展示信息
         */
        private fun imageToast(context: Context, imageResourceId: Int, message: String?): Toast? {
            if ((message == null || "" == message) && imageResourceId == 0) {
                return null
            }
            imgResId = imageResourceId
            oldMessage = message
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            //设置Toast提示消息在屏幕上的位置
            toast.setGravity(Gravity.CENTER, 0, 0)
            //获取Toast提示消息里原有的View
            val toastView = toast.view as LinearLayout
            //设置圆角背景
            toastView.setBackgroundResource(R.drawable.shape_stroke_black)
            //设置透明度
//            toastView.background.alpha = 153
            //获得文本View
            val childAt = toastView.getChildAt(0) as TextView
            childAt.setTextColor(ResourceUtil.getColor(context, android.R.color.white))
            //获得顶部的间距
            val paddingMid = DensityUtil.dip2px(context, 10f)
            //根据屏幕计算左右padding
            val windowWidth = CommonUtil.getWindowWidth(context)
            //获得内边距
            val padding = DensityUtil.dip2px(context, 10f)
            val params = LinearLayout.LayoutParams(windowWidth * 3 / 5, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER
            params.bottomMargin = padding
            params.topMargin = padding
            childAt.layoutParams = params
            childAt.gravity = Gravity.CENTER
            childAt.setCompoundDrawablesWithIntrinsicBounds(null, ResourceUtil.getDrawable(context, imageResourceId), null, null)
            childAt.compoundDrawablePadding = paddingMid
            toast.show()
            return toast
        }
    }

    fun cancelToast() {
        toast?.cancel()
    }

}
