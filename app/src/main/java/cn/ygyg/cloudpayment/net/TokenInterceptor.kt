package cn.ygyg.cloudpayment.net

import android.content.Intent
import cn.ygyg.cloudpayment.app.Constants.IntentKey.TOKEN_KEY
import cn.ygyg.cloudpayment.modular.login.activity.LoginActivity
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.interceptor.BaseExpiredInterceptor
import com.cn.lib.util.ActivityListUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * token刷新
 */
class TokenInterceptor : BaseExpiredInterceptor() {

    /**
     * 判定token过期
     */
    override fun isResponseExpired(response: Response, bodyString: String): Boolean {
        val code = response.code()
        if (code == 401) {
            val currentActivity = ActivityListUtil.INSTANCE.currentActivity()
            currentActivity?.let {
                val intent = Intent(it, LoginActivity::class.java)
                intent.putExtra("isToMainActivity", 1)
                it.startActivity(intent)
            }
            return false
        }
        return false
    }


    override fun responseExpired(chain: Interceptor.Chain, bodyString: String): Response {
        refreshToken()
        val request = chain.request()
        val newBuilder = request.newBuilder()
        newBuilder.addHeader(TOKEN_KEY, UserUtil.getToken())
        return chain.proceed(newBuilder.build())
    }

    /**
     * 同步请求刷新token
     */
    private fun refreshToken() {
        RequestManager.post(UrlConstants.refreshToken)
                .param("", "")
                .isSyncRequest(false)
                .execute("refreshToken", object : ResultCallback<String>() {
                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {

                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {

                    }

                    override fun onSuccess(tag: Any?, result: String?) {
                        result?.let {
                            UserUtil.refreshToken(result)
                        }
                    }
                })

    }
}