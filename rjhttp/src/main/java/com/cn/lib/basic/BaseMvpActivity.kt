package com.cn.lib.basic

import android.os.Bundle


/**
 * Created by admin on 2017/4/17.
 */

abstract class BaseMvpActivity<P : IBasePresenter<V>, in V : IBaseView> : BaseActivity() {

    protected  var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        if (mPresenter != null) {
            mPresenter?.detachView()
        }
        super.onDestroy()
    }

    /**
     * 创建Presenter
     */
    protected abstract fun createPresenter(): P

}
