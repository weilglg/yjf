package cn.ygyg.cloudpayment

import android.util.Log
import cn.ygyg.cloudpayment.net.RequestManager
import com.cn.lib.basic.BaseActivity
import com.cn.lib.retrofit.network.callback.ResultCallback
import com.cn.lib.retrofit.network.exception.ApiThrowable
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity: BaseActivity() {
    override fun getContentViewResId(): Int = R.layout.activity_test
    override fun initListener() {
        super.initListener()
        btn.setOnClickListener {
            RequestManager.get("test")
                    .baseUrl("http://10.2.155.79:7001")
                    .cancelEncryption(false)
                    .execute("encryption",object : ResultCallback<String>(){
                        override fun onStart(tag: Any?) {

                        }

                        override fun onCompleted(tag: Any?) {

                        }

                        override fun onError(tag: Any?, e: ApiThrowable) {

                        }

                        override fun onSuccess(tag: Any?, result: String?) {
                            Log.e("TAG","========encryption==========$result")
                        }
                    })

        }
    }
}