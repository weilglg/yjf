package cn.ygyg.cloudpayment.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object ViewUtils {
    fun showKeyboard(editText: EditText) {
        //设置可获得焦点
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        //请求获得焦点
        editText.requestFocus()
        //调用系统输入法
        val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun hideKeyboard(editText: EditText) {
        //设置可获得焦点
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        //请求获得焦点
        editText.clearFocus()
        //调用系统输入法
        val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}