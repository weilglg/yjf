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

import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio


class ProgressRequestBody(private val mRequestBody: RequestBody, private val progressListener: ResultProgressCallback<*>?, private val tag: Any) : RequestBody() {

    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (progressListener == null) {
            mRequestBody.writeTo(sink)
            return
        }
        val progressOutputStream = ProgressOutputStream(sink.outputStream(), progressListener, contentLength(), tag)
        val progressSink = Okio.buffer(Okio.sink(progressOutputStream))
        mRequestBody.writeTo(progressSink)
        progressSink.flush()
    }

}