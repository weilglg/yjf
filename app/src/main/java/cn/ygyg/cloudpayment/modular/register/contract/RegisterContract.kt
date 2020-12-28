package cn.ygyg.cloudpayment.modular.register.contract

import android.text.InputFilter
import android.text.TextWatcher
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

/**
 * Created by Admin on 2019/4/12.
 */
class RegisterContract {
    interface View : IBaseView {
        fun checkPhoneSuccess()
        fun changeCodeBtnState(state: Boolean)
        fun changeCodeBtnText(aLong: Long)
        fun changeRegisterBtnState(state: Boolean)
        fun registerSuccess()
    }

    interface Presenter : IBasePresenter<View> {
        fun checkPhone(phone: String)
        fun startCountDown()
        fun getPasswordTextChangeListener(): TextWatcher
        fun getCodeTextChangeListener(): TextWatcher
        fun getPhoneInputFilter(): InputFilter
        fun submitRegister(phone: String, password: String, code: String)
    }
}