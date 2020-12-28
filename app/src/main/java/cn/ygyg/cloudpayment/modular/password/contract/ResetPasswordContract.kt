package cn.ygyg.cloudpayment.modular.password.contract

import android.text.InputFilter
import android.text.TextWatcher
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

class ResetPasswordContract {
    interface View:IBaseView{
        fun changeCodeBtnState(state: Boolean)
        fun changeConfirmBtnState(state: Boolean)
        fun changeCodeBtnText(aLong: Long)
        /**
         * 修改密码成功
         */
        fun forgetPasswordSuccess()

    }

    interface Presenter:IBasePresenter<View>{
        fun getPhoneInputFilter(): InputFilter?
        /**
         * 获取验证码
         */
        fun getVerificationCode(phone: String)
        fun getCodeTextChangeListener(): TextWatcher
        fun getPasswordTextChangeListener(): TextWatcher
        /**
         * 修改密码
         */
        fun forgetPwd(code: String, username: String, password: String)
    }


}
