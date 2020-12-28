package cn.ygyg.cloudpayment.modular.my.presenter

import cn.ygyg.cloudpayment.modular.my.contract.MyContract
import cn.ygyg.cloudpayment.utils.StringUtil
import cn.ygyg.cloudpayment.utils.UserUtil
import com.cn.lib.basic.BasePresenterImpl

/**
 * Created by Admin on 2019/4/17.
 */
class MyPresenter(view: MyContract.View) : BasePresenterImpl<MyContract.View>(view), MyContract.Presenter {
    override fun loaderPageData() {
        val entity = UserUtil.getUser()
        entity?.run {
            mvpView?.loaderPageDataSuccess(this)
        }
    }

    override fun logout() {
        mvpView?.logoutSuccess()
    }
}