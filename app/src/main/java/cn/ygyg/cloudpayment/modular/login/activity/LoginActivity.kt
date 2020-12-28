package cn.ygyg.cloudpayment.modular.login.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.InputType.*
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.home.activity.MainTabActivity
import cn.ygyg.cloudpayment.modular.login.contract.LoginContract
import cn.ygyg.cloudpayment.modular.login.entity.UserEntity
import cn.ygyg.cloudpayment.modular.login.presenter.LoginPresenter
import cn.ygyg.cloudpayment.modular.password.activity.ResetPasswordActivity
import cn.ygyg.cloudpayment.modular.register.activity.RegisterActivity
import cn.ygyg.cloudpayment.utils.WXUtil
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.util.ResourceUtil
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.include_activity_header.*


/**
 * 登录
 * Created by Admin on 2019/4/13.
 */
class LoginActivity : BaseMvpActivity<LoginContract.Presenter, LoginContract.View>(), LoginContract.View {

    private var loginType: Int = 1
    private var hashToMainActivity: Int = 0

    override fun getContentViewResId(): Int = R.layout.activity_login

    override fun createPresenter(): LoginContract.Presenter {
        return LoginPresenter(this)
    }

    override fun initViews() {
        super.initViews()
        hashToMainActivity = intent.getIntExtra("isToMainActivity", 0)
        edit_login_code.addTextChangedListener(mPresenter?.getPasswordTextChangeListener())
        edit_login_phone.filters = arrayOf(mPresenter?.getPhoneInputFilter())
        tv_right.text = ResourceUtil.getString(getViewContext(), R.string.register)
        tv_right.setTextColor(ResourceUtil.getColor(getViewContext(), R.color.text_green_color))
        tv_right.visibility = View.VISIBLE
        inputTypeVerificationCode()
        RxBus.get().register(this)
        div_bottom.setBackgroundColor(0x00000000)
    }

    override fun initListener() {
        super.initListener()
        btn_login_type.setOnClickListener {
            edit_login_code.text = null
            if (loginType == 0) { //判断是否是密码登录
                //验证码登录
                inputTypeVerificationCode()
            } else {
                //密码登录
                inputTypePassword()
            }
            mPresenter?.setLoginType(loginType)
        }

        btn_retrieve_password.setOnClickListener {
            //找回密码
            toActivity(ResetPasswordActivity::class.java)
        }
        btn_login_code.setOnClickListener {
            mPresenter?.getVerificationCode(edit_login_phone.text.toString())
        }

        tv_right.setOnClickListener {
            toActivityForResult(getViewContext(), RegisterActivity::class.java, Companion.REQUEST_CODE_REGISTER)
        }
        btn_login.setOnClickListener {
            mPresenter?.login(loginType, edit_login_phone.text.toString(), edit_login_code.text.toString())
        }
        btn_wx_login.setOnClickListener {
            //判断是否安装微信APP,按照微信的说法，目前移动应用上微信登录只提供原生的登录方式，需要用户安装微信客户端才能配合使用。
            if (!WXUtil.isWXAppInstalled(getViewContext())) {
                showToast("请安装微信")
                return@setOnClickListener
            } else {
                val req = SendAuth.Req()
                //应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
                req.scope = "snsapi_userinfo"
                //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
                req.state = "cloudpayment_wx_login"
                //像微信发送请求
                WXUtil.mWxApi.sendReq(req)
            }
        }
        btn_pwd.setOnClickListener {
            if (edit_login_code.inputType != EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) { //显示密码
                edit_login_code.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btn_pwd.setImageResource(R.mipmap.pwd_open)
            } else {//隐藏密码
                edit_login_code.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btn_pwd.setImageResource(R.mipmap.pwd_close)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == Activity.RESULT_OK) {
            data?.let {
                inputTypePassword()
                val userName = it.getStringExtra("userName")
                val password = it.getStringExtra("password")
                edit_login_phone.setText(userName)
                edit_login_code.setText(password)
                mPresenter?.login(0, userName, password)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 密码登录设置密码文本输入类型
     */
    private fun inputTypePassword() {
        loginType = 0
        edit_login_code.apply {
            hint = ResourceUtil.getString(getViewContext(), R.string.input_password)
            addTextChangedListener(mPresenter?.getPasswordTextChangeListener())
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            filters = arrayOf(InputFilter.LengthFilter(20))
        }
        btn_retrieve_password.visibility = View.VISIBLE
        btn_login_code.visibility = View.GONE
        btn_login_type.text = ResourceUtil.getString(getViewContext(), R.string.login_verification_code)
        tv_login_title.text = ResourceUtil.getString(getViewContext(), R.string.login_password)
        btn_pwd.visibility = View.VISIBLE
    }

    /**
     * 验证码登录设置文本输入类型为明文数字
     */
    private fun inputTypeVerificationCode() {
        loginType = 1
        edit_login_code.apply {
            hint = ResourceUtil.getString(getViewContext(), R.string.verification_code)
            addTextChangedListener(mPresenter?.getCodeTextChangeListener())
            inputType = TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(6))
        }
        btn_retrieve_password.visibility = View.INVISIBLE
        btn_login_code.visibility = View.VISIBLE
        btn_login_type.text = ResourceUtil.getString(getViewContext(), R.string.login_password)
        tv_login_title.text = ResourceUtil.getString(getViewContext(), R.string.login_verification_code)
        btn_pwd.visibility = View.GONE
    }

    override fun changeLoginBtnState(state: Boolean) {
        btn_login.isEnabled = state
        btn_login.setBackgroundResource(if (state) R.drawable.shape_stroke_bg_blue else R.drawable.shape_stroke_bg_gray)
    }

    override fun changeCodeBtnState(state: Boolean) {
        btn_login_code.text = ResourceUtil.getString(getViewContext(), R.string.get_verification_code)
        btn_login_code.isEnabled = state
        btn_login_code.setTextColor(ResourceUtil.getColor(getViewContext(), if (state) R.color.text_green_color else R.color.text_hint_color))
    }

    override fun changeCodeBtnText(aLong: Long) {
        btn_login_code.text = "${aLong}秒后重新获取"
    }

    override fun loginSuccess() {
        if (hashToMainActivity == 0) {
            toActivity(MainTabActivity::class.java)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    fun loginByCode(code: String) {
        mPresenter?.loginByCode(code)
    }

    override fun toBindingPhone(entity: UserEntity?) {
        entity?.apply {
            val bundle = Bundle()
            bundle.putInt("sourceType", 1)
            toActivity(BindingPhoneActivity::class.java, bundle)
        }
    }

    override fun errorPassword(e: ApiThrowable) {
        DefaultPromptDialog.builder()
                .setAffirmText("找回密码")
                .setCancelText("确认")
                .setContentText(if (TextUtils.equals("ER014", e.code)) "用户名或密码不正确" else e.message)
                .setContext(getViewContext())
                .setButtonOrientation(typeEnum = DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(ResetPasswordActivity::class.java)
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }

    companion object {
        private const val REQUEST_CODE_REGISTER = 0x11
    }
}