package cn.ygyg.cloudpayment.utils

import java.util.regex.Pattern

object TextSearchUtils {

    fun contain(source: String, regex: String): Boolean {
        return Pattern.matches(regex, source)
    }

    fun toRegex(s: String): String {
        if (s.isEmpty()) {
            return ""
        }
        val array = s.toCharArray()

        val builder = StringBuilder()
        for (char in array) {
            builder.append(char).append("[A-Za-z]*")
        }
        return builder.toString()
    }
}