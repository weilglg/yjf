package cn.ygyg.cloudpayment.modular.my.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelStore
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.LoaderManager
import android.support.v4.app.SharedElementCallback
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.app.Constants.IntentKey.IS_LOGIN
import cn.ygyg.cloudpayment.app.Constants.IntentKey.USER_INFO
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.login.activity.LoginActivity
import cn.ygyg.cloudpayment.modular.login.entity.UserEntity
import cn.ygyg.cloudpayment.modular.my.activity.AboutActivity
import cn.ygyg.cloudpayment.modular.my.contract.MyContract
import cn.ygyg.cloudpayment.modular.my.presenter.MyPresenter
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcPaymentsHistoryActivity
import cn.ygyg.cloudpayment.modular.payments.activity.PaymentsHistoryActivity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.SharePreUtil
import cn.ygyg.cloudpayment.utils.StringUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BaseMvpFragment
import com.cn.lib.util.ActivityListUtil
import kotlinx.android.synthetic.main.fragment_my.*
import java.io.FileDescriptor
import java.io.PrintWriter


class MyFragment : BaseMvpFragment<MyContract.Presenter, MyContract.View>(), MyContract.View {

    override fun createPresenter(): MyContract.Presenter = MyPresenter(this)

    override val contentViewResId: Int = R.layout.fragment_my


    override fun initViews(v: View) {
        val companyInfo = ConfigUtil.getCompanyInfo()
        val telephone = if("" == companyInfo?.hotline) null else companyInfo?.hotline
        telephone?.let {
            layout_customer_service.visibility = View.VISIBLE
        } ?: let{
            layout_customer_service.visibility = View.GONE
        }
        val exitNfc = ConfigUtil.exitNfc()
        if(exitNfc!=null&&exitNfc){
            layout_nfc_recharge_history.visibility=View.VISIBLE
        }else{
            layout_nfc_recharge_history.visibility=View.GONE
        }
    }

    override fun initListener(v: View) {

        btn_nb_recharge_history.setOnClickListener {
            toActivity(PaymentsHistoryActivity::class.java)
        }

        btn_nfc_recharge_history.setOnClickListener {
            toActivity(NfcPaymentsHistoryActivity::class.java)
        }
        btn_customer_service.setOnClickListener {
            val companyInfo = ConfigUtil.getCompanyInfo()
            val telephone = if(companyInfo?.hotline=="")null else companyInfo?.hotline
            telephone?.let {
                DefaultPromptDialog.builder()
                        .setContext(getViewContext())
                        .setAffirmText("呼叫")
                        .setCancelText("取消")
                        .setContentText(it)
                        .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                            override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it"))
                                startActivity(dialIntent)
                                return super.clickPositiveButton(dialog)
                            }
                        })
                        .build()
                        .show()
            }
        }
        btn_logout.setOnClickListener {
            DefaultPromptDialog.builder()
                    .setContext(getViewContext())
                    .setAffirmText("确认")
                    .setCancelText("取消")
                    .setContentText("确认退出吗？")
                    .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_VERTICAL)
                    .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                        override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                            mPresenter?.logout()
                            return super.clickPositiveButton(dialog)
                        }
                    })
                    .build()
                    .show()
        }
        btn_about.setOnClickListener {
            toActivity(AboutActivity::class.java)
        }
    }

    override fun loaderData() {
        mPresenter?.loaderPageData()
    }

    override fun logoutSuccess() {
        SharePreUtil.clear(IS_LOGIN)
        SharePreUtil.clear(Constants.IntentKey.TOKEN_KEY)
        SharePreUtil.clear(USER_INFO)
        UserUtil.clear()
        toActivity(LoginActivity::class.java)
        ActivityListUtil.INSTANCE.finishAllActivity(true)
    }

    @SuppressLint("SetTextI18n")
    override fun loaderPageDataSuccess(entity: UserEntity?) {
        entity?.cellPhone?.let {
            val phone = StringUtil.blurPhone(it)
            tv_phone.text = "Hello,$phone"
        }
        setHasLoadedOnce(true)
    }


    override fun getUserVisibleHint(): Boolean {
        val exitNfc = ConfigUtil.exitNfc()
        if(exitNfc!=null&&exitNfc){
            layout_nfc_recharge_history.visibility=View.VISIBLE
        }else{
            layout_nfc_recharge_history.visibility=View.GONE
        }
        return super.getUserVisibleHint()
    }
}
