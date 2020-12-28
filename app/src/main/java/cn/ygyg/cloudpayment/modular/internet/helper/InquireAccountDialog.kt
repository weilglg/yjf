package cn.ygyg.cloudpayment.modular.internet.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

import cn.ygyg.cloudpayment.R

class InquireAccountDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.dialog_inquire_account)
        window?.let {
            it.decorView?.setPadding(0, 0, 0, 0)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            it.setGravity(Gravity.BOTTOM)
            it.decorView?.setBackgroundColor(Color.TRANSPARENT)
        }
        findViewById<View>(R.id.close).setOnClickListener { dismiss() }
    }
}
