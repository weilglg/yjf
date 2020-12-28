package cn.ygyg.cloudpayment.modular.internet.presenter

import android.util.ArrayMap
import cn.ygyg.cloudpayment.net.RequestManager
import cn.ygyg.cloudpayment.net.UrlConstants
import cn.ygyg.cloudpayment.modular.internet.contract.AddressSelectorActivityContract
import cn.ygyg.cloudpayment.modular.internet.entity.CityListResponseEntity
import cn.ygyg.cloudpayment.modular.internet.entity.CityTitle
import cn.ygyg.cloudpayment.modular.internet.entity.CompanyListResponseEntity
import cn.ygyg.cloudpayment.modular.internet.vm.CityVM
import cn.ygyg.cloudpayment.utils.ProgressUtil
import com.cn.lib.basic.BasePresenterImpl
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.github.promeg.pinyinhelper.Pinyin
import java.util.*
import kotlin.collections.ArrayList

class AddressSelectorActivityPresenter(view: AddressSelectorActivityContract.View) :
        BasePresenterImpl<AddressSelectorActivityContract.View>(view),
        AddressSelectorActivityContract.Presenter {
    override fun loadCityList() {
        RequestManager.get(UrlConstants.cityList)
                .param("pageIndex", "1")
                .param("pageSize", "9999")
                .execute("cityList", object : ResultCallback<CityListResponseEntity>() {
                    override fun onStart(tag: Any?) {

                    }

                    override fun onCompleted(tag: Any?) {

                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let { mvpView?.showToast(it) }
                    }

                    override fun onSuccess(tag: Any?, t: CityListResponseEntity?) {
                        t?.list?.let {
                            mvpView?.onLoadCityListSuccess(getSortPinyinCityList(it))
                        }
                    }
                })
    }

    override fun addTitleItem(response: ArrayList<out CityVM>) {
        val result = ArrayList<CityVM>()
        var char = '0'
        val titlePositionMap = ArrayMap<String, Int>()
        for (ele in response) {
            val pinyin = ele.cityPinyin()
            val c = pinyin[0]
            if (c != char) {
                titlePositionMap[c.toString()] = result.size
                result.add(CityTitle().apply { cityTitle = c.toString() })
            }
            result.add(ele)
            char = c
        }
        mvpView?.addTitleSuccess(result, titlePositionMap)
    }

    override fun getCompanyByCity(city: CityVM) {
        RequestManager.get(UrlConstants.companyList)
                .param("cityId", city.getCityId())
                .param("pageNum", "1")
                .param("pageSize", "9999")
                .execute("companyList", object : ResultCallback<CompanyListResponseEntity>() {
                    override fun onStart(tag: Any?) {
                        mvpView?.let {
                            ProgressUtil.showProgressDialog(it.getViewContext(), "加载中...")
                        }
                    }

                    override fun onCompleted(tag: Any?) {
                        mvpView?.let {
                            ProgressUtil.dismissProgressDialog()
                        }
                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        e.message?.let { mvpView?.showToast(it) }
                    }

                    override fun onSuccess(tag: Any?, t: CompanyListResponseEntity?) {
                        t?.list?.let {
                            mvpView?.onLoadCompanyListSuccess(it)
                        }
                    }
                })
    }

    private fun getSortPinyinCityList(response: ArrayList<out CityVM>): ArrayList<CityVM> {
        val map = TreeMap<String, CityVM>()
        for (ele in response) {
            val pinyin = Pinyin.toPinyin(ele.cityShowName(), "")
            ele.initCityPinyin(pinyin)
            map[pinyin] = ele
        }
        return ArrayList(map.values)
    }

}
