package com.cn.lib.weight.indicator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import com.cn.lib.util.ListUtil
import java.util.*


class IndicatorTabPageAdapter(fm: FragmentManager, tabs: ArrayList<TabInfo>) : FragmentPagerAdapter(fm) {
    private val tabMap = SparseArray<TabInfo>()
    private var mFragmentRelevantCallback: FragmentRelevantCallback? = null

    fun setFragmentRelevantCallback(mFragmentRelevantCallback: FragmentRelevantCallback) {
        this.mFragmentRelevantCallback = mFragmentRelevantCallback
    }

    init {
        tabMap.clear()
        if (!ListUtil.isEmpty(tabs)) {
            for (i in tabs.indices) {
                tabMap.put(i, tabs[i])
            }
        }
    }

    fun setNewTabs(tabs: ArrayList<out TabInfo>) {
        tabMap.clear()
        if (!ListUtil.isEmpty(tabs)) {
            for (i in tabs.indices) {
                tabMap.put(i, tabs[i])
            }
        }
        notifyDataSetChanged()
    }

    override fun getItem(pos: Int): Fragment? {
        var fragment: Fragment? = null
        if (tabMap.size() > 0) {
            val tab = tabMap.get(pos) ?: return null
            fragment = tab.createFragment()
            fragment?.let {
                mFragmentRelevantCallback?.initFragmentEnd(pos, it)
            }
        }
        return fragment
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return tabMap.size()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val tab = tabMap.get(position)
        val fragment = super.instantiateItem(container, position) as Fragment
        tab.fragment = fragment
        return fragment
    }

    interface FragmentRelevantCallback {
        fun initFragmentEnd(fragmentIndex: Int, fragment: Fragment)
    }
}
