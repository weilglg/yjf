package cn.ygyg.cloudpayment.modular.login.activity

import android.annotation.SuppressLint
import android.view.View
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants.IntentKey.OPEN_ID
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.home.activity.MainTabActivity
import cn.ygyg.cloudpayment.modular.login.contract.BindingPhoneContract
import cn.ygyg.cloudpayment.modular.login.presenter.BindingPhonePresenter
import cn.ygyg.cloudpayment.utils.SharePreUtil
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.util.ResourceUtil
import kotlinx.android.synthetic.main.activity_binding_phone.*


class BindingPhoneActivity : BaseMvpActivity<BindingPhoneContract.Presenter, BindingPhoneContract.View>(), BindingPhoneContract.View {

    private var sourceType = 0

    override fun createPresenter(): BindingPhoneContract.Presenter = BindingPhonePresenter(this)

    override fun getContentViewResId(): Int = R.layout.activity_binding_phone

    override fun initViews() {
        super.initViews()
        bundle?.apply {
            sourceType = getInt("sourceType", 0)
        }
        if (sourceType == 1) {
            btn_back.visibility = View.VISIBLE
        } else {
            btn_back.visibility = View.INVISIBLE
        }
        edit_binding_phone.filters = arrayOf(mPresenter?.getPhoneInputFilter()) //添加手机号文本框的输入过滤器
        edit_binding_code.addTextChangedListener(mPresenter?.getCodeTextChangeListener())
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            finish()
        }
        btn_binding_code.setOnClickListener {
            //获取验证码
            mPresenter?.getVerificationCode(edit_binding_phone.text.toString())
        }
        btn_binding_confirm.setOnClickListener {
            val phone = edit_binding_phone.text.toString()
            val code = edit_binding_code.text.toString()
            val openId = SharePreUtil.getString(OPEN_ID)
            mPresenter?.confirm(phone, code, openId)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun changeCodeBtnText(aLong: Long) {
        btn_binding_code.text = "${aLong}秒后重新获取"
    }

    override fun changeConfirmBtnState(state: Boolean) {
        btn_binding_confirm.isEnabled = state
        btn_binding_confirm.setBackgroundResource(if (state) R.drawable.shape_stroke_bg_blue else R.drawable.shape_stroke_bg_gray)
    }

    override fun changeCodeBtnState(state: Boolean) {
        btn_binding_code.text = ResourceUtil.getString(getViewContext(), R.string.get_verification_code)
        btn_binding_code.isEnabled = state
        btn_binding_code.setTextColor(ResourceUtil.getColor(getViewContext(), if (state) R.color.text_green_color else R.color.text_hint_color))
    }

    override fun loginSuccess() {
        DefaultPromptDialog.builder()
                .setTitleText("绑定成功")
                .setAffirmText("我知道了")
                .setContentText("微信和手机号绑定成功，并且都能登录")
                .setContext(getViewContext())
                .setButtonOrientation(typeEnum = DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(MainTabActivity::class.java)
                        finish()
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()


    }


}