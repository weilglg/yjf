package com.cn.lib.basic


/**
 * Created by admin on 2017/4/17.
 */

interface IBasePresenter<in V : IBaseView> {

    /**
     * 绑定View层
     *
     * @param view
     */
    fun attachView(view: V)

    /**
     * 解除绑定
     */
    fun detachView()

}
