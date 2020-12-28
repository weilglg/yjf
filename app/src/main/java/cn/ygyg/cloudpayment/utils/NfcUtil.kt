package cn.ygyg.cloudpayment.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.Log
import cn.ygyg.cloudpayment.dialog.DefaultPromptDialog

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object NfcUtil {

    private var mNfcAdapter: NfcAdapter? = null
    var mPendingIntent: PendingIntent? = null
    var mIntentFilter: Array<IntentFilter>? = null
    var mTechList: Array<Array<String>>? = null
    var mContext: Context? = null

    /**
     * 检查 NFC 是否打开
     * @return
     */
    val isEnabled: Boolean get() = mNfcAdapter!!.isEnabled

    /**
     * 获取 adapter
     * @param context
     * @return
     */
    fun getNfcAdapter(context: Context): NfcAdapter? {
        val mNFCManager = context.applicationContext.getSystemService(Context.NFC_SERVICE) as NfcManager
        mNfcAdapter = mNFCManager.defaultAdapter
        return mNfcAdapter
    }

    /**
     * 检查 当前系统是否支持 NFC
     * @return
     * @param activity
     */
    fun checkNFC(context: Context):Boolean{
        this.mContext=context
        if(getNfcAdapter(context)!=null){
            if(isEnabled) return true else {
                nfcCanOpen()
                return false
            }
        }else{
            nfcNonsupport()
            return false
        }
    }

    /**
     * 初始化nfc设置
     */
    fun NfcInit(activity: Activity) {

        //获取通知
        mPendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        val filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filter2 = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        try {
            filter.addDataType("*/*")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            e.printStackTrace()
        }

        mIntentFilter = arrayOf(filter, filter2)
        mTechList = null
    }

    /**
     * open NFC
     */
    fun enable() {
        try {
            val method = mNfcAdapter!!.javaClass.getDeclaredMethod("enable")
            method.invoke(mNfcAdapter)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    /**
     * close NFC
     */
    fun disable() {
        try {
            val method = mNfcAdapter!!.javaClass.getDeclaredMethod("disable")
            method.invoke(mNfcAdapter)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    /**
     *  支持nfc但未打开
     */
    fun nfcCanOpen() {
        //直接去NFC Setting页面去设置  打开nfc
        DefaultPromptDialog.builder()
                .setContext(mContext!!)
                .setButtonOrientation(DefaultPromptDialog.TypeEnum.BUTTON_HORIZONTAL)
                .setContentText("NFC尚未开启，是否去开启NFC？")
                .setAffirmText("确认")
                .setCancelText("取消")
                .onPromptDialogButtonListener(object : DefaultPromptDialog.DefaultPromptDialogButtonListener() {
                    override fun clickPositiveButton(dialog: DefaultPromptDialog): Boolean {
                        mContext!!.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        return super.clickPositiveButton(dialog)
                    }
                })
                .build()
                .show()
    }

    /**
     * 当前系统不支持NFC
     */
    fun nfcNonsupport() {
        DefaultPromptDialog.builder()
                .setContext(mContext!!)
                .setAffirmText("我知道了")
                .setContentText("当前系统不支持NFC")
                .build()
                .show()
    }
}
