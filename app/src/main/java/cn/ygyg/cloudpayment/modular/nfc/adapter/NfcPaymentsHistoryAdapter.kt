package cn.ygyg.cloudpayment.modular.nfc.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.nfc.entity.NfcHistoryEntity
import cn.ygyg.cloudpayment.modular.payments.vm.HistoryVM
import cn.ygyg.cloudpayment.utils.BaseViewHolder

class NfcPaymentsHistoryAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val list = ArrayList<NfcHistoryEntity.ListBean>()


    fun setData(list: ArrayList<out NfcHistoryEntity.ListBean>?) {
        this.list.clear()
        list?.let {
            this.list.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<out NfcHistoryEntity.ListBean>?) {
        list?.let {
            this.list.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_payments_history, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = list[position]
        holder.findViewById<TextView>(R.id.user_name).text = item.customerName
        holder.findViewById<TextView>(R.id.pay_amount).text = item.buyGasMoney+"元"
//        val payState = holder.findViewById<TextView>(R.id.pay_state)
//        payState.text = item.payState()
//        val colorId = if (TextUtils.equals("退款成功", item.payState())) R.color.text_color_red else R.color.text_green_color
//        payState.setTextColor(payState.context.resources.getColor(colorId))
        holder.findViewById<TextView>(R.id.account_code).text = item.realSteelGrade
        holder.findViewById<TextView>(R.id.pay_mode).text = item.paymentMethodDesc+"支付"
        holder.findViewById<TextView>(R.id.pay_time).text = item.updateDate
    }
}