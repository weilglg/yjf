package com.cn.lib.retrofit.network.entity

import java.io.File
import java.io.InputStream
import java.net.URLConnection
import java.util.ArrayList
import java.util.HashMap

import okhttp3.MediaType

class HttpParamEntity {
    val paramMap = HashMap<String, String>()
    val bodyparamMap = HashMap<String, String>()
    val fileMap = HashMap<String, MutableList<FileEntity<*>>>()

    fun isParamsEmpty(): Boolean {
        return paramMap.isEmpty()
    }

    fun isBodyssEmpty(): Boolean {
        return bodyparamMap.isEmpty()
    }

    fun isFilesEmpty(): Boolean {
        return fileMap.isEmpty()
    }

    fun put(paramEntity: HttpParamEntity?) {
        if (paramEntity != null) {
            if (!paramEntity.paramMap.isEmpty()) {
                paramMap.putAll(paramEntity.paramMap)
            }
            if (paramEntity.fileMap.isEmpty()) {
                fileMap.putAll(paramEntity.fileMap)
            }
        }
    }

    fun param(params: Map<String, String>?) {
        if (params != null && params.isEmpty()) {
            paramMap.putAll(params)
        }
    }

    fun param(key: String, value: String) {
        paramMap[key] = value
    }

    fun body(params: Map<String, String>?) {
        if (params != null && params.isEmpty()) {
            bodyparamMap.putAll(params)
        }
    }

    fun body(key: String, value: String) {
        bodyparamMap[key] = value
    }

    fun put(fileMap: MutableMap<String, FileEntity<*>>?) {
        if (fileMap != null && !fileMap.isEmpty()) {
            fileMap.putAll(fileMap)
        }
    }

    fun <T : File> put(key: String, file: T) {
        put(key, file, file.name)
    }

    fun <T : File> put(key: String, file: T, fileName: String) {
        put(key, fileName, file, getMediaTypeByName(fileName))
    }

    fun <T : InputStream> put(key: String, streamName: String, inputStream: T) {
        put(key, streamName, inputStream, getMediaTypeByName(streamName))
    }

    fun put(key: String, name: String, bytes: ByteArray) {
        put(key, name, bytes, getMediaTypeByName(name))
    }

    fun <T> put(key: String, fileName: String, data: T, mediaType: MediaType?) {
        val fileEntity = FileEntity(data, fileName, mediaType)
        put(key, fileEntity)
    }

    fun put(key: String, fileEntity: FileEntity<*>) {
        if (!fileMap.containsKey(key)) {
            fileMap[key] = mutableListOf()
        }
        val mutableList = fileMap[key]
        if (mutableList != null) {
            mutableList.add(fileEntity)
        }
    }

    fun put(key: String, fileWrappers: List<FileEntity<*>>) {
        if (!fileMap.containsKey(key)) {
            fileMap[key] = ArrayList()
        }
        fileMap[key]?.addAll(fileWrappers)
    }

    private fun getMediaTypeByName(name: String): MediaType? {
        var fileName = name
        val fileNameMap = URLConnection.getFileNameMap()
        fileName = fileName.replace("#", "")   //解决文件名中含有#号异常的问题
        var contentType: String? = fileNameMap.getContentTypeFor(fileName)
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return MediaType.parse(contentType)
    }

}
