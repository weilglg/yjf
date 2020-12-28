package cn.ygyg.cloudpayment.modular.payments.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.MyApplication
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.internet.activity.NewAccountActivity
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.modular.payments.contract.PaymentsActivityContract
import cn.ygyg.cloudpayment.modular.payments.entity.CreateOrderResponseEntity
import cn.ygyg.cloudpayment.modular.payments.presenter.PaymentsActivityPresenter
import cn.ygyg.cloudpayment.utils.*
import cn.ygyg.cloudpayment.wxapi.WXPayEntryActivity
import com.alipay.sdk.app.PayTask
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.tencent.mm.opensdk.modelpay.PayReq
import kotlinx.android.synthetic.main.activity_payments.*

class PaymentsActivity :
        BaseMvpActivity<PaymentsActivityContract.Presenter, PaymentsActivityContract.View>(),
        PaymentsActivityContract.View {
    override fun createPresenter(): PaymentsActivityContract.Presenter = PaymentsActivityPresenter(this)

    override fun getContentViewResId(): Int = R.layout.activity_payments
    private var contractCode = ""
    private var deviceCode = ""
    private var meterClassification = ""
    private val companyCode = ConfigUtil.getCompanyCode()
    private var paymentMethod: Constants.PaymentMethod = Constants.PaymentMethod.WX_PAY
    private var amount = ""
    override fun onNewIntent(intent: Intent?) {
        bundle?.let {
            deviceCode = it.getString(Constants.IntentKey.DEVICE_CODE, "")
            meterClassification = it.getString(Constants.IntentKey.METER_CLASS, "")
            mPresenter?.getBindDevice(deviceCode, companyCode)
        } ?: finish()
        initData()
    }

    override fun initViews() {
        HeaderBuilder(this).apply {
            setTitle(R.string.activity_title_payments)
            setLeftImageRes(R.mipmap.back)
        }
        input_amount.clearFocus()
        bundle?.let {
            deviceCode = it.getString(Constants.IntentKey.DEVICE_CODE, "")
            meterClassification = it.getString(Constants.IntentKey.METER_CLASS, "")
            mPresenter?.getBindDevice(deviceCode, companyCode)
        } ?: finish()
        input_amount.filters = arrayOf(DigitsFilter(2))
        ConfigUtil.userIsNfc(false)
    }

    override fun initListener() {
        payments_history.setOnClickListener { toActivity(PaymentsHistoryActivity::class.java) }

        val singleChose = View.OnClickListener { v ->
            if (v != null) {
                input_amount.setText("")
                ViewUtils.hideKeyboard(input_amount)
                amount = v.tag as String

            }
            selector_rmb100.isChecked = selector_rmb100 == v
            selector_rmb300.isChecked = selector_rmb300 == v
            selector_rmb800.isChecked = selector_rmb800 == v
        }

        selector_rmb100.setOnClickListener(singleChose)
        selector_rmb300.setOnClickListener(singleChose)
        selector_rmb800.setOnClickListener(singleChose)

        input_amount.setOnFocusChangeListener { _, hasFocus ->
            input_layout.isSelected = hasFocus
            if (hasFocus) {
                singleChose.onClick(null)
            }
        }
        input_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    amount = s.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        contact_service.setOnClickListener {
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


        payments.setOnClickListener {
            if (checkPay()) {
                mPresenter?.createOrder(
                        amount,
                        contractCode,
                        UserUtil.getUserName(),
                        meterClassification,
                        paymentMethod,
                        "APP")
            }
        }
    }

    override fun initData() {
        selector_rmb100.performClick()
        wx_pay.performClick()
    }

    /**
     * 检查支付参数
     */
    private fun checkPay(): Boolean {
        if (amount.isEmpty()) {
            showToast("充值金额不能为空")

            return false
        }
        if (contractCode.isEmpty()) {
            showToast("支付账户不能为空")
            return false
        }
        return true
    }

    override fun onLoadDeviceSuccess(response: DeviceVM) {
        contractCode = response.contractCode()
        user_name.text = response.userName()
        user_account.text = deviceCode
        address.text = response.deviceAddress()
        account_balance.text = response.deviceBalance()
    }

    override fun onCreateOrderSuccess(response: CreateOrderResponseEntity) {
        when (response.paymentMethod) {
            Constants.PaymentMethod.WX_PAY.string() -> {
                WXPayEntryActivity.amount = amount
                val request = PayReq()
                request.appId = response.weChatRes?.appid
                request.partnerId = response.weChatRes?.partnerid
                request.prepayId = response.weChatRes?.prepayid
                request.packageValue = response.weChatRes?.packageX
                request.nonceStr = response.weChatRes?.noncestr
                request.timeStamp = response.weChatRes?.timestamp.toString()
                request.sign = response.weChatRes?.sign

                WXUtil.mWxApi.sendReq(request) //发送调起微信支付
            }
            Constants.PaymentMethod.ALI_PAY.string() -> {
                val handler = Handler()
                PermissionUtil.Builder()
                        .setContext(MyApplication.getApplication())
                        .addPermissionName(PermissionUtil.READ_PHONE_STATE)
                        .addPermissionName(PermissionUtil.WRITE_EXTERNAL_STORAGE)
                        .setCallback(object : PermissionUtil.CheckPermissionCallback {
                            override fun onGranted() {
                                Thread(Runnable {
                                    val result = PayTask(this@PaymentsActivity).payV2(response.alipayAppRes, true)
                                    handler.postDelayed({
                                        val payResult = AliPayResult(result)
                                        val resultStatus = payResult.resultStatus
                                        // 判断resultStatus 为9000则代表支付成功
                                        startActivity(Intent(this@PaymentsActivity, PaymentsCompleteActivity::class.java).apply {
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

    override fun onLoadDeviceError(err: ApiThrowable) {
        val flag = TextUtils.equals("ER033", err.code) || TextUtils.equals("ER075", err.code) || TextUtils.equals("ER076", err.code) || TextUtils.equals("ER074", err.code) || TextUtils.equals("ER080", err.code)
        if (TextUtils.equals("ER100", err.code)) {
            DefaultPromptDialog.builder()
                    .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                    .setContext(this)
                    .setContentText("缴费户号不可用，请重新绑定")
                    .setAffirmText("确认")
                    .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                        override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                            toActivity(NewAccountActivity::class.java)
                            finish()
                            return super.clickPositiveButton(dialog)
                        }
                    })
                    .build()
                    .show()
        }else{
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
}
