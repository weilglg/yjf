package cn.ygyg.cloudpayment.modular.nfc.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.nfc.contract.NfcHintActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.ReadDecResponseEntity
import cn.ygyg.cloudpayment.modular.nfc.presenter.NfcHintActivityPresenter
import cn.ygyg.cloudpayment.utils.*
import cn.ygyg.cloudpayment.utils.SharePreUtil.putString
import com.cn.lib.basic.BaseMvpActivity
import com.cn.lib.retrofit.network.exception.ApiThrowable
import kotlinx.android.synthetic.main.activity_nfc_hint.*

class NfcHintActivity : BaseMvpActivity<NfcHintActivityContract.Presenter, NfcHintActivityContract.View>(), NfcHintActivityContract.View{

    override fun createPresenter(): NfcHintActivityContract.Presenter = NfcHintActivityPresenter(this)

    override fun getContentViewResId(): Int = R.layout.activity_nfc_hint

    private var metrNo:String?=null

    override fun initViews() {
        HeaderBuilder(this).apply {
            setLeftImageRes(R.mipmap.back)
        }
        bundle?.let {
            metrNo=it.getString(Constants.IntentKey.DEVICE_CODE)
        }
        if(NfcUtil.checkNFC(this)){
            NfcUtil.NfcInit(this)
        }
    }

    override fun initListener() {
        slot_card.setOnClickListener {}
    }

    /**
     * 发送指令成功
     */
    override fun sendOrderSuccess(orderCount:Int, result: String) {
        when(orderCount){
            0 ->{
                Log.i("sendOrderSuccess","SELECT : "+result)
                mPresenter?.sendOrder(null)
            }
            1 ->{
                Log.i("sendOrderSuccess","READ_UUID : "+result)
                mPresenter?.sendOrder(null)
            }
            2 ->{
                Log.i("sendOrderSuccess","GET_CHALLENGE : "+result)
                mPresenter?.sendOrder(PosUtils.stringToBcd(result))
            }
            3 ->{
                Log.i("sendOrderSuccess","EXTERANL_AUTHENTICATION : "+result)
                mPresenter?.sendOrder(null)
            }
            4 ->{
                Log.i("sendOrderSuccess","SET_SECURE_CHANNEL_FIRST : "+result)
                mPresenter?.getFirstVerify(result)
            }
            5 ->{
                Log.i("sendOrderSuccess","SET_SECURE_CHANNEL_SECOND : "+result)
                mPresenter?.getSecondVerify(result)
            }
            6 -> {
                Log.i("sendOrderSuccess","SET_SECURE_CHANNEL_THRID : "+result)
                mPresenter?.sendOrder(null)
            }
            7 -> {
                Log.i("sendOrderSuccess","READ_MZ : "+result)
                mPresenter?.getContractInformation(result)
            }

            1001 -> {
                Log.i("sendOrderSuccess","loadfirstVerify : "+result)
                mPresenter?.sendOrder(PosUtils.stringToBcd(result))
            }
            1002 -> {
                Log.i("sendOrderSuccess","loadsecondVerify : "+result)
                mPresenter?.sendOrder(PosUtils.stringToBcd(result))
            }
            1003 -> {
                Log.i("sendOrderSuccess","loadfirstVerify : "+result)
            }
        }
    }

    /**
     * 发送指令失败
     */
    override fun sendOrderError() {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setContext(getViewContext())
                .setAffirmText(getString(R.string.i_know_it))
                .setContentText("抱歉，您的卡片读取异常，请到营业厅处理")
                .build()
                .show()

    }

    override fun onFirstVerifyError(data: String, err: ApiThrowable) {
        ProgressUtil.dismissProgressDialog()
        DefaultPromptDialog.builder()
                .setContext(getViewContext())
                .setAffirmText(getString(R.string.i_know_it))
                .setContentText("网络异常，请重试")
                .build()
                .show()
    }

    override fun onSecondVerifyError(data: String, err: ApiThrowable) {
        ProgressUtil.dismissProgressDialog()
        val flag = TextUtils.equals("ER001",err.code)
        if(flag){
            mPresenter?.getFirstVerify(data)
        }else{
            DefaultPromptDialog.builder()
                    .setContext(getViewContext())
                    .setAffirmText(getString(R.string.i_know_it))
                    .setContentText("网络异常，请重试")
                    .build()
                    .show()
        }
    }

    /**
     *  获取合约信息成功回调
     */
    override fun onInformationSuccess(result: ReadDecResponseEntity?) {
        ProgressUtil.dismissProgressDialog()
        if(!result!!.isWriteCardFlag){
            toActivity(NfcPaymentsAffirmActivity::class.java, Bundle().apply {
                putSerializable(Constants.IntentKey.READ_DEC, result)
            })
            finish()
        }else if(!result.isOnMeterFlag){
            DefaultPromptDialog.builder()
                    .setContext(getViewContext())
                    .setAffirmText(getString(R.string.i_know_it))
                    .setContentText("您的卡里有余额\n请先将卡插入到燃气表")
                    .build()
                    .show()
        }else{
            toActivity(NfcPaymentsActivity::class.java, Bundle().apply {
                putSerializable(Constants.IntentKey.READ_DEC, result)
            })
            finish()
        }
    }

    /**
     *   获取合约信息失败回调
     */
    override fun onInformationError(data: String, err: ApiThrowable) {
        ProgressUtil.dismissProgressDialog()
        val flag = TextUtils.equals("ER033", err.code) || TextUtils.equals("ER075", err.code) || TextUtils.equals("ER076", err.code) || TextUtils.equals("ER074", err.code) || TextUtils.equals("ER080", err.code)
        DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(this)
                .setAffirmText(getString(R.string.i_know_it))
                .setContentText(if (flag) "网络异常，请重试" else err.message)
                .build()
                .show()
    }

    /**
     *  nfc检测回调
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            ProgressUtil.showProgressDialog(this,"系统处理中，请勿关闭")
            this.intent=intent
            mPresenter?.sendOrder(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        //开启前台调度系统
        NfcUtil.getNfcAdapter(this)?.enableForegroundDispatch(this, NfcUtil.mPendingIntent, NfcUtil.mIntentFilter, NfcUtil.mTechList)

    }

    override fun onPause() {
        super.onPause()
        //关闭前台调度系统
        NfcUtil.getNfcAdapter(this)?.disableForegroundDispatch(this)
    }
}
