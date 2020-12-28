package cn.ygyg.cloudpayment.modular.internet.contract

import android.util.ArrayMap
import cn.ygyg.cloudpayment.modular.internet.vm.CityVM
import cn.ygyg.cloudpayment.modular.internet.vm.CompanyVM
import com.cn.lib.basic.IBasePresenter
import com.cn.lib.basic.IBaseView

class AddressSelectorActivityContract {
    interface View : IBaseView {
        /**
         * 加载城市列表
         */
        fun onLoadCityListSuccess(response: ArrayList<out CityVM>)

        /**
         * 加载公司列表
         */
        fun onLoadCompanyListSuccess(response: ArrayList<out CompanyVM>)

        fun addTitleSuccess(response: ArrayList<CityVM>, titlePositionMap: ArrayMap<String, Int>)
    }

    interface Presenter : IBasePresenter<View> {
        /**
         * 加载城市列表
         */
        fun loadCityList()

        fun addTitleItem(response: ArrayList<out CityVM>)
        /**
         * 根据城市 获取该城市的机构
         */
        fun getCompanyByCity(city: CityVM)
    }
}
