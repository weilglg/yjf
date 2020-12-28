package cn.ygyg.cloudpayment.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import java.util.*

/**
 * 加载 提示框
 *
 * @author jianglihui
 */

object ProgressUtil {
    private var mTimer: Timer? = null
    private var mProgressDialog: Dialog? = null
    private var mSeconds: Int = 0

    private val mHandler = object : Handler(Looper.myLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> dismissProgressDialog()
            }
        }
    }

    fun isShow(): Boolean {
        return mProgressDialog?.isShowing ?: false
    }

    fun cancelTimer() {
        if (mTimer != null) {
            mSeconds = 0
            mTimer?.cancel()
            mTimer = null
        }
    }

    private fun beginTimer(context: Context) {
        cancelTimer()
        mTimer = Timer()
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (mSeconds >= 30) {
                    val msg = Message()
                    msg.what = 0
                    mHandler.sendMessage(msg)
                    mSeconds = 0
                } else {
                    mSeconds++
                }
            }
        }, 0, 1000)
    }

    fun setCancelable() {
        mProgressDialog?.setCancelable(false)
    }

    fun createLoadingDialog(context: Context, msg: String): Dialog {

        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.loading_dialog, null)// 得到加载view
        val layout = v.findViewById<View>(R.id.dialog_view) as LinearLayout// 加载布局
        val spaceshipImage = v.findViewById<View>(R.id.img) as ImageView
        val tipTextView = v.findViewById<TextView>(R.id.tipTextView)// 提示文字
        // 加载动画
        val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation)
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation)
        tipTextView.text = msg// 设置加载信息
        val loadingDialog = Dialog(context, R.style.loading_dialog)// 创建自定义样式dialog
        loadingDialog.setCancelable(false)// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false)// 设置点击屏幕Dialog不消失
        loadingDialog.setContentView(layout, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))// 设置布局
        return loadingDialog

    }

    fun showProgressDialog(context: Context, msg: String) {
        if (isShow()) {
            dismissProgressDialog()
        }
        if (mProgressDialog == null) {
            try {
                mProgressDialog = createLoadingDialog(context, msg)
                mProgressDialog?.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (TextUtils.isEmpty(msg) && isShow()) {
                            cancelTimer()
                            mSeconds = 0
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return@OnKeyListener true
                    }
                    false
                })
                mProgressDialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun dismissProgressDialog() {
        mProgressDialog?.let {
            if (it.isShowing ) {
                it.cancel()
            }
            cancelTimer()
        }
        mProgressDialog = null
    }

    fun onDestroy() {
        cancelTimer()
        mProgressDialog = null
    }

}
