package cn.ygyg.cloudpayment.utils

import android.text.TextUtils
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.Constants.IntentKey.TOKEN_KEY
import cn.ygyg.cloudpayment.modular.login.entity.UserEntity

object UserUtil {
    private var userEntity: UserEntity? = null
    private var token: String? = null

    private fun checkUserEmpty() {
        if (userEntity == null) {
            userEntity = SharePreUtil.getBeanByFastJson<UserEntity>(Constants.IntentKey.USER_INFO)
        }
        if (token == null) {
            token = SharePreUtil.getString(Constants.IntentKey.TOKEN_KEY)
        }
    }

    fun saveUser(user: UserEntity) {
        userEntity = user
        token = user.token
        token?.let {
            SharePreUtil.putString(TOKEN_KEY, it)
        }
        SharePreUtil.saveBeanByFastJson(Constants.IntentKey.USER_INFO, user)
    }

    fun refreshToken(refreshToken: String) {
        this.token = refreshToken
        this.userEntity?.token = refreshToken
        SharePreUtil.putString(TOKEN_KEY, refreshToken)
    }

    fun getUser(): UserEntity? {
        checkUserEmpty()
        return userEntity
    }

    fun getUserName(): String {
        checkUserEmpty()
        return userEntity?.cellPhone ?: ""
    }

    fun getMemberId(): String {
        checkUserEmpty()
        return userEntity?.mid ?: ""
    }

    fun isLogin(): Boolean {
        checkUserEmpty()
        return !TextUtils.isEmpty(userEntity?.mid)
    }

    fun getToken(): String {
        checkUserEmpty()
        return token ?: ""
    }

    fun clear() {
        userEntity = null
        token = null
    }

}
