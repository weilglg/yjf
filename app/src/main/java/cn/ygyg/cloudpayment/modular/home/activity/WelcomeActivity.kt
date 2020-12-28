package cn.ygyg.cloudpayment.modular.home.activity

import android.annotation.SuppressLint
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.home.contract.WelcomeContract
import cn.ygyg.cloudpayment.modular.home.presenter.WelcomePresenter
import cn.ygyg.cloudpayment.modular.login.activity.LoginActivity
import cn.ygyg.cloudpayment.utils.UserUtil
import cn.ygyg.cloudpayment.utils.WXUtil
import com.cn.lib.basic.BaseMvpActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.concurrent.TimeUnit

class WelcomeActivity : BaseMvpActivity<WelcomeContract.Presenter, WelcomeContract.View>(), WelcomeContract.View {

    override fun createPresenter(): WelcomeContract.Presenter = WelcomePresenter(this)

    private var isSuccess = false
    override fun getContentViewResId(): Int = R.layout.activity_welcome
    override fun initViews() {
        super.initViews()
//        if (ConfigUtil.isNotEmpty()) {
//            defaultMode()
//        } else {
            mPresenter?.loaderPaymentConfig(this)
//        }
    }

    override fun initListener() {
        super.initListener()
        layout_btn.setOnClickListener {
            if (isSuccess) {
                isSuccess = false
                mPresenter?.loaderPaymentConfig(this)
            }
        }
    }

    override fun completed() {
        isSuccess = true
    }

    /**
     * 如果已经拉取到配置信息则走默认的启动方式
     */
    @SuppressLint("CheckResult")
    private fun defaultMode() {
        mPresenter?.loaderPaymentConfig(this)
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(3)
                .subscribe { t ->
                    if (t == 2L) {
                        jumpPage()
                    }
                }
    }

    /**
     * 跳转页面
     */
    override fun jumpPage() {
        initAppId()
        when {
            UserUtil.isLogin() -> toActivity(MainTabActivity::class.java)
//            !TextUtils.isEmpty(UserUtil.getToken()) -> toActivity(BindingPhoneActivity::class.java)
            else -> toActivity(LoginActivity::class.java)
        }
        this.finish()
    }

    /**
     * 初始化微信支付宝appId
     */
    private fun initAppId() {
        WXUtil.registerToWX()
    }

    /**
     * 加载失败
     */
    override fun loaderConfigError() {
        isSuccess = true
        showToast("配置信息拉取失败，点击重试")
    }
}