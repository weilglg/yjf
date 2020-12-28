package cn.ygyg.cloudpayment.modular.internet.helper

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.internet.adapter.AddressSearchAdapter
import cn.ygyg.cloudpayment.modular.internet.vm.CityVM
import cn.ygyg.cloudpayment.utils.BaseViewHolder
import cn.ygyg.cloudpayment.utils.TextSearchUtils
import cn.ygyg.cloudpayment.utils.ViewUtils
import cn.ygyg.cloudpayment.widget.CleanUpEditText
import cn.ygyg.cloudpayment.widget.EmptyView
import com.cn.lib.retrofit.network.util.LogUtil

class SearchAddressDialog(context: Context) : Dialog(context) {
    private var searchBtn: TextView
    private var search: CleanUpEditText
    private var searchList: RecyclerView
    private var emptyView: EmptyView


    var onAddressClickListener: OnAddressClickListener? = null
    var getDataSource: DataSourceGetter? = null
    private var mOnShowListener: DialogInterface.OnShowListener? = null

    private val adapter = AddressSearchAdapter()

    init {
        setContentView(R.layout.dialog_search_address)
        window?.let {
            it.decorView?.setPadding(0, 0, 0, 0)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            it.setGravity(Gravity.BOTTOM)
            it.decorView?.setBackgroundColor(Color.TRANSPARENT)
        }
        searchBtn = findViewById(R.id.search_btn)
        search = findViewById(R.id.search)
        searchList = findViewById(R.id.search_list)
        emptyView = findViewById(R.id.empty_view)
        emptyView.setEmptyText("该城市暂未开通燃气缴费服务")
        emptyView.setEmptyImageResource(R.mipmap.image_empty_data)

        searchList.layoutManager = LinearLayoutManager(context)
        searchList.adapter = adapter

        adapter.onItemClickListener = object : AddressSearchAdapter.OnItemClickListener {
            override fun onItemClicked(holder: BaseViewHolder, position: Int) {
                dismiss()
                onAddressClickListener?.onAddressClicked(adapter.getItem(position))
            }
        }


        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchBtn.setText(if (s.isNullOrEmpty()) R.string.cancel else R.string.search)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        search.setOnEditorActionListener { v, actionId, event ->
            var click = true
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                event?.let {
                    click = event.action == KeyEvent.ACTION_UP
                }
                if (click) {
                    searchAddress(v.text.toString())
                }
            }
            click
        }
        searchBtn.setOnClickListener {
            if (search.text.isEmpty()) {
                dismiss()
            } else {
                searchAddress(search.text.toString())
            }
        }
        super.setOnShowListener { dialog ->
            ViewUtils.showKeyboard(search)
            mOnShowListener?.onShow(dialog)
        }
    }

    override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
        mOnShowListener = listener
    }

    /**
     * 搜索地址
     */
    private fun searchAddress(keyWorld: String) {
        val result = ArrayList<CityVM>()
        getDataSource?.dataSource()?.let {
            val regex = TextSearchUtils.toRegex(keyWorld.toUpperCase())
            for (ele in it) {
                LogUtil.i("searchAddress", ele.toString())
                if (TextSearchUtils.contain(ele.cityShowName(), regex) ||
                        TextSearchUtils.contain(ele.cityPinyin(), regex)) {
                    result.add(ele)
                    LogUtil.i("searchAddress", ele.toString())
                }
            }
        }
        emptyView.visibility = if (result.size == 0) View.VISIBLE else View.GONE
        searchList.visibility = if (result.size == 0) View.GONE else View.VISIBLE
        adapter.setData(result)
    }


    interface OnAddressClickListener {
        fun onAddressClicked(city: CityVM)
    }

    interface DataSourceGetter {
        fun dataSource(): ArrayList<out CityVM>?

    }
}
