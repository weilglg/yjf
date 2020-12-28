package cn.ygyg.cloudpayment.modular.internet.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.internet.adapter.CompanySelectorAdapter
import cn.ygyg.cloudpayment.modular.internet.vm.CompanyVM
import cn.ygyg.cloudpayment.utils.BaseViewHolder
import com.cn.lib.util.ToastUtil

class CompanySelectDialog(context: Context) : Dialog(context) {

    fun setData(response: ArrayList<out CompanyVM>) {
        adapter.setData(response)
    }

    private val adapter: CompanySelectorAdapter by lazy { CompanySelectorAdapter() }
    private val recycler: RecyclerView

    var onCompanyConfirmListener: OnCompanyConfirmListener? = null

    init {
        setContentView(R.layout.dialog_company_select)
        window?.let {
            it.decorView?.setPadding(0, 0, 0, 0)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.BOTTOM)
            it.decorView?.setBackgroundColor(Color.TRANSPARENT)
        }
        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        adapter.setData(ArrayList<CompanyVM>().apply {
            for (i in 1..10) {
                add(object : CompanyVM {
                    override fun companyName(): String = "$i"

                    override fun companyId(): Long = 0L

                })
            }
        })
        recycler.adapter = adapter

        findViewById<View>(R.id.close).setOnClickListener { dismiss() }
        findViewById<View>(R.id.confirm).setOnClickListener {
            val company = adapter.getItem(adapter.selectPosition)
            if (company == null) {
                ToastUtil.showToast(context, "请选择")
            } else {
                onCompanyConfirmListener?.onCompanyConfirm(company)
            }
        }
        adapter.onItemClickListener = object : CompanySelectorAdapter.OnItemClickListener {
            override fun onItemClicked(holder: BaseViewHolder, position: Int) {
                adapter.selectPosition = position
            }
        }
    }

    interface OnCompanyConfirmListener {
        fun onCompanyConfirm(company: CompanyVM)
    }
}
