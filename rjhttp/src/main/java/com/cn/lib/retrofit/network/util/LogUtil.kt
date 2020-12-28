package com.cn.lib.retrofit.network.util

import android.os.Environment
import android.util.Log
import com.cn.lib.BuildConfig
import java.io.*

object LogUtil {
    /**
     * @return is BuildConfig.DEBUG
     */
    /**
     * 设置debug开关
     *
     * @param aDebug true打开，false关闭
     */
    var isDebug = BuildConfig.DEBUG
    private val LOG_TAG = "RxHttp"
    val DEBUG_DEBUG = true
    val DEBUG_ERROR = true
    val DEBUG_PERFORMENCE = true
    val DEBUG_INFO = true
    val DEBUG_VERBOSE = true
    val DEBUG_WARN = true
    val DEBUG_EXCEPT = true
    private var mOutfilestream: FileOutputStream? = null
    private var mLogcaOutfilestream: FileOutputStream? = null
    private var mIsLogToFile = false
    private val mFolderName = (Environment.getExternalStorageDirectory().toString() + File.separator + "Tamic"
            + File.separator + "down" + File.separator + "log" + File.separator)
    private val mLogFileName = mFolderName + "fastDownlaoder_log.txt"
    private val mLogFileNameLogcat = mFolderName + "fastDownlaoder_lasttime_log.txt"

    /**
     * LogLevel
     */
    private enum class LogLevel {
        /**
         * DEBUG Level
         */
        DEBUG,
        /**
         * ERROR Level
         */
        ERROR,
        /**
         * INFO Level
         */
        INFO,
        /**
         * VERBOSE Level
         */
        VERBOSE,
        /**
         * WARN Level
         */
        WARN
    }


