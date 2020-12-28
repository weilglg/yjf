/*
 * @author http://blog.csdn.net/singwhatiwanna
 */
package com.cn.lib.basic

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager.OnPageChangeListener
import com.cn.lib.widget.CustomViewPager
import com.cn.lib.weight.indicator.IndicatorTabPageAdapter
import com.cn.lib.weight.indicator.TabInfo
import com.cn.lib.widget.indicator.TabIndicatorView
import java.util.*


abstract class BaseIndicatorActivity : BaseActivity(), OnPageChangeListener, TabIndicatorView.OnTabChangeListener, IndicatorTabPageAdapter.FragmentRelevantCallback {

    /**
     * 选中的选项卡的下标
     */
    var mCurrentTab = 0

    // 存放选项卡信息的列表
    protected var mTabs: ArrayList<TabInfo> = ArrayList()

    // ViewPager adapter
    protected var myAdapter: IndicatorTabPageAdapter? = null

    // ViewPager
    protected var mPager: CustomViewPager? = null

    // 选项卡控件
    protected var mIndicator: TabIndicatorView? = null

    /**
     * 返回ViewPager的控件ID，如果没有ViewPager不用重写
     */
    protected open fun getViewPagerId(): Int = 0

    /**
     * 返回选项卡控件的布局ID
     */
    protected abstract fun getTagIndicatorViewId(): Int


    override fun onDestroy() {
        mTabs.clear()
        if (mPager != null) {
            myAdapter = null
            mPager = null
        }
        mIndicator = null
        super.onDestroy()
    }

    override fun initViews() {
        super.initViews()
        // 这里初始化界面
        initTabsInfo(mTabs)
        val intent = intent
        if (intent != null) {
            val bundle = getIntent().getBundleExtra(BaseActivity.ACTIVITY_BUNDLE)
            if (bundle != null) {
                val tabIndex = bundle.getInt(EXTRA_TAB, mCurrentTab)
                if (tabIndex >= 0 && tabIndex < mTabs.size) {
                    mCurrentTab = tabIndex
                }
            }
        }
        // 根据ID获得选项卡对象
        mIndicator = findViewById(getTagIndicatorViewId())
        val layoutResId = getViewPagerId()
        if (layoutResId != 0) {
            myAdapter = IndicatorTabPageAdapter(supportFragmentManager, mTabs)
            myAdapter?.setFragmentRelevantCallback(this)
            mPager = findViewById(getViewPagerId())
            mPager?.let {
                it.adapter = myAdapter
                it.addOnPageChangeListener(this)
                it.offscreenPageLimit = 0
                // 设置ViewPager内部页面之间的间距
                it.pageMargin = 0
                // 设置ViewPager内部页面间距的Drawable
                it.setPageMarginDrawable(android.R.color.transparent)
            }
        } else {
            mIndicator?.onTabChangeListener = this
        }

        // 初始化选项卡
        mIndicator?.init(mCurrentTab, mTabs, mPager)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float,
                                positionOffsetPixels: Int) {
        mPager?.run {
            mIndicator?.onScrolled((width + pageMargin) * position + positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        mIndicator?.onSwitched(position)
        mCurrentTab = position
        mTabs[mCurrentTab].fragment?.let {
            onViewPagerSwitch(mCurrentTab, it)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    protected fun getTabInfoById(tabId: Int): TabInfo? {
        var index = 0
        val count = mTabs.size
        while (index < count) {
            val tab = mTabs[index]
            if (tab.id == tabId) {
                return tab
            }
            index++
        }
        return null
    }

    /**
     * 跳转到任意选项卡
     *
     * @param position 选项卡下标
     */
    fun navigateByPosition(position: Int) {
        if (position < mTabs.size) {
            mPager?.setCurrentItem(position, false)
            mIndicator?.let {
                it.onTabChangeListener?.onTabChange(position)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    /**
     * 在这里提供要显示的选项卡数据
     */
    protected abstract fun initTabsInfo(tabs: ArrayList<TabInfo>)

    /**
     * 在这里提供初始化后的Fragment的特性初始化操作(没有ViewPager时不用重写这个方法)
     */
    override fun initFragmentEnd(fragmentIndex: Int, fragment: Fragment) {

    }

    /**
     * 切换选项卡监听器(没有ViewPager时不用重写这个方法)
     */
    fun onViewPagerSwitch(index: Int, fragment: Fragment) {

    }

    /**
     * 选项卡被选中时的回调函数
     *
     * @param index 被选中的选项卡的下标
     */
    override fun onTabChange(index: Int) {

    }

    companion object {

        val EXTRA_TAB = "tab"
    }

}
