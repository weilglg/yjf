package cn.ygyg.cloudpayment.modular.payments.activity

import android.view.View
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.home.activity.MainTabActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcHintActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcPaymentsAffirmActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcPaymentsHistoryActivity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import com.cn.lib.basic.BaseActivity
import kotlinx.android.synthetic.main.activity_payments_complete.*

class PaymentsCompleteActivity : BaseActivity() {

    override fun getContentViewResId(): Int = R.layout.activity_payments_complete
    var icCardType: String? = null

    override fun initViews() {
        var isSuccess = false
        bundle?.let {
            isSuccess = it.getBoolean(Constants.IntentKey.IS_SUCCESS)
            amount_num.text = it.getString(Constants.IntentKey.AMOUNT)
            icCardType =if(ConfigUtil.isUserNfc()!!) "02" else "01"
        }
        val builder = HeaderBuilder(this)
        builder.setLeftImageRes(R.mipmap.back)
        if (isSuccess) {
            if(icCardType=="02"){
                back_to_main.visibility=View.GONE
                to_payments_history.text = "确定"
            }else{
                to_payments_history.setText(R.string.to_payments_history)
            }
            state_icon.setImageResource(R.mipmap.icon_success)
            to_payments_history.visibility = View.VISIBLE
            payments_amount.visibility = View.VISIBLE
            payment_again.visibility = View.GONE
            builder.setTitle(R.string.payment_success)
        } else {
            state_icon.setImageResource(R.mipmap.icon_failed)
            state_name.setText(R.string.payment_failed)
            to_payments_history.visibility = View.GONE
            payments_amount.visibility = View.INVISIBLE
            payment_again.visibility = View.VISIBLE
            builder.setTitle(R.string.payment_failed)
        }

    }

    override fun initListener() {
        to_payments_history.setOnClickListener {
            if(icCardType=="02") toActivity(NfcPaymentsAffirmActivity::class.java)
            else toActivity(PaymentsHistoryActivity::class.java)
        }
        payment_again.setOnClickListener {toActivity(PaymentsActivity::class.java)
        }
        back_to_main.setOnClickListener { toActivity(MainTabActivity::class.java) }
    }
}
