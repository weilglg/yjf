package cn.ygyg.cloudpayment.modular.internet.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.internet.entity.SelectModel
import com.cn.lib.basic.BaseActivity

class ListSelectActivity : BaseActivity() {
    override fun getContentViewResId(): Int {
        return R.layout.activity_list_select

    }

    companion object {
        val LIST_DATA = "list_data"
        val SELECT_POSITION = "select_position"
        val IS_MULTIPLE = "is_multiple"

        /** 单选调用
         * @param activity 调用者
         * @param list 数据源
         * @param selectPosition 默认选中下标
         * @param requestCode 请求码
         */
        fun startForResult(activity: Activity, list: ArrayList<out SelectModel>, selectPosition: Int, requestCode: Int) {
            startForResult(activity, list, false, selectPosition, requestCode)
        }

        /** 单选调用
         * @param activity 调用者
         * @param list 数据源
         * @param isMultiple 是否多选
         * @param requestCode 请求码
         */
        fun startForResult(activity: Activity, list: ArrayList<out SelectModel>, isMultiple: Boolean, requestCode: Int) {
            startForResult(activity, list, isMultiple, -1, requestCode)
        }

        /** 多选调用
         * @param activity 调用者
         * @param list 数据源
         * @param requestCode 请求码
         */
        fun startForResult(activity: Activity, list: ArrayList<out SelectModel>, requestCode: Int) {
            startForResult(activity, list, true, -1, requestCode)
        }

        /**
         * @param activity 调用者
         * @param list 数据源
         * @param isMultiple 是否多选
         * @param selectPosition 默认选中下标
         * @param requestCode 请求码
         */
        fun startForResult(activity: Activity, list: ArrayList<out SelectModel>, isMultiple: Boolean, selectPosition: Int, requestCode: Int) {
            val intent = Intent(activity, ListSelectActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(LIST_DATA, list)
            bundle.putBoolean(IS_MULTIPLE, isMultiple)
            bundle.putSerializable(SELECT_POSITION, selectPosition)
            intent.putExtra(ACTIVITY_BUNDLE, bundle)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var listData: ArrayList<out SelectModel>? = null
    private var isMultiple = false
    private var selectPosition = -1

    @Suppress("UNCHECKED_CAST")
    override fun initViews() {
        bundle?.let {
            listData = it.getSerializable(LIST_DATA) as ArrayList<out SelectModel>
            isMultiple = it.getBoolean(IS_MULTIPLE, false)
            selectPosition = it.getInt(SELECT_POSITION, -1)
        }
        if (listData == null || listData?.size == 0) {
            finish()
        }
    }
}
