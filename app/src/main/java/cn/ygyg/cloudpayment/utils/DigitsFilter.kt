package cn.ygyg.cloudpayment.utils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.DigitsKeyListener

class DigitsFilter : DigitsKeyListener {
    private var digits = 2

    constructor(digits: Int) : super(false, true) {
        this.digits = digits
    }

    constructor() : super(false, true)

    fun setDigits(d: Int): DigitsFilter {
        digits = d
        return this
    }

    override fun filter(source: CharSequence, start: Int, end: Int,
                        dest: Spanned, dstart: Int, dend: Int): CharSequence {
        var sourceVar = source
        var startVar = start
        var endVar = end
        val out = super.filter(sourceVar, startVar, endVar, dest, dstart, dend)


        // if changed, replace the source
        if (out != null) {
            sourceVar = out
            startVar = 0
            endVar = out.length
        }

        val len = endVar - startVar

        // if deleting, source is empty
        // and deleting can't break anything
        if (len == 0) {
            return sourceVar
        }

        //以点开始的时候，自动在前面添加0
        if (sourceVar.toString() == "." && dstart == 0) {
            return "0."
        }
        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (sourceVar.toString() != "." && dest.toString() == "0") {
            return ""
        }

        val dlen = dest.length

        // Find the position of the decimal .
        for (i in 0 until dstart) {
            if (dest[i] == '.') {
                // being here means, that a number has
                // been inserted after the dot
                // check if the amount of digits is right
                return if (dlen - (i + 1) + len > digits)
                    ""
                else
                    SpannableStringBuilder(sourceVar, startVar, endVar)
            }
        }

        for (i in startVar until endVar) {
            if (sourceVar[i] == '.') {
                // being here means, dot has been inserted
                // check if the amount of digits is right
                return if (dlen - dend + (endVar - (i + 1)) > digits)
                    ""
                else
                    break  // return new SpannableStringBuilder(source, start, end);
            }
        }


        // if the dot is after the inserted part,
        // nothing can break
        return SpannableStringBuilder(sourceVar, startVar, endVar)
    }
}