    fun d(aTag: String, aMessage: String) {
        if (isDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, aTag, aMessage, null)
        }
    }


    fun d(aMessage: String) {
        if (isDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, null)
        }
    }


    fun d(aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, aThrow)
        }
    }


    fun d(aTag: String, aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, aTag, aMessage, aThrow)
        }
    }


    fun p(aMessage: String) {
        if (isDebug && DEBUG_PERFORMENCE) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, null)
        }
    }


    fun p(aTag: String, aMessage: String) {
        if (isDebug && DEBUG_PERFORMENCE) {
            doLog(LogLevel.DEBUG, aTag, aMessage, null)
        }
    }


    fun e(aTag: String, aMessage: String?) {
        if (DEBUG_ERROR) {
            aMessage?.let {
                doLog(LogLevel.ERROR, aTag, it, null)
            }
        }
    }

    fun e(aMessage: String?) {
        //Debug版Release版都会打印
        if (DEBUG_ERROR) {
            aMessage?.let {
                doLog(LogLevel.ERROR, LOG_TAG, it, null)
            }
        }
    }


    fun e(aMessage: String, aThrow: Throwable) {
        if (DEBUG_ERROR) {
            doLog(LogLevel.ERROR, LOG_TAG, aMessage, aThrow)
        }
    }


    fun i(aTag: String, aMessage: String) {
        if (isDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, aTag, aMessage, null)
        }
    }


    fun i(aMessage: String) {
        if (isDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, LOG_TAG, aMessage, null)
        }
    }

    fun i(aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, LOG_TAG, aMessage, aThrow)
        }
    }


    fun v(aTAG: String, aMessage: String?) {
        if (isDebug && DEBUG_VERBOSE) {
            aMessage?.let {
                doLog(LogLevel.VERBOSE, aTAG, it, null)
            }
        }
    }


    fun v(aMessage: String) {
        if (isDebug && DEBUG_VERBOSE) {
            doLog(LogLevel.VERBOSE, LOG_TAG, aMessage, null)
        }
    }


    fun v(aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_VERBOSE) {
            doLog(LogLevel.VERBOSE, LOG_TAG, aMessage, aThrow)
        }
    }


    fun w(aMessage: String) {
        if (isDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, LOG_TAG, aMessage, null)
        }
    }

    fun w(aTag: String, aMessage: String) {
        if (isDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, aTag, aMessage, null)
        }
    }


    fun w(aTag: String, aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, aTag, aMessage, aThrow)
        }
    }


    fun w(aMessage: String, aThrow: Throwable) {
        if (isDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, LOG_TAG, aMessage, aThrow)
        }
    }


    fun printStackTrace(aException: Exception) {
        if (isDebug && DEBUG_EXCEPT) {
            aException.printStackTrace()
        }
    }


    private fun doLog(aLevel: LogLevel, aTag: String, aMessage: String, aThrow: Throwable?) {
        when (aLevel) {
            LogUtil.LogLevel.DEBUG -> if (aThrow == null) {
                Log.d(aTag, aMessage)
            } else {
                Log.d(aTag, aMessage, aThrow)
            }
            LogUtil.LogLevel.ERROR -> if (aThrow == null) {
                Log.e(aTag, aMessage)
            } else {
                Log.e(aTag, aMessage, aThrow)
            }
            LogUtil.LogLevel.INFO -> if (aThrow == null) {
                Log.i(aTag, aMessage)
            } else {
                Log.i(aTag, aMessage, aThrow)
            }
            LogUtil.LogLevel.VERBOSE -> if (aThrow == null) {
                Log.v(aTag, aMessage)
            } else {
                Log.v(aTag, aMessage, aThrow)
            }
            LogUtil.LogLevel.WARN -> if (aThrow == null) {
                Log.w(aTag, aMessage)
            } else {
                Log.w(aTag, aMessage, aThrow)
            }
        }

        if (mIsLogToFile) {
            flushToFile(aTag, aMessage)
        }

    }


    fun dumpLogcat() {

        var localBufferedReader: BufferedReader? = null
        try {

            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                return
            }

            val folder = File(mFolderName)

            if (!folder.exists()) {
                folder.mkdirs()
                //  myFile.createNewFile();
            }

            if (null == mLogcaOutfilestream) {
                mLogcaOutfilestream = FileOutputStream(mLogFileNameLogcat)
            }

            val localProcess = Runtime.getRuntime().exec("logcat -v time -d")
            val localInputStream = localProcess.inputStream
            val localInputStreamReader = InputStreamReader(localInputStream)
            localBufferedReader = BufferedReader(localInputStreamReader)

            var str1: String? = localBufferedReader.readLine()
            while (str1 != null) {
                mLogcaOutfilestream!!.write(str1.toByteArray(charset("UTF-8")))
                mLogcaOutfilestream!!.write("\n".toByteArray())
                str1 = localBufferedReader
                        .readLine()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                localBufferedReader?.close()
                if (mLogcaOutfilestream != null) {
                    mLogcaOutfilestream!!.close()
                    mLogcaOutfilestream = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }


    private fun flushToFile(aTag: String, aMessage: String) {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return
        }

        try {

            val folder = File(mFolderName)

            if (!folder.exists()) {
                folder.mkdirs()
                //  myFile.createNewFile();
            }

            if (null == mOutfilestream) {
                mOutfilestream = FileOutputStream(mLogFileName)
            }
            val output = "$aTag : $aMessage"
            mOutfilestream!!.write(output.toByteArray(charset("UTF-8")))
            mOutfilestream!!.write("\n".toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setWriteToFile(bWritetoFile: Boolean) {
        mIsLogToFile = bWritetoFile
    }


    fun logException(aTag: String, aException: Exception?) {
        try {
            if (null == aException) {
                return
            }

            if (isDebug) {
                aException.printStackTrace()
            }

            LogUtil.d(aTag, "========================= Exception Happened !!================================")

            LogUtil.d(aTag, aException.message!!)
            val stack = aException.stackTrace
            //			if (null == stack) { //FindBugs
            //				return;
            //			}
            for (i in stack.indices) {
                LogUtil.d(aTag, stack[i].toString())

            }

            LogUtil.d(aTag, "========================= Exception Ended !!================================")

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    fun printInvokeTrace(aTag: String) {
        val stackTrace = Throwable().stackTrace
        for (i in 1 until stackTrace.size) {
            LogUtil.d(aTag + ":  " + stackTrace[i].toString())
        }
    }

    fun printInvokeTrace(aTag: String, aMax: Int) {
        val stackTrace = Throwable().stackTrace
        val n = Math.min(aMax, stackTrace.size)
        for (i in 1 until n) {
            LogUtil.d(aTag + ":  " + stackTrace[i].toString())
        }
    }
}
/**
 * Constructor
 */
