package com.cn.lib.retrofit.network.request

import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.proxy.ResultCallbackProxy
import com.cn.lib.retrofit.network.entity.CommResultEntity

import io.reactivex.disposables.Disposable

class CommPostRequest(url: String) : UploadRequest(url) {

//    override fun <T> execute(tag: Any, callback: ResultCallback<T>): Disposable {
//        return super.execute(tag, object : ResultCallbackProxy<CommResultEntity<T>, T>(callback) {
//
//        })
//    }

}
