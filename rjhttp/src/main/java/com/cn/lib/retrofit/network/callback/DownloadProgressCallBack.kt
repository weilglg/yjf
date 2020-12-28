package com.cn.lib.retrofit.network.callback

abstract class DownloadProgressCallBack : ResultCallback<String>() {

    /**
     * 下载进度回调
     *
     * @param tag       标识
     * @param bytesRead 下载的大小
     * @param fileSize  文件大小
     * @param progress  下载进度
     */
    abstract fun onProgress(tag: Any?, bytesRead: Long, fileSize: Long, progress: Float)


    /**
     * 下载完成的回调
     *
     * @param tag      标识
     * @param filePath 存储的文件路径
     */
    abstract override fun onSuccess(tag: Any?, filePath: String?)

    override fun onCompleted(tag: Any?) {

    }

}
