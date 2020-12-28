package com.cn.lib.basic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cn.lib.weight.indicator.IndicatorTabPageAdapter;
import com.cn.lib.widget.indicator.TabIndicatorView;
import com.cn.lib.weight.indicator.TabInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2015/11/9.
 * 有选项卡的Fragment的基类
 */
public abstract class BaseIndicatorFragment extends BaseFragment implements ViewPager
        .OnPageChangeListener,
        TabIndicatorView.OnTabChangeListener, IndicatorTabPageAdapter.FragmentRelevantCallback {

    public static final String EXTRA_TAB = "tab";

    /**
     * 选中的选项卡的下标
     */
    public int mCurrentTab = 0;

    protected boolean isInitTabs;

    // 存放选项卡信息的列表
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    // ViewPager adapter
    protected IndicatorTabPageAdapter myAdapter = null;

    // ViewPager
    protected ViewPager mPager;
    private TabIndicatorView mIndicator;

    @Override
    public void onDestroy() {
        if (mTabs != null) {
            mTabs.clear();
        }
        if (myAdapter != null) {
            myAdapter = null;
        }
        if (mPager != null) {
            mPager = null;
        }
        mIndicator = null;
        super.onDestroy();
    }

    @Override
    protected void initViews(View v) {
        // 这里初始化界面
        if (!isInitTabs) {
            initTabsInfo(mTabs);
            isInitTabs = true;
            Bundle bundle = getArguments();
            if (bundle != null) {
                int tabIndex = bundle.getInt(EXTRA_TAB, mCurrentTab);
                if (tabIndex >= 0 && tabIndex < mTabs.size()) {
                    mCurrentTab = tabIndex;
                }
            }
            mIndicator = (TabIndicatorView) v.findViewById(getTabIndicatorViewId());


            int layoutResId = getViewPagerId();
            if (layoutResId != 0) {
                myAdapter = new IndicatorTabPageAdapter(getChildFragmentManager(), mTabs);
                myAdapter.setFragmentRelevantCallback(this);
                mPager = (ViewPager) v.findViewById(getViewPagerId());
                mPager.setAdapter(myAdapter);
                mPager.addOnPageChangeListener(this);
                mPager.setOffscreenPageLimit(1);
                // 设置ViewPager内部页面之间的间距
                mPager.setPageMargin(getResources().getDimensionPixelSize(0));
            } else {
                mIndicator.setOnTabChangeListener(this);
            }
            mIndicator.init(mCurrentTab, mTabs, mPager);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin())
                * position + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.onSwitched(position);
        mCurrentTab = position;
        onViewPagerSwitch(mCurrentTab, mTabs.get(mCurrentTab).getFragment());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    protected TabInfo getFragmentById(int tabId) {
        if (mTabs == null)
            return null;
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            TabInfo tab = mTabs.get(index);
            if (tab.getId() == tabId) {
                return tab;
            }
        }
        return null;
    }

    /**
     * 跳转到任意选项卡
     *
     * @param tabId 选项卡下标
     */
    public void navigate(int tabId) {
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            if (mTabs.get(index).getId() == tabId) {
                if (mPager != null) {
                    mPager.setCurrentItem(index, false);
                } else if (mIndicator != null
                        && mIndicator.getOnTabChangeListener() != null) {
                    mIndicator.getOnTabChangeListener().onTabChange(index);
                }
            }
        }
    }

    /**
     * 在这里提供要显示的选项卡数据
     */
    protected abstract void initTabsInfo(List<TabInfo> tabs);

    /**
     * 切换选项卡监听器(没有ViewPager时不用重写这个方法)
     */
    protected void onViewPagerSwitch(int index, Fragment fragment) {

    }

    /**
     * 返回ViewPager的控件ID，如果没有ViewPager不用重写
     */
    protected int getViewPagerId() {
        return 0;
    }

    /**
     * 返回ViewPager指示器的控件资源ID
     */
    protected abstract int getTabIndicatorViewId();

    @Override
    public void onTabChange(int index) {

    }

    /**
     * 在这里提供初始化后的Fragment的特性初始化操作(没有ViewPager时不用重写这个方法)
     */
    @Override
    public void initFragmentEnd(int fragmentIndex, Fragment fragment) {

    }
}
