package cn.ygyg.cloudpayment.utils

import android.text.InputFilter
import android.text.Spanned

/**
 * Constructor.
 *
 * @param decimalDigits maximum decimal digits
 */
class DecimalDigitsInputFilter(private val decimalDigits: Int) : InputFilter {


    /**
     * @param source 待输入字符
     * @param start  输入字符开始位置
     * @param end    带输入字符结束位置
     * @param dest   原字符
     * @param dstart 原字符被替换开始位置
     * @param dend   原字符被替换结束位置
     * @return 待输入字符
     */
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        var change = source.toString()
        val data = dest.toString()
        val newString = data.substring(0, dstart) + change.substring(start, end) + data.substring(dend, data.length)
        val index = getPointIndex(newString)
        if (index == -1) return null
        val pointOffset = if (decimalDigits <= 0) 0 else decimalDigits + 1
        if (change.length == 1) {
            if (decimalDigits <= 0) return ""
            if (newString.length > index + pointOffset + 1) return ""
        } else {
            val changeIndex = getPointIndex(change)
            if (changeIndex == -1) return ""
            //去除原数据小数点后数据最多可保留位数
            val maxOffset = Math.max(pointOffset - data.length + dend, 0)
            change = change.substring(0, Math.min(changeIndex + maxOffset, change.length))
            return change
        }
        return null
    }

    private fun getPointIndex(s: String): Int {
        val index = s.indexOf(DECIMAL_POINT_1)
        return if (index != -1) index else s.indexOf(DECIMAL_POINT_2)
    }

    companion object {

        private val DECIMAL_POINT_1 = "."
        private val DECIMAL_POINT_2 = ","
    }

}