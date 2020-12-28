package cn.ygyg.cloudpayment.modular.register.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.view.inputmethod.EditorInfo
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.register.contract.RegisterContract
import cn.ygyg.cloudpayment.modular.register.presenter.RegisterPresenter
import com.cn.lib.util.ResourceUtil
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.util.ToastUtil
import kotlinx.android.synthetic.main.activity_register.*


/**
 * 注册
 * Created by Admin on 2019/4/12.
 */
class RegisterActivity : BaseMvpActivity<RegisterContract.Presenter, RegisterContract.View>(), RegisterContract.View {

    override fun getContentViewResId(): Int = R.layout.activity_register

    override fun createPresenter(): RegisterContract.Presenter = RegisterPresenter(this)

    override fun initViews() {
        super.initViews()
        edit_phone.filters = arrayOf(mPresenter?.getPhoneInputFilter())
        edit_pwd.addTextChangedListener(mPresenter?.getPasswordTextChangeListener())
        edit_code.addTextChangedListener(mPresenter?.getCodeTextChangeListener())
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            this.finish()
        }
        btn_pwd.setOnClickListener {
            if (edit_pwd.inputType != EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) { //显示密码
                edit_pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btn_pwd.setImageResource(R.mipmap.pwd_open)
            } else {//隐藏密码
                edit_pwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btn_pwd.setImageResource(R.mipmap.pwd_close)
            }
        }

        btn_code.setOnClickListener {
            mPresenter?.checkPhone(edit_phone.text.toString())
        }
        btn_agreement.setOnClickListener {
            toActivity(UserAgreementActivity::class.java)
        }
        btn_register.setOnClickListener {
            val phone = edit_phone.text.toString()
            val password = edit_pwd.text.toString()
            val code = edit_code.text.toString()
            mPresenter?.submitRegister(phone, password, code)
        }
    }

    override fun checkPhoneSuccess() {
        mPresenter?.startCountDown()
    }

    override fun changeRegisterBtnState(state: Boolean) {
        btn_register.isEnabled = state
        btn_register.setBackgroundResource(if (state) R.drawable.shape_stroke_bg_blue else R.drawable.shape_stroke_bg_gray)
    }

    @SuppressLint("SetTextI18n")
    override fun changeCodeBtnText(aLong: Long) {
        btn_code.text = "${aLong}秒后重新获取"
    }

    override fun changeCodeBtnState(state: Boolean) {
        if (state) btn_code.text = ResourceUtil.getString(getViewContext(), R.string.get_verification_code)
        btn_code.isEnabled = state
        btn_code.setTextColor(ResourceUtil.getColor(getViewContext(), if (state) R.color.text_green_color else R.color.text_hint_color))
    }

    override fun registerSuccess() {
        ToastUtil.showSuccessToast(getViewContext(), "注册成功")
        val intent = Intent()
        intent.putExtra("userName", edit_phone.text.toString())
        intent.putExtra("password", edit_pwd.text.toString())
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

}