package cn.ygyg.cloudpayment.modular.internet.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.internet.vm.CompanyVM
import cn.ygyg.cloudpayment.utils.BaseViewHolder
import com.cn.lib.retrofit.network.util.LogUtil

class CompanySelectorAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private val list: ArrayList<CompanyVM> by lazy { ArrayList<CompanyVM>() }

    var onItemClickListener: OnItemClickListener? = null
    var selectPosition = -1
        set(value) {
            if (field != value) {
                val old = field
                field = value
                notifyItemChanged(old)
                notifyItemChanged(field)
            }
        }

    fun setData(list: List<CompanyVM>?) {
        this.list.clear()
        list?.let {
            this.list.addAll(it)
        }
        LogUtil.i("setData", this.list.size.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutId = R.layout.item_company_select
        val itemView = LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val company = holder.findViewById<TextView>(R.id.company_name)
        val itemSelect = holder.findViewById<ImageView>(R.id.item_select)
        holder.findViewById<View>(R.id.item_layout).setOnClickListener { onItemClickListener?.onItemClicked(holder, position) }
        company.text = list[position].companyName()
        itemSelect.visibility = if (selectPosition == position) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): CompanyVM? {
        if (position >= 0 && position < list.size) {
            return list[position]
        }
        return null
    }

    interface OnItemClickListener {
        fun onItemClicked(holder: BaseViewHolder, position: Int)
    }
}
