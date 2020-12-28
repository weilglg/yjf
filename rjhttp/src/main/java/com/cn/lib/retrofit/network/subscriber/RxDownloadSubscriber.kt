package com.cn.lib.retrofit.network.subscriber

import android.content.Context
import android.text.TextUtils
import com.cn.lib.retrofit.network.callback.DownloadProgressCallBack
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.exception.ExceptionFactory
import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.util.MimeUtils
import com.cn.lib.retrofit.network.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import java.io.*
import java.lang.ref.WeakReference
import java.util.regex.Pattern

class RxDownloadSubscriber<ResponseBody : okhttp3.ResponseBody>(private val mTag: Any, private val mUrl: String, mContext: Context, private val mSavePath: String? = "", private val mSaveName: String? = "", private val mCallback: DownloadProgressCallBack?) : BaseSubscriber<ResponseBody>() {

    init {
        contextWeakReference = WeakReference(mContext)
    }

    override fun onStart() {
        super.onStart()
        mCallback?.onStart(mTag)
    }

    override fun onNext(result: ResponseBody) {
        super.onNext(result)
        writeResponseBodyToDisk(result, mTag, mSaveName, mSavePath, mCallback)
    }


    private fun writeResponseBodyToDisk(body: ResponseBody, mTag: Any, fileName: String?, filePath: String?, mCallback: DownloadProgressCallBack?) {
        var fileName = fileName
        var filePath = filePath
        Util.checkNotNull<Any>(body, "ResponseBody is null")
        fileName = getFileName(fileName, mUrl, body.contentType())
        LogUtil.i("download", "fileName = $fileName")
        if (TextUtils.isEmpty(filePath)) {
            filePath = "${contextWeakReference.get()?.getExternalFilesDir(null)}${File.separator}Downloads${File.separator}${fileName}"
        } else {
            val file = File(filePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            filePath = filePath + File.separator + fileName
            filePath = filePath.replace("//".toRegex(), "/")
        }
        LogUtil.i("download", "filePath = $filePath")
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }

            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0
            var updateCount = 0
            // 定义一次读取的大小
            val fileReader = ByteArray(1024 * 4)
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            // 向指定文件写入数据
            while (true) {
                val read = inputStream!!.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream?.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
                val progress: Float
                if (fileSize == -1L || fileSize == 0L) {
                    progress = 100f
                } else {
                    progress = (fileSizeDownloaded * 100 / fileSize).toFloat()
                }
                if (updateCount == 0 || progress >= updateCount) {
                    updateCount += 1
                    mCallback?.run {
                        Observable.just(fileSizeDownloaded)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { fileSizeDownloaded -> onProgress(mTag, fileSizeDownloaded!!, fileSize, progress) }
                    }
                }
            }
            outputStream?.flush()
            mCallback?.run {
                Observable.just(filePath)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { path -> onSuccess(mTag, path) }
            }
        } catch (e: IOException) {
            finalOnError(e)
        } finally {
            try {
                outputStream?.close()

                inputStream?.close()
            } catch (e: IOException) {
                finalOnError(e)
            }

        }

    }

    override fun onError(throwable: ApiThrowable) {
        mCallback?.onError(mTag, throwable)
    }

    override fun onComplete() {
        mCallback?.onCompleted(mTag)
    }

    private fun finalOnError(e: Exception) {
        if (mCallback != null)
            Observable.just(e)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { e -> ExceptionFactory.handleException(e) }.subscribe({ apiThrowable -> mCallback.onError(mTag, apiThrowable) }, { throwable -> mCallback.onError(mTag, ExceptionFactory.handleException(throwable)) })
    }

    private fun getFileSuffixByUrl(url: String): String? {
        val fileName = getFileNameByUrl(url)
        // 从路径中获取
        return if (fileName != null && "" != fileName && fileName.contains(".")) {
            fileName.substring(fileName.lastIndexOf("."))
        } else null
    }

    private fun getFileNameByUrl(url: String): String {
        val urlReg = "^((ht|f)tps?):\\/\\/([\\w\\-]+(\\.[\\w\\-]+)*\\/)*[\\w\\-]+(\\.[\\w\\-]+)*\\/?(\\?([\\w\\-\\.,@?^=%&:\\/~\\+#]*)+)?"
        var pattern = Pattern.compile(urlReg)
        var matcher = pattern.matcher(url)
        if (matcher.matches()) {
            val nameReg = "([^<>/\\\\\\|:\\''\\*\\?\\&\\=]+)((.(w+)\\?)|(.(\\w+)$))"
            pattern = Pattern.compile(nameReg)
            matcher = pattern.matcher(url)
            if (matcher.find()) {
                return matcher.group()
            }
        }
        return ""
    }

    private fun getFileName(saveName: String?, url: String, mediaType: MediaType?): String? {
        var fileName: String? = saveName
        if (TextUtils.isEmpty(fileName)) { // 如果没有配置文件名称，则从Url中解析文件名称
            fileName = getFileNameByUrl(url)
        }
        if (TextUtils.isEmpty(fileName)) { // 如果从Url中没有解析到文件名称，则使用时间戳作为文件名称
            fileName = System.currentTimeMillis().toString()
        }
        fileName?.contains(".").run {
            // 从Url中获取文件类型
            var fileSuffix: String? = getFileSuffixByUrl(url)
            mediaType?.let {
                // 从Content-Type中获取文件后缀名
                if (TextUtils.isEmpty(fileSuffix)) {
                    fileSuffix = "." + MimeUtils.guessExtensionFromMimeType(it.toString())
                }
                // 如果前面中都没有，则直接一类型作为后缀名
                if (TextUtils.isEmpty(fileSuffix)) {
                    val subtype = it.subtype()
                    if (!TextUtils.isEmpty(subtype)) {
                        fileSuffix = ".$subtype"
                    }
                }
            }
            //如果前面的都没获取到，则使用默认后缀名
            if (TextUtils.isEmpty(fileSuffix)) {
                fileSuffix = ".tmpl"
            }
            fileName = fileSuffix?.let {
                // 拼接文件名称（名称+后缀）
                return@let fileName + fileSuffix
            }
        }
        return fileName
    }

}
