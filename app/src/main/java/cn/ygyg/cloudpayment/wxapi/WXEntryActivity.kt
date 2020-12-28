package cn.ygyg.cloudpayment.wxapi

import android.annotation.SuppressLint
import android.os.Bundle

import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

import cn.ygyg.cloudpayment.app.MyApplication
import cn.ygyg.cloudpayment.utils.WXUtil
import com.cn.lib.basic.BaseActivity
import com.cn.lib.util.ToastUtil
import com.hwangjr.rxbus.RxBus

/**
 * Created by Admin on 2019/4/19.
 */

class WXEntryActivity : BaseActivity(), IWXAPIEventHandler {

    companion object {
        private val TAG = "WXEntryActivity"
        private val RETURN_MSG_TYPE_LOGIN = 1 //登录
        private val RETURN_MSG_TYPE_SHARE = 2 //分享
    }

    override fun getContentViewResId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //这句没有写,是不能执行回调的方法的
        WXUtil.mWxApi
                .handleIntent(intent, this)
    }


    // 微信发送请求到第三方应用时，会回调到该方法
    override fun onReq(baseReq: BaseReq) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @SuppressLint("CheckResult")
    override fun onResp(baseResp: BaseResp) {
        val type = baseResp.type //类型：分享还是登录
        when (baseResp.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                //用户拒绝授权
                ToastUtil.showToast(getViewContext(), "拒绝授权微信登录")
                //用户取消
                var message = ""
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录"
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享"
                }
                ToastUtil.showToast(getViewContext(), message)
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                var message = ""
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录"
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享"
                }
                ToastUtil.showToast(getViewContext(), message)
            }
            BaseResp.ErrCode.ERR_OK ->
                //用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    val code = (baseResp as SendAuth.Resp).code
                    RxBus.get().post(code)
                    this.finish()
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    ToastUtil.showToast(getViewContext(), "微信分享成功")
                }
        }
        finish()
    }

}
