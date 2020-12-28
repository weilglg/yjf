package com.cn.lib.retrofit.network.entity

import java.io.File

import okhttp3.MediaType

class FileEntity<T>(var data: T, var fileName: String, var mediaType: MediaType?) {
    var fileSize: Long = 0

    init {
        if (data is File) {
            this.fileSize = (data as File).length()
        } else if (data is ByteArray) {
            this.fileSize = (data as ByteArray).size.toLong()
        }
    }
}
