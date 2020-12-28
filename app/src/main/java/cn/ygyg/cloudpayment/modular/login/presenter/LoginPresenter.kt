package cn.ygyg.cloudpayment.modular.login.presenter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.Constants.IntentKey.OPEN_ID
import cn.ygyg.cloudpayment.modular.login.contract.LoginContract
import cn.ygyg.cloudpayment.modular.login.entity.TokenEntity
import cn.ygyg.cloudpayment.modular.login.entity.UserEntity
import cn.ygyg.cloudpayment.utils.*
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.subscriber.ResultCallbackSubscriber
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 登录的Presenter
 * Created by Admin on 2019/4/13.
 */
class LoginPresenter(view: LoginContract.View) : BasePresenterImpl<LoginContract.View>(view), LoginContract.Presenter {

    private var isLegalPassword: Boolean = false
    private var isLegalPhone: Boolean = false
    private var isLegalCode: Boolean = false
    private var loginType: Int = 0
    var disposable: Disposable? = null

    override fun setLoginType(type: Int) {
        loginType = type
    }

    /**
     * 开始重新获取验证码的倒计时
     */
    override fun startCountDown() {
        val count = 60
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .take(count + 1L)
                .map { aLong -> count - aLong }
                .doOnSubscribe {
                    mvpView?.changeCodeBtnState(false)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(aLong: Long) {
                        mvpView?.changeCodeBtnText(aLong)
                    }

                    override fun onComplete() {
                        disposable?.dispose()
                        mvpView?.changeCodeBtnState(true)
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    /**
     * 手机号码输入框过滤器
     */
    override fun getPhoneInputFilter(): InputFilter {
        return InputFilter { source, _, end, dest, dstart, dend ->
            var result: CharSequence? = null
            if (dstart == dend && !source.isEmpty()) { //当两者相等说明是输入
                if (dstart == 0 && "1" != source && source.length != 11) { //输入的第一位必须是"1"，如果不是则不添加
                    isLegalPhone = false
                    result = ""
                }
                val count = end + dstart
                if (count == 11) { //当输入的长度达到11位时校验手机号
                    val str = "$dest$source"
                    if (StringUtil.checkCellPhone(str)) {
                        mvpView?.changeCodeBtnState(true)
                        isLegalPhone = true
                    } else {
                        isLegalPhone = false
                        mvpView?.showToast("请输入正确的手机号码")
                    }
                } else if (count > 11) { //已经输入11还在继续输入则不添加
                    result = ""
                }
            } else if (source.isEmpty() && dend > dstart) { //当dend大于dstart时说明时删除操作
                if (dend != dest.length && dstart == 0) { //如果是非尾端删除并且删除的是第一位
                    result = dest.subSequence(dstart, dend)
                } else {
                    disposable?.dispose()
                    mvpView?.changeCodeBtnState(false)
                    isLegalPhone = false
                }
            }
            checkAllInput()
            result
        }
    }

    override fun getCodeFilter(): InputFilter {
        return InputFilter { source, _, end, _, dstart, dend ->
            if (dstart == dend && !source.isEmpty()) { //当两者相等说明是输入
                val count = end + dstart
                if (count == 6) {
                    isLegalCode = true
                    checkAllInput()
                    return@InputFilter null
                }
                isLegalCode = false
                return@InputFilter ""
            }
            isLegalCode = true
            return@InputFilter null
        }
    }

    override fun getCodeTextChangeListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                isLegalCode = s.length == 6
                checkAllInput()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
    }


    override fun getPasswordTextChangeListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val length = s.length
                isLegalPassword = length >= 6
                checkAllInput()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
    }

    /**
     * 校验所有的输入时候符合规定，符合规定时设置按钮可点击
     */
    private fun checkAllInput() {
        mvpView?.changeLoginBtnState(if (loginType == 0) { //密码登录
            isLegalPhone && isLegalPassword
        } else {
            isLegalPhone && isLegalCode
        })
    }

    override fun getVerificationCode(phone: String) {
        RequestManager.post(UrlConstants.captcha)
                .param("phone", phone)
                .execute("getCode", object : ResultCallback<String>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.showProgressDialog(it, "获取验证码中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        ProgressUtil.dismissProgressDialog()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let {
                            mvpView?.showToast(it)
                        }
                    }

                    override fun onSuccess(tag: Any?, result: String?) {
                        mvpView?.showToast("验证码发送成功")
                        //获取验证码成功开始倒计时
                        startCountDown()
                    }
                })
    }

    @SuppressLint("CheckResult")
    override fun login(loginType: Int, username: String, password: String) {
        if (loginType == 0) { //密码登录
            RequestManager.post(UrlConstants.login)
                    .param("password", password)
                    .param("username", username)
                    .execute("login", getLoginCallback())

        } else { //验证码登录
            RequestManager.post(UrlConstants.captchalogin)
                    .param("captcha", password)
                    .param("username", username)
                    .execute("captchaLogin", getLoginCallback())
        }
    }

    @SuppressLint("CheckResult")
    override fun loginByCode(code: String) {
        RequestManager.post(UrlConstants.getToken)
                .param("code", code)
                .param("appId", ConfigUtil.getWXAppId())
                .param("applicationId", ConfigUtil.getApplicationId())
                .param("companyCode", ConfigUtil.getCompanyCode())
                .execute(TokenEntity::class.java)
                .flatMap { optional ->
                    val entity = optional.get()
                    SharePreUtil.putString(OPEN_ID, entity.openid ?: "")
                    RequestManager.post(UrlConstants.getMemberInfo)
                            .param("appId", ConfigUtil.getWXAppId())
                            .param("openId", entity.openid ?: "")
                            .execute(UserEntity::class.java)
                }
                .subscribeWith(ResultCallbackSubscriber("wxLogin", object : ResultCallback<UserEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.getViewContext()?.let {
                            ProgressUtil.showProgressDialog(it, "登录中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        ProgressUtil.dismissProgressDialog()
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let {
                            mvpView?.showToast(it)
                        }
                    }

                    override fun onSuccess(tag: Any?, result: UserEntity?) {
                        result?.let {
                            UserUtil.saveUser(it)
                        }
                        if (result != null && !TextUtils.isEmpty(result.cellPhone)) { //微信第一次登录没有用户信息，需要去绑定手机号生成用户信息
                            mvpView?.loginSuccess()
                        } else {
                            mvpView?.toBindingPhone(result)
                        }
                    }
                }))
    }

    private fun getLoginCallback(): ResultCallback<UserEntity> {
        return object : ResultCallback<UserEntity>() {
            override fun onStart(tag: Any?) {
                mvpView?.getViewContext()?.let {
                    ProgressUtil.showProgressDialog(it, "登录中...")
                }
            }

            override fun onCompleted(tag: Any?) {
                ProgressUtil.dismissProgressDialog()
            }

            override fun onError(tag: Any?, e: ApiThrowable) {
                if (TextUtils.equals("login", tag.toString()) && TextUtils.equals("ER014", e.code)) {
                    mvpView?.errorPassword(e)
                } else {
                    e.message?.let {
                        mvpView?.showToast(it)
                    }
                }

            }

            override fun onSuccess(tag: Any?, result: UserEntity?) {
                if (result != null) { //微信第一次登录没有用户信息，需要去绑定手机号生成用户信息
                    SharePreUtil.putBoolean(Constants.IntentKey.IS_LOGIN, true)
                    UserUtil.saveUser(result)
                    mvpView?.loginSuccess()
                } else {
                    mvpView?.showToast("登录失败")
                }
            }
        }
    }
}