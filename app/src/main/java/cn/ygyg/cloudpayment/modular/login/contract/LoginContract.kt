package cn.ygyg.cloudpayment.modular.login.contract

import android.text.InputFilter
import android.text.TextWatcher
import cn.ygyg.cloudpayment.modular.login.entity.UserEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView
import com.cn.lib.retrofit.network.exception.ApiThrowable

/**
 * Created by Admin on 2019/4/13.
 */
class LoginContract {
    interface View : IBaseView {
        fun changeLoginBtnState(state: Boolean)
        fun changeCodeBtnState(state: Boolean)
        fun changeCodeBtnText(aLong: Long)
        /**
         * 登录成功
         */
        fun loginSuccess()

        /**
         * 跳转绑定手机号码页面
         */
        fun toBindingPhone(entity: UserEntity?)

        /**
         * 密码登陆失败
         */
        fun errorPassword(e: ApiThrowable)

    }

    interface Presenter : IBasePresenter<View> {

        fun startCountDown()
        fun getPhoneInputFilter(): InputFilter
        fun getCodeTextChangeListener(): TextWatcher
        fun getPasswordTextChangeListener(): TextWatcher
        fun setLoginType(type: Int)
        fun getCodeFilter(): InputFilter
        /**
         * 获取验证码
         */
        fun getVerificationCode(phone: String)

        /**
         * 登录
         */
        fun login(loginType: Int, username: String, password: String)

         fun loginByCode(code: String)
    }
}