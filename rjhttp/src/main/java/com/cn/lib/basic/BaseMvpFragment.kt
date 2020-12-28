package com.cn.lib.basic

import android.os.Bundle


/**
 * Fragment基类
 * Created by admin on 2017/4/17.
 */


abstract class BaseMvpFragment<P : IBasePresenter<V>,in V : IBaseView> : BaseFragment() {
    protected var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mPresenter == null) {
            this.mPresenter = createPresenter()
        } else {
            mPresenter?.attachView(this as V)
        }
    }

    protected abstract fun createPresenter(): P

    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null) {
            mPresenter?.detachView()
            mPresenter = null
        }
    }

}
