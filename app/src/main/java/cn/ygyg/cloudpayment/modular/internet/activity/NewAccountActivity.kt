package cn.ygyg.cloudpayment.modular.internet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.internet.contract.NewAccountActivityContract
import cn.ygyg.cloudpayment.modular.internet.entity.DeviceResponseEntity
import cn.ygyg.cloudpayment.modular.internet.helper.ConfirmAccountDialog
import cn.ygyg.cloudpayment.modular.internet.helper.InquireAccountDialog
import cn.ygyg.cloudpayment.modular.internet.presenter.NewAccountActivityPresenter
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.modular.register.activity.UserAgreementActivity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import cn.ygyg.cloudpayment.utils.StringUtil
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import kotlinx.android.synthetic.main.activity_new_account.*

class NewAccountActivity : BaseMvpActivity<NewAccountActivityContract.Presenter, NewAccountActivityContract.View>(),
        NewAccountActivityContract.View {

    override fun createPresenter(): NewAccountActivityContract.Presenter = NewAccountActivityPresenter(this)
    override fun getContentViewResId(): Int =R.layout.activity_new_account

    private val headerBuilder: HeaderBuilder by lazy { HeaderBuilder(this) }
    private var deviceCode = ""
    private val companyCode = ConfigUtil.getCompanyCode()

    private val accountDialog: ConfirmAccountDialog by lazy {
        ConfirmAccountDialog(this).apply {
            setOnConformClick(View.OnClickListener {
                mPresenter?.bindDevice(deviceCode, companyCode)
            })
        }
    }

    private val dialog: InquireAccountDialog by lazy { InquireAccountDialog(this) }

    override fun initViews() {
        headerBuilder.setLeftImageRes(R.mipmap.back)
        headerBuilder.setTitle(R.string.activity_title_add_new_account)
        pay_account.requestFocus()
    }

    override fun initListener() {
        read_protocol.setOnClickListener { toActivity(UserAgreementActivity::class.java) }
        agree_protocol.setOnCheckedChangeListener { _, isChecked -> canDoNext(isChecked) }
        next_step.setOnClickListener { v ->
            if (v.isSelected) {
                deviceCode = pay_account.text.toString()
                mPresenter?.getDevice(deviceCode, companyCode)
            }
        }
        input_account_help.setOnClickListener { dialog.show() }
        pay_account.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                canDoNext(!s.isNullOrEmpty())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        pay_cost_company.setOnClickListener {
            //            toActivity(AddressSelectorActivity::class.java)
        }
    }

    override fun initData() {
        pay_cost_company.text = ConfigUtil.getCompanyName()
    }

    override fun onLoadDeviceSuccess(result: DeviceVM, deviceCode: String) {
        accountDialog.setData(result, deviceCode)
        accountDialog.show()
    }

    override fun onBindDeviceSuccess(result: DeviceResponseEntity) {
        toActivity(NewAccountSuccessActivity::class.java, Bundle().apply {
            putString(Constants.IntentKey.DEVICE_CODE, result.meterNo)
            putString(Constants.IntentKey.COMPANY_KEY, result.companyCode())
            putString(Constants.IntentKey.METER_CLASS, result.meterClassification())
            putString(Constants.IntentKey.IC_TYPE, result.geticCardType())
        })
    }

    override fun onBindDeviceError(e: ApiThrowable) {
        val flag = TextUtils.equals("ER033", e.code) || TextUtils.equals("ER075", e.code) || TextUtils.equals("ER076", e.code) || TextUtils.equals("ER074", e.code) || TextUtils.equals("ER080", e.code)
        val builder = DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setTitleText("提示")
                .setContentText(if (flag) "该缴费户号异常" else e.message)
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

    private fun canDoNext(canDo: Boolean) {
        if (canDo) {
            next_step.isSelected = agree_protocol.isChecked && !pay_account.text.isNullOrEmpty()
        } else {
            next_step.isSelected = false
        }
    }
}
