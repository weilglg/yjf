package cn.ygyg.cloudpayment.modular.register.activity

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebSettings
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import com.cn.lib.util.ResourceUtil
import com.cn.lib.basic.BaseActivity
import kotlinx.android.synthetic.main.activity_user_agreement.*
import kotlinx.android.synthetic.main.activity_user_agreement.view.*
import kotlinx.android.synthetic.main.include_activity_header.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * 用户协议
 * Created by Admin on 2019/4/13.
 */
class UserAgreementActivity : BaseActivity() {

    override fun getContentViewResId(): Int = R.layout.activity_user_agreement

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        super.initViews()
        val headerBuilder = HeaderBuilder(this)
        headerBuilder.setTitle(ResourceUtil.getString(getViewContext(), R.string.user_agreement))
        headerBuilder.setLeftImageRes(R.mipmap.back)

        val wSet:WebSettings = webView.settings
        wSet.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        wSet.loadWithOverviewMode = true
        wSet.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/license.html")
    }

    override fun initListener() {
        super.initListener()
        iv_left.setOnClickListener {
            this.finish()
        }
    }

    /**
     * 从文件中获取字符串
     *
     * @param fileName
     * @param context
     * @return
     */
    fun getString(fileName: String, context: Context): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(InputStreamReader(assetManager.open(fileName)))
            var line: String?
            line = bf.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bf.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }
}