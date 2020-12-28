/**
 * Copyright 2017 区长
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cn.lib.retrofit.network.body


import com.cn.lib.retrofit.network.callback.ResultProgressCallback

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Okio

/**
 * 带进度响应体
 */
class ProgressResponseBody(private val responseBody: ResponseBody, private val progressListener: ResultProgressCallback<*>?, private val tag: Any) : ResponseBody() {
    private var progressSource: BufferedSource? = null


    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }


    override fun contentLength(): Long {
        return responseBody.contentLength()
    }


    override fun source(): BufferedSource {
        if (progressListener == null) {

            return responseBody.source()
        }
        val progressInputStream = ProgressInputStream(responseBody.source().inputStream(), progressListener, contentLength(), tag)
        progressSource = Okio.buffer(Okio.source(progressInputStream))
        return progressSource as BufferedSource
    }

    override fun close() {
        try {
            progressSource?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}