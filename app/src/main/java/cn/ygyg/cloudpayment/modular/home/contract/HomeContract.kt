package cn.ygyg.cloudpayment.modular.home.contract

import android.content.Context
import cn.ygyg.cloudpayment.modular.home.entity.DeviceListResponseEntity
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

/**
 * Created by Admin on 2019/4/17.
 */
class HomeContract {
    interface View : IBaseView {

        fun loaderSuccess(result: DeviceListResponseEntity?)

        fun unbindSuccess(position: Int, device: DeviceVM) // 解除绑定成功

        fun loaderCompleted()
    }

    interface Presenter : IBasePresenter<View> {
        fun loaderData(pageNum: Int, pageSize: Int)

        fun unBindDevice(position: Int, device: DeviceVM) //解除绑定设备
    }
}