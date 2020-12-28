package cn.ygyg.cloudpayment.wxapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.payments.activity.PaymentsCompleteActivity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.WXUtil
import com.cn.lib.basic.BaseActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXPayEntryActivity : BaseActivity(), IWXAPIEventHandler {

    override fun getContentViewResId(): Int = 0

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WXUtil.mWxApi.handleIntent(intent, this)
    }

    companion object {
        var amount = ""
    }

    override fun initData() {
        WXUtil.mWxApi.handleIntent(intent, this)
    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            startActivity(Intent(this, PaymentsCompleteActivity::class.java).apply {
                putExtra(BaseActivity.ACTIVITY_BUNDLE, Bundle().apply {
                    putBoolean(Constants.IntentKey.IS_SUCCESS, BaseResp.ErrCode.ERR_OK == baseResp.errCode)
                    putString(Constants.IntentKey.AMOUNT, amount)
                })
            })
        }
        finish()
    }

    override fun onReq(baseReq: BaseReq) {
    }

}
