package cn.ygyg.cloudpayment.modular.my.activity

import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import com.cn.lib.basic.BaseActivity

class AboutActivity:BaseActivity() {
    override fun getContentViewResId(): Int = R.layout.activity_about

    override fun initViews() {
        super.initViews()
        HeaderBuilder(this).apply {
            setLeftImageRes(R.mipmap.back)
            setTitle(R.string.about)
        }
    }
}