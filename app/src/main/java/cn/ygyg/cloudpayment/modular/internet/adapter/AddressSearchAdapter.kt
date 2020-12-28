package cn.ygyg.cloudpayment.modular.internet.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.internet.vm.CityVM
import cn.ygyg.cloudpayment.utils.BaseViewHolder

class AddressSearchAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private val list: ArrayList<CityVM> by lazy { ArrayList<CityVM>() }

    var onItemClickListener: OnItemClickListener? = null

    fun setData(list: List<CityVM>?) {
        this.list.clear()
        list?.let {
            this.list.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CityVM {
        return list[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutId = R.layout.item_address_selector_content
        val itemView = LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val address = holder.findViewById<TextView>(R.id.address_name)
        address.text = list[position].cityShowName()
        address.setOnClickListener { onItemClickListener?.onItemClicked(holder, position) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClicked(holder: BaseViewHolder, position: Int)
    }
}
