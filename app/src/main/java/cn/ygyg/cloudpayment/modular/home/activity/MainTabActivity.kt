package cn.ygyg.cloudpayment.modular.home.activity

import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.modular.home.fragment.HomeFragment
import cn.ygyg.cloudpayment.modular.my.fragment.MyFragment
import com.cn.lib.util.ResourceUtil
import com.cn.lib.basic.BaseIndicatorActivity
import com.cn.lib.weight.indicator.TabInfo
import java.util.*

class MainTabActivity : BaseIndicatorActivity() {

   override fun  getViewPagerId(): Int = R.id.view_pager

    override fun initTabsInfo(tabs: ArrayList<TabInfo>) {
        tabs.add(TabInfo(1, ResourceUtil.getString(getViewContext(), R.string.tab_home), R.mipmap.tab_home_normal, R.mipmap.tab_home_press, HomeFragment::class.java))
        tabs.add(TabInfo(2, ResourceUtil.getString(getViewContext(), R.string.tab_my), R.mipmap.tab_my_normal, R.mipmap.tab_my_press, MyFragment::class.java))
    }

    override fun getTagIndicatorViewId(): Int = R.id.layout_tab

    override fun getContentViewResId(): Int = R.layout.activity_home

}