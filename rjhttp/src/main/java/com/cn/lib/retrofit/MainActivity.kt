package com.cn.lib.retrofit

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast

import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
import com.cn.lib.R
import com.cn.lib.retrofit.network.Demo
import com.cn.lib.retrofit.network.RxHttp
import com.cn.lib.retrofit.network.callback.DownloadProgressCallBack
import com.cn.lib.retrofit.network.entity.UploadFileType
import com.cn.lib.retrofit.network.callback.ResponseTemplateCallback
import com.cn.lib.retrofit.network.proxy.ResultCallbackProxy
import com.cn.lib.retrofit.network.callback.ResultProgressCallback
import com.cn.lib.retrofit.network.config.Optional
import com.cn.lib.retrofit.network.config.ResultConfigLoader
import com.cn.lib.retrofit.network.exception.ApiThrowable
import com.cn.lib.retrofit.network.request.CommPostRequest
import com.cn.lib.retrofit.network.request.UploadRequest
import com.cn.lib.retrofit.network.util.LogUtil
import com.cn.lib.retrofit.network.util.TestApi

import java.io.File

import io.reactivex.observers.DisposableObserver
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import com.cn.lib.retrofit.network.entity.CommResultEntity

class MainActivity : AppCompatActivity() {

    private val util: RxHttp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo)
        ResultConfigLoader.init(baseContext)
        RxHttp.INSTANCE.init(baseContext)
                .baseUrl("https://ygzk.ygego.cn/api/")
                .isLog(true)
                .callAdapterFactory(RxJava2CallAdapterFactory.create())
                .converterFactory(Retrofit2ConverterFactory())
        findViewById<View>(R.id.tv).setOnClickListener { post() }
        findViewById<View>(R.id.tv2).setOnClickListener {
            val param = JSONObject()
            param["pageSize"] = 1
            param["pageNum"] = 10
            RxHttp.templatePost("home/hotnews")
                    .jsonObj(param)
                    .execute(String::class.java)
                    .subscribeWith(object : DisposableObserver<Optional<String>>() {
                        override fun onNext(t: Optional<String>) {

                        }

                        override fun onError(e: Throwable) {
                            LogUtil.e("MainActivity", "onError=$e")
                        }

                        override fun onComplete() {

                        }
                    })
        }
    }

    private fun post() {
        val param = JSONObject()
        param["pageSize"] = 1
        param["pageNum"] = 10

        RxHttp.templatePost("home/hotnews")
                .addHeader("11", "2222")
                .addHeader("22", "2222")
                .addHeader("33", "2222")
                .addHeader("44", "2222")
                .addHeader("55", "2222")
                .addHeader("66", "2222")
                .addHeader("77", "2222")
                .jsonObj(param)
                .execute("CCCC", object : ResponseTemplateCallback<Demo<List<String>>>() {

                    override fun onSuccess(tag: Any?, result: Demo<List<String>>?) {
                        LogUtil.e("MainActivity", "result=$result")
                    }

                    override fun checkSuccessCode(code: String, msg: String): Boolean {
                        return code == "0"
                    }

                    override fun onStart(tag: Any?) {
                        LogUtil.e("MainActivity", "onStart")
                    }

                    override fun onCompleted(tag: Any?) {

                    }

                    override fun onError(tag: Any?, throwable: ApiThrowable) {
                        Log.e("tag", "onError=" + throwable.message)
                    }
                })


        //                .execute("cccc", new ResponseGenericsCallback<String>() {
        //                    @Override
        //                    public void onError(Object tag, ApiThrowable throwable) {
        //                        LogUtil.e("MainActivity", "throwable=" + throwable.toString());
        //                    }
        //
        //                    @Override
        //                    public void onSuccess(Object tag, String result) {
        //                        LogUtil.e("MainActivity", "result=" + result);
        //                    }
        //                });
    }


    fun post_2(v: View) {
        val param = JSONObject()
        param["pageSize"] = 1
        param["pageNum"] = 10

        RxHttp.resultPost("home/hotnews")
                .jsonObj(param)
                .execute("resultPost", object : ResultCallbackProxy<TestApi<String>, String>(object : ResultProgressCallback<String>() {
                    override fun onUIProgressChanged(mTag: Any?, numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
                        Toast.makeText(this@MainActivity, "numBytes=$numBytes  totalBytes=$totalBytes  percent=$percent  speed=$speed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onStart(tag: Any?) {
                        Log.e("tag", "onStart")
                    }

                    override fun onCompleted(tag: Any?) {
                        Log.e("tag", "onCompleted")
                    }

                    override fun onError(tag: Any?, throwable: ApiThrowable) {
                        Log.e("tag", "onError=" + throwable.message)
                    }

                    override fun onSuccess(tag: Any?, s: String?) {
                        Log.e("tag", "onSuccess=$s")
                    }
                }) {

                })
        //                .execute("resultPost", new ResultCallback<String>() {
        //                    @Override
        //                    public void onStart(Object tag) {
        //                        Log.e("tag", "onCompleted");
        //                    }
        //
        //                    @Override
        //                    public void onCompleted(Object tag) {
        //                        Log.e("tag", "onCompleted");
        //                    }
        //
        //                    @Override
        //                    public void onError(Object tag, ApiThrowable throwable) {
        //                        Log.e("tag", "onError=" + throwable.getMessage());
        //                    }
        //
        //                    @Override
        //                    public void onSuccess(Object tag, String s) {
        //                        Log.e("tag", "onSuccess=" + s);
        //                    }
        //                });
    }

    fun uploadPartFile(v: View) {
        val file = File(Environment.getExternalStorageDirectory().toString() +
                File.separator + "1.jpg")
        val file2 = File(Environment.getExternalStorageDirectory().toString() +
                File.separator + "2.jpg")
//        CommPostRequest("upload5273")
//                .param("appId", "27")
//                .baseUrl("http://business-workbench.qingtian.ygego.alpha3/rest/")
//                .uploadType(UploadFileType.PART_FROM)
//                .params("file", file)
////                .params("file", file2)
//                .execute("upload", object : ResultProgressCallback<String>() {
//                    override fun onStart(tag: Any?) {
//                        Toast.makeText(this@MainActivity, "开始上传", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onCompleted(tag: Any?) {
//
//                    }
//
//                    override fun onError(tag: Any?, e: ApiThrowable) {
//
//                    }
//
//                    override fun onSuccess(tag: Any?, s: String?) {
//                        Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onUIProgressChanged(tag: Any?, numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
//                        Toast.makeText(this@MainActivity, "numBytes=$numBytes  totalBytes=$totalBytes  percent=$percent  speed=$speed", Toast.LENGTH_SHORT).show()
//                    }
//                })
    }

    fun uploadBodyFile(v: View) {
        val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "1.jpg")
//        val file2 = File(Environment.getExternalStorageDirectory().toString() + File.separator + "3.jpg")
        val requestBody1 = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("appId", "27")
                .addFormDataPart("file", file.name, RequestBody.create(MediaType.parse("image/*"), file))
//                .addFormDataPart("file", file2.name, RequestBody.create(MediaType.parse("image/*"), file))
                .build()
//        UploadRequest("upload5273")
//                .baseUrl("http://business-workbench.qingtian.ygego.alpha3/rest/")
//                .uploadType(UploadFileType.BODY)
//                .requestBody(requestBody1)
//                .execute("upload", object : ResultCallbackProxy<CommResultEntity<String>, String>(object : ResultProgressCallback<String>() {
//                    override fun onStart(tag: Any?) {
//                        Toast.makeText(this@MainActivity, "开始上传", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onCompleted(tag: Any?) {
//
//                    }
//
//                    override fun onError(tag: Any?, e: ApiThrowable) {
//
//                    }
//
//                    override fun onSuccess(tag: Any?, s: String?) {
//                        Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onUIProgressChanged(tag: Any?, numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
//                        Toast.makeText(this@MainActivity, "numBytes=$numBytes  totalBytes=$totalBytes  percent=$percent  speed=$speed", Toast.LENGTH_SHORT).show()
//                    }
//                }) {})
    }

    fun downFile(v: View) {
        RxHttp.download("http://yun.ygego.cn/ygego/ygego.apk")
                .savePath(Environment.getExternalStorageDirectory().absolutePath)
                .execute("file", object : DownloadProgressCallBack() {

                    override fun onSuccess(tag: Any?, filePath: String?) {

                    }

                    override fun onProgress(tag: Any?, bytesRead: Long, fileSize: Long, progress: Float) {
                        Log.e("TAG", "bytesRead=$bytesRead fileSize=$fileSize progress=$progress")
                    }

                    override fun onStart(tag: Any?) {

                    }

                    override fun onError(tag: Any?, e: ApiThrowable) {
                        Log.e("TAG", "error=" + e.message)
                    }
                })
    }


}
