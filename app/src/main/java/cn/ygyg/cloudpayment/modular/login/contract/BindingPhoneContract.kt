package cn.ygyg.cloudpayment.modular.login.contract

import android.text.InputFilter
import android.text.TextWatcher
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

class BindingPhoneContract {
    interface View:IBaseView{
        fun changeCodeBtnState(state: Boolean)
        fun changeConfirmBtnState(state: Boolean)
        fun changeCodeBtnText(aLong: Long)
        fun loginSuccess()
    }

    interface Presenter:IBasePresenter<View>{
        fun getPhoneInputFilter(): InputFilter?
        /**
         * 获取验证码
         */
        fun getVerificationCode(phone: String)
        fun getCodeTextChangeListener(): TextWatcher
        /**
         * 绑定手机
         */
        fun confirm(phone: String, code: String, openId: String?)
    }


}
