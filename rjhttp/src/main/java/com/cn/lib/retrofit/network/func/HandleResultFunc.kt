package com.cn.lib.retrofit.network.func

import android.text.TextUtils
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.entity.ApiResultEntity
import com.cn.lib.retrofit.network.exception.ServerException
import io.reactivex.Observable
import io.reactivex.functions.Function


class HandleResultFunc<T> : Function<ApiResultEntity<T>, Observable<Optional<T>>> {

    @Throws(Exception::class)
    override fun apply(resultEntity: ApiResultEntity<T>): Observable<Optional<T>> {
        if (resultEntity.isOk) {
            return Observable.just(Optional(resultEntity.data))
        }
        val errorMsg = if (resultEntity.msg == null || TextUtils.isEmpty(resultEntity.msg)) "请求失败" else resultEntity.msg
        throw ServerException(resultEntity.code, errorMsg)
    }
}
