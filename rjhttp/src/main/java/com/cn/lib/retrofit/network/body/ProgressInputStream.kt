package com.cn.lib.retrofit.network.body


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

import com.cn.lib.retrofit.network.callback.ResultProgressCallback

import java.io.IOException
import java.io.InputStream

/**
 * 带进度的输入流
 */
class ProgressInputStream internal constructor(private val stream: InputStream?, private val listener: ResultProgressCallback<*>, private val total: Long, private val tag: Any) : InputStream() {
    private var totalRead: Long = 0
    internal var started: Boolean = false
    internal var lastRefreshTime = 0L
    internal var lastBytesWritten = 0L
    internal var minTime = 100//最小回调时间100ms，避免频繁回调

    private var mHandler: Handler? = null

    private fun ensureHandler() {
        if (mHandler != null) {
            return
        }
        synchronized(ResultProgressCallback::class.java) {
            if (mHandler == null) {
                mHandler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message?) {
                        if (msg == null) {
                            return
                        }
                        when (msg.what) {
                            WHAT_START -> {
                                val startData = msg.data ?: return
                                listener.onUIProgressStart(tag, startData.getLong(TOTAL_BYTES))
                            }
                            WHAT_UPDATE -> {
                                val updateData = msg.data ?: return
                                val numBytes = updateData.getLong(CURRENT_BYTES)
                                val totalBytes = updateData.getLong(TOTAL_BYTES)
                                val percent = updateData.getFloat(PERCENT)
                                val speed = updateData.getFloat(SPEED)
                                listener.onUIProgressChanged(tag, numBytes, totalBytes, percent, speed)
                            }
                            WHAT_FINISH -> listener.onUIProgressFinish(tag)
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        val read = this.stream?.read() ?: 0
        if (this.total < 0) {
            onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            this.totalRead++
            onProgressChanged(this.totalRead, this.total, this.totalRead * 1.0f / this.total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = this.stream?.read(b, off, len) ?: 0
        if (this.total < 0) {
            onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            this.totalRead += read.toLong()
            onProgressChanged(this.totalRead, this.total, this.totalRead * 1.0f / this.total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun close() {
        this.stream?.close()
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     */
    fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float) {
        if (!started) {
            onProgressStart(totalBytes)
            started = true
        }
        if (numBytes == -1L && totalBytes == -1L && percent == -1f) {
            onProgressChanged(-1, -1, -1f, -1f)
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime >= minTime || numBytes == totalBytes || percent >= 1f) {
            var intervalTime = currentTime - lastRefreshTime
            if (intervalTime == 0L) {
                intervalTime += 1
            }
            val updateBytes = numBytes - lastBytesWritten
            val networkSpeed = updateBytes / intervalTime
            onProgressChanged(numBytes, totalBytes, percent, networkSpeed.toFloat())
            lastRefreshTime = System.currentTimeMillis()
            lastBytesWritten = numBytes
        }
        if (numBytes == totalBytes || percent >= 1f) {
            onProgressFinish()
        }
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    private fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onUIProgressChanged(tag, numBytes, totalBytes, percent, speed)
            return
        }
        ensureHandler()
        mHandler?.run {
            val message = obtainMessage()
            message.what = WHAT_UPDATE
            val data = Bundle()
            data.putLong(CURRENT_BYTES, numBytes)
            data.putLong(TOTAL_BYTES, totalBytes)
            data.putFloat(PERCENT, percent)
            data.putFloat(SPEED, speed)
            message.data = data
           sendMessage(message)
        }

    }

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    private fun onProgressStart(totalBytes: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onUIProgressStart(tag, totalBytes)
            return
        }
        ensureHandler()
        mHandler?.run {
            val message = obtainMessage()
            message.what = WHAT_START
            val data = Bundle()
            data.putLong(TOTAL_BYTES, totalBytes)
            message.data = data
            sendMessage(message)
        }
    }

    /**
     * 进度结束
     */
    private fun onProgressFinish() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onUIProgressFinish(tag)
            return
        }
        ensureHandler()
        mHandler?.run {
            val message = obtainMessage()
            message.what = WHAT_FINISH
            sendMessage(message)
        }
    }

    companion object {
        private const val WHAT_START = 0x01
        private const val WHAT_UPDATE = 0x02
        private const val WHAT_FINISH = 0x03
        private const val CURRENT_BYTES = "numBytes"
        private const val TOTAL_BYTES = "totalBytes"
        private const val PERCENT = "percent"
        private const val SPEED = "speed"
    }

}