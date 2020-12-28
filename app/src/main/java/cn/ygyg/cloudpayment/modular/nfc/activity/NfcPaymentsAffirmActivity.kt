package cn.ygyg.cloudpayment.modular.nfc.activity

import android.text.TextUtils
import android.util.Log
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog.PromptDialogButtonListener
import cn.ygyg.cloudpayment.modular.nfc.contract.NfcAffirmContract
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcWriteDataEntity
import cn.ygyg.cloudpayment.modular.nfc.presenter.NfcAffirmPresenter
import cn.ygyg.cloudpayment.utils.*
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import kotlinx.android.synthetic.main.activity_nfc_payments_affirm.*

class NfcPaymentsAffirmActivity  : BaseMvpActivity<NfcAffirmContract.Presenter, NfcAffirmContract.View>(),
        NfcAffirmContract.View{

    override fun createPresenter(): NfcAffirmContract.Presenter = NfcAffirmPresenter(this)
    override fun getContentViewResId(): Int = R.layout.activity_nfc_payments_affirm

    var bean : NfcWriteDataEntity? = null
    var listBean : NfcHistoryEntity.ListBean? = null

    override fun initViews() {
        super.initViews()
        HeaderBuilder(this).apply {
            setLeftImageRes(R.mipmap.back)
            setTitle(R.string.recharge)
        }
        mPresenter?.loadPageData()
        ProgressUtil.showProgressDialog(this,"系统处理中，请稍后")
    }

    override fun initListener() {
        super.initListener()
        affirm.setOnClickListener {
            if(NfcUtil.checkNFC(this)){
                ProgressUtil.showProgressDialog(this,"系统处理中，请稍后")
                bean?.let {
                    mPresenter?.sendOrder(PosUtils.stringToBcd(bean!!.pszKeyInst))
                }
            }else{
                toActivity(NfcHintActivity::class.java)
                finish()
            }
        }
    }

    override fun sendOrderSuccess(orderCount: Int, request: String?) {
        ProgressUtil.dismissProgressDialog()
        when(orderCount){
            8 -> {
                Log.i("sendOrderSuccess","verfy_pin : "+request)
                mPresenter?.sendOrder(PosUtils.stringToBcd(bean?.pszGasInst))
            }
            9 -> {
                Log.i("sendOrderSuccess","Write_data : "+request)
                mPresenter?.updateStatus(listBean?.rechargeSeq!!)
            }
        }
    }

    override fun sendOrderError(result: String) {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setAffirmText(getString(R.string.i_know_it))
                .setTitleText(getString(R.string.payment_failed))
                .setContentText("请保持手机背面顶部靠近刷卡区\n" +
                        "系统重新充值")
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(NfcHintActivity::class.java)
                        finish()
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }

    override fun onLoadDataError(err: ApiThrowable) {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setAffirmText(getString(R.string.i_know_it))
                .setTitleText(getString(R.string.payment_failed))
                .setContentText("请保持手机背面顶部靠近刷卡区\n" +
                        "系统重新充值")
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(NfcHintActivity::class.java)
                        finish()
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }

    override fun onLoadPageError(err: ApiThrowable) {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setAffirmText(getString(R.string.i_know_it))
                .setTitleText(getString(R.string.payment_failed))
                .setContentText("请保持手机背面顶部靠近刷卡区\n" +
                        "系统重新充值")
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(NfcHintActivity::class.java)
                        finish()
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }

    override fun onLoadPageSuccess(data: NfcHistoryEntity.ListBean) {
        ProgressUtil.dismissProgressDialog()
        this.listBean=data
        ProgressUtil.dismissProgressDialog()
        user_name.text = data.customerName
        user_account.text = data.realSteelGrade
        pay_count.text = data.buyGasSize+"立方米"
        pay_money.text = data.buyGasMoney+"元"
        pay_date.text =data.createDate

        mPresenter?.loadWriteData(listBean?.rechargeSeq!!)
    }

    override fun onLoadDataSuccess(result: NfcWriteDataEntity) {
        ProgressUtil.dismissProgressDialog()
        this.bean=result
    }

    override fun onUpdataSuccess(result: String?) {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setTitleText(getString(R.string.payment_success))
                .setContentText("请尽快将卡插刀燃气表")
                .setAffirmText(getString(R.string.i_know_it))
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        toActivity(NfcPaymentsHistoryActivity::class.java)
                        finish()
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }
}
