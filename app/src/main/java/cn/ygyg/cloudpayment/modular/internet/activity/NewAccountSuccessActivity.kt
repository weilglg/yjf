package cn.ygyg.cloudpayment.modular.internet.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.home.activity.MainTabActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcHintActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcPaymentsActivity
import cn.ygyg.cloudpayment.modular.payments.activity.PaymentsActivity
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import cn.ygyg.cloudpayment.utils.NfcUtil
import com.cn.lib.basic.BaseActivity
import kotlinx.android.synthetic.main.activity_new_account_success.*

class NewAccountSuccessActivity : BaseActivity() {

    override fun getContentViewResId(): Int = R.layout.activity_new_account_success

    private var deviceCode = ""
    private var companyCode = ""
    private var meterClassification = ""
    private var icType = ""

    override fun initViews() {
        HeaderBuilder(this).apply {
            setTitle(R.string.activity_title_new_account_success)
            setLeftImageRes(R.mipmap.back)
        }
        bundle?.let {
            deviceCode = it.getString(Constants.IntentKey.DEVICE_CODE, "")
            companyCode = it.getString(Constants.IntentKey.COMPANY_KEY, "")
            meterClassification = it.getString(Constants.IntentKey.METER_CLASS, "")
            icType = it.getString(Constants.IntentKey.IC_TYPE, "")
        }
    }

    override fun initListener() {

        to_payments.setOnClickListener {
             if(icType=="02"){
                 if(NfcUtil.checkNFC(this)){
                     toActivity(NfcHintActivity::class.java)
                 }
             }else{
                toActivity(PaymentsActivity::class.java, Bundle().apply {
                    putString(Constants.IntentKey.DEVICE_CODE, deviceCode)
                    putString(Constants.IntentKey.COMPANY_KEY, companyCode)
                    putString(Constants.IntentKey.METER_CLASS, meterClassification)
                })
            }
        }

        back_to_main.setOnClickListener {
            toActivity(MainTabActivity::class.java)
        }
    }
}
