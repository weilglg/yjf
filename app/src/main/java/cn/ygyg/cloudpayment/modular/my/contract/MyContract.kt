package cn.ygyg.cloudpayment.modular.my.contract

import cn.ygyg.cloudpayment.modular.login.entity.UserEntity
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

/**
 * Created by Admin on 2019/4/17.
 */
class MyContract {
    interface View : IBaseView {
        fun logoutSuccess()
        fun loaderPageDataSuccess(entity: UserEntity?)

    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 退出登录
         */
        fun logout()

        fun loaderPageData()

    }
}