package cn.ygyg.cloudpayment.modular.nfc.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.MyApplication
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.nfc.contract.NfcPaymentsActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.ConversionEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcPaymentEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.ReadDecResponseEntity
import cn.ygyg.cloudpayment.modular.nfc.presenter.NfcPaymentsActivityPresenter
import cn.ygyg.cloudpayment.modular.payments.activity.PaymentsCompleteActivity
import cn.ygyg.cloudpayment.modular.payments.adapter.NfcPaymentsCostAdapter
import cn.ygyg.cloudpayment.utils.*
import cn.ygyg.cloudpayment.wxapi.WXPayEntryActivity
import com.alipay.sdk.app.PayTask
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.tencent.mm.opensdk.modelpay.PayReq
import kotlinx.android.synthetic.main.activity_nfc_payments.*
import kotlinx.android.synthetic.main.item_payments_cost.*

class NfcPaymentsActivity : BaseMvpActivity<NfcPaymentsActivityContract.Presenter, NfcPaymentsActivityContract.View>(),
        NfcPaymentsActivityContract.View{

    override fun createPresenter(): NfcPaymentsActivityContract.Presenter =NfcPaymentsActivityPresenter(this)

    override fun getContentViewResId(): Int = R.layout.activity_nfc_payments

    private var dec: ReadDecResponseEntity? = null

    private var amount = ""
    private var gas = "50"
    private var contractCode = ""
    private var ifAmount = false
    private var paymentMethod: Constants.PaymentMethod = Constants.PaymentMethod.WX_PAY
    private var paymentType:String =""
    private val adapter : NfcPaymentsCostAdapter by lazy { NfcPaymentsCostAdapter() }

    override fun onNewIntent(intent: Intent?) {
        initData()
    }

    override fun initViews() {
        HeaderBuilder(this).apply {
            setTitle(R.string.activity_title_payments)
            setLeftImageRes(R.mipmap.back)
        }
        input_gas.clearFocus()
        bundle?.let {
            dec = it.getSerializable(Constants.IntentKey.READ_DEC) as ReadDecResponseEntity?
        } ?: finish()
        input_gas.filters = arrayOf(DigitsFilter(2))
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        ConfigUtil.userIsNfc(true)
    }

    override fun initListener() {
        nfc_payments_history.setOnClickListener { toActivity(NfcPaymentsHistoryActivity::class.java) }

        val singleChose = View.OnClickListener { v ->
            if (v != null) {
                input_gas.setText("")
                ViewUtils.hideKeyboard(input_gas)
                gas = v.tag as String

                dec?.let {
                    mPresenter?.getCalculate(it.cardNo,it.cardRemark,it.chargeTime.toString(),gas,it.issueTime.toString(),
                            it.contractId.toString(), ifAmount,it.prestored.toString())
                }
            }
            selector_gas50.isChecked = selector_gas50 == v
            selector_gas100.isChecked = selector_gas100 == v
            selector_gas200.isChecked = selector_gas200 == v
        }

        selector_gas50.setOnClickListener(singleChose)
        selector_gas100.setOnClickListener(singleChose)
        selector_gas200.setOnClickListener(singleChose)

        input_gas.setOnFocusChangeListener { _, hasFocus ->
            input_layout.isSelected = hasFocus
            if (hasFocus) {
                singleChose.onClick(null)
            }else{
                if (!input_gas.text.isNullOrEmpty()) {
                    gas = input_gas.text.toString()
                    dec?.let {
                        mPresenter?.getCalculate(it.cardNo,it.cardRemark,it.chargeTime.toString(),gas,it.issueTime.toString(),
                                it.contractId.toString(), ifAmount,it.prestored.toString())
                    }
                }else{
                    showToast("请输入购气量")
                }
            }
        }
        input_gas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    gas = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        detail.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    detail.text = "收起费用明细"
                    nfc_payment_cost.visibility=View.VISIBLE
                    actual_payment_cost.visibility=View.VISIBLE
                }else{
                    detail.text = "展开费用明细"
                    nfc_payment_cost.visibility=View.GONE
                    actual_payment_cost.visibility=View.GONE
                }
            }
        })


        nfc_contact_service.setOnClickListener {
            DefaultPromptDialog.builder()
               .setContext(getViewContext())
               .setAffirmText("呼叫")
               .setCancelText("取消")
               .setContentText("0317-20725341")
               .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                   override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                       val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0317-20725341"))
                       startActivity(dialIntent)
                       return super.clickPositiveButton(dialog)
                   }
               })
               .build()
               .show()
    }

        ali_pay.setOnClickListener {
            wx_pay_select.visibility = View.GONE
            ali_pay_select.visibility = View.VISIBLE
            paymentMethod = Constants.PaymentMethod.ALI_PAY
        }
        wx_pay.setOnClickListener {
            ali_pay_select.visibility = View.GONE
            wx_pay_select.visibility = View.VISIBLE
            paymentMethod = Constants.PaymentMethod.WX_PAY
        }
        nfc_payments.setOnClickListener {
            if (checkPay()) {
                var chargeTime=dec!!.chargeTime!!+1
                mPresenter?.createOrder(amount,gas,dec!!.cardNo.toString(),dec!!.cardRemark.toString(),chargeTime.toString(),
                        dec!!.contractNo.toString(),dec!!.contractId.toString(), dec!!.prestored.toString(),dec!!.issueTime.toString(),
                        paymentType,dec!!.prestored.toString(),amount,contractCode)
            }
        }
}

    override fun initData() {
        user_name.text = dec!!.customerName
        user_account.text = dec!!.realSteelGrade
        address.text = dec!!.houseAddress
        account_balance.text = dec!!.prestored.toString()+"元"
        selector_gas50.performClick()
        wx_pay.performClick()
    }

    /**
     * 检查支付参数
     */
    private fun checkPay(): Boolean {
        contractCode=ConfigUtil.getCompanyCode()
        if (amount.isEmpty()) {
            showToast("充值金额不能为空")
            return false
        }
        if (contractCode.isEmpty()) {
            showToast("支付账户不能为空")
            return false
        }
        if(paymentMethod==Constants.PaymentMethod.WX_PAY){
            paymentType = "WX_APP"
        }else{
            paymentType = "ALI_APP"
        }
        return true
    }

    /**
     *  换算成功回调
     */
    override fun getCalculateSuccsee(result: ConversionEntity) {
        amount=result.realAmount.toString()
        payment_money.text = amount
        nfc_payment_money.text = result.amount.toString()
        nfc_account_money.text = result.prestored.toString()
        nfc_recharge_money.text = amount
        actual_payment_money.text = amount
        adapter.setData(result.priceList!!)
    }

    override fun getCalculateError(err: ApiThrowable) {
        val flag = TextUtils.equals("ER033", err.code) || TextUtils.equals("ER075", err.code) || TextUtils.equals("ER076", err.code) || TextUtils.equals("ER074", err.code) || TextUtils.equals("ER080", err.code)
        val builder = DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setTitleText("提示")
                .setContentText(if (flag) "该缴费户号异常" else err.message)
        val companyInfo = ConfigUtil.getCompanyInfo()
        val telephone = companyInfo?.hotline
        if (flag && telephone != null && telephone != "") {
            builder.setAffirmText("联系客服")
                    .setCancelText("取消")
                    .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                        override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                            showCompanyTelephone(telephone)
                            return super.clickPositiveButton(dialog)
                        }
                    })

        } else {
            builder.setAffirmText("确认")
        }
        builder.build()
                .show()
    }

    override fun onCreateOrderSuccsee(result: NfcPaymentEntity) {
        when (result.paymentMethod) {
            Constants.PaymentMethods.METHOD_WECHAT.string() -> {
                WXPayEntryActivity.amount = amount
                val request = PayReq()
                request.appId = result.weChatAppRes?.appid
                request.partnerId = result.weChatAppRes?.partnerid
                request.prepayId = result.weChatAppRes?.prepayid
                request.packageValue = result.weChatAppRes?.packageX
                request.nonceStr = result.weChatAppRes?.noncestr
                request.timeStamp = result.weChatAppRes?.timestamp.toString()
                request.sign = result.weChatAppRes?.sign
                WXUtil.mWxApi.sendReq(request) //发送调起微信支付
            }
            Constants.PaymentMethods.METHOD_ALIPAY.string() -> {
                val handler = Handler()
                PermissionUtil.Builder()
                        .setContext(MyApplication.getApplication())
                        .addPermissionName(PermissionUtil.READ_PHONE_STATE)
                        .addPermissionName(PermissionUtil.WRITE_EXTERNAL_STORAGE)
                        .setCallback(object : PermissionUtil.CheckPermissionCallback {
                            override fun onGranted() {
                                Thread(Runnable {
                                    val result = PayTask(this@NfcPaymentsActivity).payV2(result.alipayAppRes, true)
                                    handler.postDelayed({
                                        val payResult = AliPayResult(result)
                                        val resultStatus = payResult.resultStatus
                                        // 判断resultStatus 为9000则代表支付成功
                                        startActivity(Intent(this@NfcPaymentsActivity, PaymentsCompleteActivity::class.java).apply {
                                            putExtra(ACTIVITY_BUNDLE, Bundle().apply {
                                                putBoolean(Constants.IntentKey.IS_SUCCESS, TextUtils.equals("9000", resultStatus))
                                                putString(Constants.IntentKey.AMOUNT,amount)
                                            })
                                        })
                                    }, 300)
                                }).start()
                            }
                            override fun onDenied(permissions: List<String>) {
                                showToast("打开支付宝失败")
                            }
                        }).build()
            }
        }
    }

    override fun onCreateOrderError(err: ApiThrowable) {
        val flag = TextUtils.equals("ER033", err.code) || TextUtils.equals("ER075", err.code) ||  TextUtils.equals("ER074", err.code) || TextUtils.equals("ER080", err.code)
        val builder = DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setTitleText("提示")
                .setContentText(if (flag) "该缴费户号异常" else err.message)
        val companyInfo = ConfigUtil.getCompanyInfo()
        val telephone = companyInfo?.hotline
        if (flag && telephone != null && telephone != "") {
            builder.setAffirmText("联系客服")
                    .setCancelText("取消")
                    .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                        override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                            showCompanyTelephone(telephone)
                            return super.clickPositiveButton(dialog)
                        }
                    })
        } else {
            builder.setAffirmText("确认")
        }
        builder.build()
                .show()
    }

    private fun showCompanyTelephone(telephone: String) {
        DefaultPromptDialog.builder()
                .setContext(getViewContext())
                .setAffirmText("呼叫")
                .setCancelText("取消")
                .setContentText(telephone)
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telephone"))
                        startActivity(dialIntent)
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }
}