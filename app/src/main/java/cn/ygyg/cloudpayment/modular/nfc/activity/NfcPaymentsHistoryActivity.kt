package cn.ygyg.cloudpayment.modular.nfc.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.ygyg.cloudpayment.R.*
import cn.ygyg.cloudpayment.modular.nfc.adapter.NfcPaymentsHistoryAdapter
import cn.ygyg.cloudpayment.modular.nfc.contract.NfcPaymentsHistoryActivityContract
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.modular.nfc.presenter.NfcPaymentsHistoryPresenter
import cn.ygyg.cloudpayment.modular.payments.adapter.NfcPaymentsCostAdapter
import cn.ygyg.cloudpayment.modular.payments.adapter.PaymentsHistoryAdapter
import cn.ygyg.cloudpayment.modular.payments.entity.HistoryPageResponseEntity
import cn.ygyg.cloudpayment.utils.HeaderBuilder
import cn.ygyg.cloudpayment.widget.LoadMoreView
import cn.ygyg.cloudpayment.widget.ProgressHeaderView
import com.cn.lib.basic.BaseMvpActivity
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import kotlinx.android.synthetic.main.activity_nfc_payments_history.*

class NfcPaymentsHistoryActivity : BaseMvpActivity<NfcPaymentsHistoryActivityContract.Presenter, NfcPaymentsHistoryActivityContract.View>(),
    NfcPaymentsHistoryActivityContract.View {

    override fun createPresenter(): NfcPaymentsHistoryActivityContract.Presenter = NfcPaymentsHistoryPresenter(this)

    private val pageSize = 10
    private var pageNum = 1
    private var isLastPage = false

    private val adapterNfc: NfcPaymentsHistoryAdapter by lazy { NfcPaymentsHistoryAdapter() }

    override fun getContentViewResId(): Int = layout.activity_nfc_payments_history
    override fun initViews() {
        HeaderBuilder(this).apply {
            setTitle(string.nfc_recharge_history)
            setLeftImageRes(mipmap.back)
        }
        refresh_layout.setHeaderView(ProgressHeaderView(getViewContext()).setTextVisibility(false))
        refresh_layout.setBottomView(LoadMoreView(getViewContext()))
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapterNfc
    }

    override fun initListener() {
        refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                pageNum=1
                mPresenter?.loadPage(pageNum, pageSize)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                if (!isLastPage) {
                    pageNum++
                    mPresenter?.loadPage(pageNum, pageSize)
                } else {
                    refresh_layout.finishLoadmore()
                    showToast("没有更多了")
                }
            }
        })
    }

    override fun initData() {
        mPresenter?.loadPage(1, pageSize)
    }

    override fun onLoadCompleted() {
        refresh_layout.finishLoadmore()
        refresh_layout.finishRefreshing()
        if (adapterNfc.itemCount != 0) {
            empty_view.visibility = View.GONE
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    override fun onLoadSuccess(result: NfcHistoryEntity?) {
        result!!.let {
            if (it.isFirstPage) {
                adapterNfc.setData(it.list)
            } else {
                adapterNfc.addData(it.list)
            }
            isLastPage = it.isLastPage
        }
    }
}