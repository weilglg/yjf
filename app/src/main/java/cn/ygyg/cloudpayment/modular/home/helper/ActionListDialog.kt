package cn.ygyg.cloudpayment.modular.home.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.widget.DividerDecoration

class ActionListDialog(context: Context) : Dialog(context) {
    private val recyclerView: RecyclerView

    private val myAdapter = MyAdapter()

    init {
        setContentView(R.layout.dialog_action_list)
        window?.let {
            it.decorView?.setPadding(0, 0, 0, 0)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            it.setGravity(Gravity.BOTTOM)
            it.decorView?.setBackgroundColor(Color.TRANSPARENT)
        }
        recyclerView = findViewById(R.id.recycler)

        recyclerView.layoutManager = LinearLayoutManager(getContext())

        val dividerColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getContext().resources.getColor(R.color.line_color, getContext().theme)
        } else {
            getContext().resources.getColor(R.color.line_color)
        }

        val decoration = DividerDecoration.Builder()
                .setDividerColor(dividerColor)
                .build()
        recyclerView.addItemDecoration(decoration)
        recyclerView.adapter = myAdapter
        findViewById<View>(R.id.outSide).setOnClickListener { dismiss() }

    }

    fun setData(vararg actions: String) {
        myAdapter.list.clear()
        myAdapter.list.addAll(actions)
        myAdapter.notifyDataSetChanged()
    }

    fun setOnActionClickListener(listener: OnActionClickListener) {
        myAdapter.onActionClickListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val action: TextView = itemView.findViewById(R.id.item_action)
    }

    class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        val list = ArrayList<String>()
        var onActionClickListener: OnActionClickListener? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_action_dialog, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.action.text = list[position]
            holder.action.setOnClickListener {
                onActionClickListener?.onActionClicked(position, list[position])
            }
        }

    }

    interface OnActionClickListener {
        fun onActionClicked(position: Int, action: String)
    }
}
