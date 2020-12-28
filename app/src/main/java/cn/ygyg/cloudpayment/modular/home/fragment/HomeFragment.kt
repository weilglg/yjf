package cn.ygyg.cloudpayment.modular.home.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.View
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.R.layout
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog
import cn.ygyg.cloudpayment.modular.home.adapter.AccountInfoListAdapter
import cn.ygyg.cloudpayment.modular.home.contract.HomeContract
import cn.ygyg.cloudpayment.modular.home.entity.DeviceListResponseEntity
import cn.ygyg.cloudpayment.modular.home.presenter.HomePresenter
import cn.ygyg.cloudpayment.modular.internet.activity.NewAccountActivity
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcHintActivity
import cn.ygyg.cloudpayment.modular.nfc.activity.NfcPaymentsActivity
import cn.ygyg.cloudpayment.modular.payments.activity.PaymentsActivity
import cn.ygyg.cloudpayment.utils.ConfigUtil
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import cn.ygyg.cloudpayment.utils.NfcUtil
import com.cn.lib.basic.BaseMvpFragment
import com.cn.lib.util.ToastUtil
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_activity_header.*


class HomeFragment : BaseMvpFragment<HomeContract.Presenter, HomeContract.View>(), HomeContract.View {

    override fun createPresenter(): HomeContract.Presenter = HomePresenter(this)
    override val contentViewResId: Int = layout.fragment_home

    private val pageSize = 10
    private var pageNum = 1
    private var isLastPage = false

    private val mAdapter: AccountInfoListAdapter by lazy {
        AccountInfoListAdapter(getViewContext())
    }

    private val firstView: View by lazy {
        val view = layoutInflater.inflate(layout.layout_first_into, recycler_view, false)
        view.findViewById<View>(cn.ygyg.cloudpayment.R.id.btn_recharge).setOnClickListener {
            toActivity(NewAccountActivity::class.java)
        }
        view.findViewById<View>(cn.ygyg.cloudpayment.R.id.layout_add_account).setOnClickListener {
            toActivity(NewAccountActivity::class.java)
        }
        view
    }

    override fun initViews(v: View) {
        activity?.let {
            HeaderBuilder(it).setTitle(R.string.app_name)
            div_bottom.setBackgroundResource(R.color.transparent)
        }
        recycler_view.let {
            it.clearAnimation()
            (it.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            it.adapter = mAdapter
            it.layoutManager = LinearLayoutManager(getViewContext())
        }
        mAdapter.addHeaderView(layoutInflater.inflate(layout.layout_banner, recycler_view, false))
    }

    override fun initListener(v: View) {
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                Handler().postDelayed({
                    refreshLayout.finishRefreshing()
                }, 2000)
                pageNum=1
                mPresenter?.loaderData(pageNum,pageSize)
            }
            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout) {
                Handler().postDelayed({
                    refreshLayout.finishLoadmore()
                }, 2000)
                if (!isLastPage) {
                    pageNum++
                    mPresenter?.loaderData(pageNum, pageSize)
                } else {
                    refreshLayout.finishLoadmore()
                    showToast("没有更多了")
                }
            }
        })

        mAdapter.onCustomClickListener = object : AccountInfoListAdapter.OnCustomClickListener {
            override fun onRechargeClicked(position: Int, item: DeviceVM) {
                if(""==item.meterClassification()){
                    showDialog("该缴费户号异常")
                }else if(item.geticCardType()=="02"){
                    if(NfcUtil.checkNFC(getViewContext())){
                        toActivity(NfcHintActivity::class.java,Bundle().apply {
                            putString(Constants.IntentKey.DEVICE_CODE, item.deviceCode())
                        })
                    }
                }else{
                    toActivity(PaymentsActivity::class.java, Bundle().apply {
                        putString(Constants.IntentKey.DEVICE_CODE, item.deviceCode())
                        putString(Constants.IntentKey.COMPANY_KEY, item.companyCode())
                        putString(Constants.IntentKey.METER_CLASS, item.meterClassification())

                    })
                }
            }

            override fun onItemLongClicked(position: Int, item: DeviceVM) {
                DefaultPromptDialog.builder()
                        .setContext(getViewContext())
                        .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                        .setContentText("确定删除该账户信息吗？")
                        .setAffirmText("确认")
                        .setCancelText("取消")
                        .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                            override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                                mPresenter?.unBindDevice(position, item)
                                return super.clickPositiveButton(dialog)
                            }
                        })
                        .build()
                        .show()
            }
        }
    }

    override fun loaderData() {
        refreshLayout.startRefresh()
    }


    override fun loaderSuccess(response: DeviceListResponseEntity?) {
        if (mAdapter.getFooterLayoutCount() == 0) {
            //为空时执行
            mAdapter.addFooterView(firstView)
        }
        //当列表为空时显示提示布局，否则隐藏提示布局
        if (response != null && response.size != 0) {
            firstView.findViewById<View>(cn.ygyg.cloudpayment.R.id.pay_item).visibility = View.GONE
            firstView.findViewById<View>(cn.ygyg.cloudpayment.R.id.developing).visibility = View.GONE
        } else {
            firstView.findViewById<View>(cn.ygyg.cloudpayment.R.id.pay_item).visibility = View.VISIBLE
            firstView.findViewById<View>(cn.ygyg.cloudpayment.R.id.developing).visibility = View.VISIBLE
        }
        response!!.let {
            //不为空时执行

            if (it.isFirstPage) {
                mAdapter.setNewList(it.list!!.toMutableList())
            } else {
                mAdapter.addAll(it.list!!.toMutableList())
            }
            isLastPage = it.isLastPage
            }
        }

    override fun loaderCompleted() {
        setHasLoadedOnce(true)
        refreshLayout.finishRefreshing()
    }

    override fun unbindSuccess(position: Int, device: DeviceVM) {
        context?.let { ToastUtil.showToast(it, "删除成功") }
        mAdapter.removeItem(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this)
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag("refreshDevice")])
    fun refreshList(isRefresh: String) {
        refreshLayout.startRefresh()
    }

    fun showDialog(msg: String) {
        val builder = context?.let {
            DefaultPromptDialog.builder()
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContext(it)
                .setTitleText("提示")
                .setContentText(msg)
        }
        val companyInfo = ConfigUtil.getCompanyInfo()
        val telephone = companyInfo?.hotline
        if (telephone != null && telephone != "") {
            builder!!.setAffirmText("联系客服")
                    .setCancelText("取消")
                    .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                        override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                            showCompanyTelephone(telephone)
                            return super.clickPositiveButton(dialog)
                        }
                    })
        } else {
            builder!!.setAffirmText("确认")
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
